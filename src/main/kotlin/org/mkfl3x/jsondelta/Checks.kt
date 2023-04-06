package org.mkfl3x.jsondelta

import com.google.gson.*

object Checks {

    fun checkJsonSyntax(json: String, name: String, context: DeltaContext) = try {
        JsonParser.parseString(json) is JsonElement
    } catch (e: JsonSyntaxException) {
        context.addMismatch(StructureMismatch("root", MismatchType.NOT_VALID_JSON, name))
        false
    }

    fun checkElementsType(field: String, expected: JsonElement, actual: JsonElement, ignoreNumberType: Boolean) =
        if (ignoreNumberType && isNumber(expected) && isNumber(actual))
            null
        else (getObjectType(expected) to getObjectType(actual)).let {
            if (it.first != it.second)
                ValueMismatch(field, MismatchType.TYPES_MISMATCH, it.first, it.second)
            else null
        }

    fun checkArraySizes(field: String, expected: JsonArray, actual: JsonArray) = if (expected.size() != actual.size())
        ValueMismatch(field, MismatchType.ARRAY_SIZE_MISMATCH, expected.size().toString(), actual.size().toString())
    else null

    fun checkMissedFields(field: String, expected: JsonObject, actual: JsonObject) =
        expected.keySet().subtract(actual.keySet()).let {
            if (it.isNotEmpty())
                ObjectMismatch(field, MismatchType.OBJECT_MISSED_FIELDS, it.toList())
            else null
        }

    fun checkExtraFields(field: String, expected: JsonObject, actual: JsonObject) =
        actual.keySet().subtract(expected.keySet()).let {
            if (it.isNotEmpty())
                ObjectMismatch(field, MismatchType.OBJECT_EXTRA_FIELDS, it.toList())
            else null
        }

    fun checkFieldValue(field: String, expected: JsonPrimitive, actual: JsonPrimitive) = if (expected != actual)
        ValueMismatch(field, MismatchType.VALUE_MISMATCH, expected.toString(), actual.toString())
    else null

    private fun getObjectType(value: JsonElement) = when {
        value.isJsonPrimitive -> getPrimitiveType(value.asJsonPrimitive)
        value.isJsonObject -> "object"
        value.isJsonArray -> "array"
        value.isJsonNull -> "null"
        else -> throw Exception("Unexpected object type")
    }

    private fun getPrimitiveType(value: JsonPrimitive) = when {
        value.isBoolean -> "boolean"
        value.isString -> "string"
        value.isNumber -> if (value.toString().contains(".")) "float" else "integer"
        else -> throw Exception("Unexpected primitive type")
    }

    private fun isNumber(element: JsonElement) = element.isJsonPrimitive && element.asJsonPrimitive.isNumber
}