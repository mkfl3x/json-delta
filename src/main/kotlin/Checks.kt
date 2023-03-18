import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive

object Checks {

    // TODO: refactor it
    fun checkTypes(expected: JsonElement, actual: JsonElement) = when {
        expected.isJsonArray && actual.isJsonArray.not() -> "array"
        expected.isJsonObject && actual.isJsonObject.not() -> "object"
        expected.isJsonNull && actual.isJsonNull.not() -> "null"
        expected.isJsonPrimitive && actual.isJsonPrimitive.not() -> "value"
        else -> null
    }?.let { ValueMismatch(MismatchType.OBJECT_TYPE_MISMATCH, it, getTypeName(actual)) }

    fun checkArraySizes(expected: JsonArray, actual: JsonArray) = if (expected.size() != actual.size())
        ValueMismatch(MismatchType.ARRAY_SIZE_MISMATCH, expected.size().toString(), actual.size().toString())
    else null

    fun checkMissedFields(expected: JsonObject, actual: JsonObject) = expected.keySet().subtract(actual.keySet()).let {
        if (it.isNotEmpty()) ObjectMismatch(MismatchType.MISSED_FIELDS, it.joinToString(", "))
        else null
    }

    fun checkExtraFields(expected: JsonObject, actual: JsonObject) = actual.keySet().subtract(expected.keySet()).let {
        if (it.isNotEmpty()) ObjectMismatch(MismatchType.EXTRA_FIELDS, it.joinToString(", "))
        else null
    }

    // TODO: refactor it
    fun checkFieldType(expected: JsonPrimitive, actual: JsonPrimitive) = when {
        expected.isString && actual.isString.not() -> "string"
        expected.isNumber && actual.isNumber.not() -> "number"
        expected.isBoolean && actual.isBoolean.not() -> "boolean"
        else -> null
    }?.let { ValueMismatch(MismatchType.VALUE_TYPE_MISMATCH, it, getPrimitiveType(actual)) }

    fun checkFieldValue(expected: JsonPrimitive, actual: JsonPrimitive) = if (expected != actual)
        ValueMismatch(MismatchType.VALUE_MISMATCH, expected.toString(), actual.toString())
    else null

    // TODO: refactor it
    private fun getTypeName(value: JsonElement) = when {
        value.isJsonArray -> "array"
        value.isJsonObject -> "object"
        value.isJsonNull -> "null"
        value.isJsonPrimitive -> "value"
        else -> throw Exception("Unexpected json type")
    }

    // TODO: refactor it
    private fun getPrimitiveType(value: JsonPrimitive) = when {
        value.isString -> "string"
        value.isNumber -> "number"
        value.isBoolean -> "boolean"
        else -> throw Exception("Unexpected primitive type")
    }
}