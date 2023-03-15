import com.google.gson.*

class JsonDelta {

    private val context = DeltaContext()

    fun compare(expected: String, actual: String): DeltaReport {
        try {
            validateJsonSyntax(expected, "expected")
            validateJsonSyntax(actual, "actual")
            comparisonResolver(JsonParser.parseString(expected), JsonParser.parseString(actual), "root")
        } finally {
            return DeltaReport(context.areEquals(), context.getMismatches())
        }
    }

    private fun validateJsonSyntax(json: String, name: String) {
        try {
            JsonParser.parseString(json)
        } catch (e: JsonSyntaxException) {
            context.addMismatch("root", FatalMismatch(MismatchType.NOT_VALID_JSON))
            throw Exception("'$name' json is not valid. ${e.message}")
        }
    }

    private fun comparisonResolver(expected: JsonElement, actual: JsonElement, fieldName: String) {
        checkTypes(expected, actual, fieldName)
        when (expected) {
            is JsonArray -> compareArrays(expected, actual.asJsonArray, fieldName)
            is JsonObject -> compareObjects(expected, actual.asJsonObject, fieldName)
            is JsonPrimitive -> compareFields(expected, actual.asJsonPrimitive, fieldName)
        }
    }

    private fun checkTypes(expected: JsonElement, actual: JsonElement, fieldName: String) {
        val expectedType = when {
            expected.isJsonArray && actual.isJsonArray.not() -> "array"
            expected.isJsonObject && actual.isJsonObject.not() -> "object"
            expected.isJsonNull && actual.isJsonNull.not() -> "null"
            expected.isJsonPrimitive && actual.isJsonPrimitive.not() -> "value"
            else -> return
        }
        context.addMismatch(
            fieldName,
            ValueMismatch(MismatchType.OBJECT_TYPE_MISMATCH, expectedType, getTypeName(actual))
        )
    }

    private fun getTypeName(value: JsonElement) = when {
        value.isJsonArray -> "array"
        value.isJsonObject -> "object"
        value.isJsonNull -> "null"
        value.isJsonPrimitive -> "value"
        else -> throw Exception("Unexpected json type")
    }

    private fun compareArrays(expected: JsonArray, actual: JsonArray, fieldName: String) {
        checkForArraySizes(expected, actual, fieldName)
        expected.forEachIndexed { i, _ -> comparisonResolver(expected[i], actual[i], "$fieldName[${i + 1}]") }
    }

    private fun checkForArraySizes(expected: JsonArray, actual: JsonArray, fieldName: String) {
        if (expected.size() != actual.size())
            context.addMismatch(
                fieldName,
                ValueMismatch(MismatchType.ARRAY_SIZE_MISMATCH, expected.size().toString(), actual.size().toString())
            )
    }

    private fun compareObjects(expected: JsonObject, actual: JsonObject, fieldName: String) {
        checkForMissedFields(expected, actual, fieldName)
        checkForUnexpectedFields(expected, actual, fieldName)
        expected.asMap().forEach {
            comparisonResolver(it.value, actual.get(it.key), "$fieldName.${it.key}")
        }
    }

    private fun checkForMissedFields(expected: JsonObject, actual: JsonObject, fieldName: String) {
        expected.keySet().forEach {
            if (it !in actual.keySet())
                context.addMismatch(fieldName, ObjectMismatch(MismatchType.MISSED_FIELD, it))
        }
    }

    private fun checkForUnexpectedFields(expected: JsonObject, actual: JsonObject, fieldName: String) {
        actual.keySet().forEach {
            if (it !in expected.keySet())
                context.addMismatch(fieldName, ObjectMismatch(MismatchType.EXTRA_FIELD, it))
        }
    }

    private fun compareFields(expected: JsonPrimitive, actual: JsonPrimitive, fieldName: String) {
        checkFieldsType(expected, actual, fieldName)
        checkFieldValue(expected, actual, fieldName)
    }

    private fun checkFieldsType(expected: JsonPrimitive, actual: JsonPrimitive, fieldName: String) {
        val expectedType = when {
            expected.isString && actual.isString.not() -> "string"
            expected.isNumber && actual.isNumber.not() -> "number"
            expected.isBoolean && actual.isBoolean.not() -> "boolean"
            else -> return
        }
        context.addMismatch(
            fieldName,
            ValueMismatch(MismatchType.VALUE_TYPE_MISMATCH, expectedType, getPrimitiveType(actual))
        )
    }

    private fun getPrimitiveType(value: JsonPrimitive) = when {
        value.isString -> "String"
        value.isNumber -> "Number"
        value.isBoolean -> "Boolean"
        else -> throw Exception("Unexpected primitive type")
    }

    private fun checkFieldValue(expected: JsonPrimitive, actual: JsonPrimitive, fieldName: String) {
        if (expected != actual)
            context.addMismatch(
                fieldName,
                ValueMismatch(MismatchType.VALUE_MISMATCH, expected.toString(), actual.toString())
            )
    }
}