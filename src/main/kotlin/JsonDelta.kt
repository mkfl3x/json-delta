import Checks.checkArraySizes
import Checks.checkExtraFields
import Checks.checkFieldType
import Checks.checkFieldValue
import Checks.checkJsonSyntax
import Checks.checkMissedFields
import Checks.checkTypes
import com.google.gson.*

class JsonDelta {

    fun compare(expected: String, actual: String, vararg ignoredFields: String): ComparisonReport =
        DeltaContext(ignoredFields.asList()).apply {
            if (!checkJsonSyntax(expected, "expected", this) || !checkJsonSyntax(actual, "actual", this))
                return@apply
            comparisonResolver(JsonParser.parseString(expected), JsonParser.parseString(actual), "root", this)
        }.getReport()

    private fun comparisonResolver(expected: JsonElement, actual: JsonElement, fieldName: String, report: DeltaContext) {
        if (fieldName in report.ignoredFields)
            return
        checkTypes(expected, actual)?.apply {
            report.addMismatch(fieldName, this)
            return
        }
        when (expected) {
            is JsonArray -> compareArrays(expected, actual.asJsonArray, fieldName, report)
            is JsonObject -> compareObjects(expected, actual.asJsonObject, fieldName, report)
            is JsonPrimitive -> compareFields(expected, actual.asJsonPrimitive, fieldName, report)
        }
    }

    private fun compareArrays(expected: JsonArray, actual: JsonArray, fieldName: String, report: DeltaContext) {
        checkArraySizes(expected, actual)?.apply {
            report.addMismatch(fieldName, this)
            return
        }
        expected.forEachIndexed { i, _ -> comparisonResolver(expected[i], actual[i], "$fieldName[${i + 1}]", report) }
    }

    private fun compareObjects(expected: JsonObject, actual: JsonObject, fieldName: String, report: DeltaContext) {
        checkMissedFields(expected, actual)?.apply {
            report.addMismatch(fieldName, this)
            return
        }
        checkExtraFields(expected, actual)?.apply {
            report.addMismatch(fieldName, this)
            return
        }
        expected.asMap().forEach { comparisonResolver(it.value, actual.get(it.key), "$fieldName.${it.key}", report) }
    }

    private fun compareFields(expected: JsonPrimitive, actual: JsonPrimitive, fieldName: String, report: DeltaContext) {
        report.addMismatch(fieldName, checkFieldType(expected, actual))
        report.addMismatch(fieldName, checkFieldValue(expected, actual))
    }
}