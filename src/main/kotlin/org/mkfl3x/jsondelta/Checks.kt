package org.mkfl3x.jsondelta

import com.google.gson.*

object Checks {

    fun checkJsonSyntax(json: String, name: String, context: DeltaContext) = try {
        JsonParser.parseString(json) is JsonElement
    } catch (e: JsonSyntaxException) {
        context.addMismatch("root", StructureMismatch(MismatchType.NOT_VALID_JSON))
        false
    }

    fun checkTypes(expected: JsonElement, actual: JsonElement) = if (expected.javaClass != actual.javaClass)
        ValueMismatch(MismatchType.OBJECT_TYPES_MISMATCH, expected.javaClass.simpleName, actual.javaClass.simpleName)
    else null

    fun checkArraySizes(expected: JsonArray, actual: JsonArray) = if (expected.size() != actual.size())
        ValueMismatch(MismatchType.ARRAY_SIZE_MISMATCH, expected.size().toString(), actual.size().toString())
    else null

    fun checkMissedFields(expected: JsonObject, actual: JsonObject) = expected.keySet().subtract(actual.keySet()).let {
        if (it.isNotEmpty())
            ObjectMismatch(MismatchType.OBJECT_MISSED_FIELDS, it.joinToString(", "))
        else null
    }

    fun checkExtraFields(expected: JsonObject, actual: JsonObject) = actual.keySet().subtract(expected.keySet()).let {
        if (it.isNotEmpty())
            ObjectMismatch(MismatchType.OBJECT_EXTRA_FIELDS, it.joinToString(", "))
        else null
    }

    fun checkFieldType(expected: JsonPrimitive, actual: JsonPrimitive) = if (
        expected.isString && actual.isString.not() ||
        expected.isNumber && actual.isNumber.not() ||
        expected.isBoolean && actual.isBoolean.not()
    ) ValueMismatch(MismatchType.VALUE_TYPE_MISMATCH, getPrimitiveType(expected), getPrimitiveType(actual))
    else null

    fun checkFieldValue(expected: JsonPrimitive, actual: JsonPrimitive) = if (expected != actual)
        ValueMismatch(MismatchType.VALUE_MISMATCH, expected.toString(), actual.toString())
    else null

    private fun getPrimitiveType(value: JsonPrimitive) = when {
        value.isString -> "string"
        value.isNumber -> "number"
        value.isBoolean -> "boolean"
        else -> throw Exception("Unexpected primitive type")
    }
}