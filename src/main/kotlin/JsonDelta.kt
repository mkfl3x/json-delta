import Checks.checkArraySizes
import Checks.checkExtraFields
import Checks.checkFieldType
import Checks.checkFieldValue
import Checks.checkMissedFields
import Checks.checkTypes
import com.google.gson.*

class JsonDelta {

    fun compare(expected: String, actual: String, vararg ignoredFields: String): DeltaReport {
        return DeltaReport(ignoredFields.asList()).apply {
            validateJsonSyntax(expected, "expected", this)
            validateJsonSyntax(actual, "actual", this)
            comparisonResolver(JsonParser.parseString(expected), JsonParser.parseString(actual), "root", this)
        }
    }

    private fun comparisonResolver(expected: JsonElement, actual: JsonElement, fieldName: String, report: DeltaReport) {
        if (fieldName in report.ignoredFields) return
        report.addMismatch(fieldName, checkTypes(expected, actual))
        when (expected) {
            is JsonArray -> compareArrays(expected, actual.asJsonArray, fieldName, report)
            is JsonObject -> compareObjects(expected, actual.asJsonObject, fieldName, report)
            is JsonPrimitive -> compareFields(expected, actual.asJsonPrimitive, fieldName, report)
        }
    }

    private fun compareArrays(expected: JsonArray, actual: JsonArray, fieldName: String, report: DeltaReport) {
        report.addMismatch(fieldName, checkArraySizes(expected, actual))
        expected.forEachIndexed { i, _ -> comparisonResolver(expected[i], actual[i], "$fieldName[${i + 1}]", report) }
    }

    private fun compareObjects(expected: JsonObject, actual: JsonObject, fieldName: String, report: DeltaReport) {
        report.addMismatch(fieldName, checkMissedFields(expected, actual))
        report.addMismatch(fieldName, checkExtraFields(expected, actual))
        expected.asMap()
            .forEach { comparisonResolver(it.value, actual.get(it.key) ?: return, "$fieldName.${it.key}", report) }
    }

    private fun compareFields(expected: JsonPrimitive, actual: JsonPrimitive, fieldName: String, report: DeltaReport) {
        report.addMismatch(fieldName, checkFieldType(expected, actual))
        report.addMismatch(fieldName, checkFieldValue(expected, actual))
    }

    // TODO: move from here
    private fun validateJsonSyntax(json: String, name: String, report: DeltaReport) {
        try {
            JsonParser.parseString(json)
        } catch (e: JsonSyntaxException) {
            report.addMismatch("root", FatalMismatch(MismatchType.NOT_VALID_JSON))
            throw Exception("'$name' json is not valid. ${e.message}")
        }
    }
}