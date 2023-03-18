import com.google.gson.*

class JsonDelta {

    // TODO: make it more elegance
    private val context = DeltaContext()

    fun compare(expected: String, actual: String, vararg ignoredFields: String): DeltaReport {
        context.setIgnoredFields(*ignoredFields)
        try {
            validateJsonSyntax(expected, "expected")
            validateJsonSyntax(actual, "actual")
            comparisonResolver(JsonParser.parseString(expected), JsonParser.parseString(actual), "root")
        } finally {
            return DeltaReport(context.areEquals(), context.getMismatches())
        }
    }

    private fun comparisonResolver(expected: JsonElement, actual: JsonElement, fieldName: String) {
        if (fieldName in context.getIgnoredFields()) return
        context.addMismatch(fieldName, Checker.checkTypes(expected, actual))
        when (expected) {
            is JsonArray -> compareArrays(expected, actual.asJsonArray, fieldName)
            is JsonObject -> compareObjects(expected, actual.asJsonObject, fieldName)
            is JsonPrimitive -> compareFields(expected, actual.asJsonPrimitive, fieldName)
        }
    }

    private fun compareArrays(expected: JsonArray, actual: JsonArray, fieldName: String) {
        context.addMismatch(fieldName, Checker.checkArraySizes(expected, actual))
        expected.forEachIndexed { i, _ -> comparisonResolver(expected[i], actual[i], "$fieldName[${i + 1}]") }
    }

    private fun compareObjects(expected: JsonObject, actual: JsonObject, fieldName: String) {
        context.addMismatch(fieldName, Checker.checkMissedFields(expected, actual))
        context.addMismatch(fieldName, Checker.checkExtraFields(expected, actual))
        expected.asMap().forEach { comparisonResolver(it.value, actual.get(it.key), "$fieldName.${it.key}") }
    }

    private fun compareFields(expected: JsonPrimitive, actual: JsonPrimitive, fieldName: String) {
        context.addMismatch(fieldName, Checker.checkFieldType(expected, actual))
        context.addMismatch(fieldName, Checker.checkFieldValue(expected, actual))
    }

    // TODO: move from here
    private fun validateJsonSyntax(json: String, name: String) {
        try {
            JsonParser.parseString(json)
        } catch (e: JsonSyntaxException) {
            context.addMismatch("root", FatalMismatch(MismatchType.NOT_VALID_JSON))
            throw Exception("'$name' json is not valid. ${e.message}")
        }
    }
}