package org.mkfl3x.jsondelta

import com.google.gson.*

object Checks {

    fun isJsonValid(json: String, name: String, context: DeltaContext) = try {
        JsonParser.parseString(json) is JsonElement
        true
    } catch (e: JsonSyntaxException) {
        context.addMismatch(StructureMismatch("root", MismatchType.NOT_VALID_JSON, name))
        false
    }

    fun areTypesEqual(field: String, expected: JsonElement, actual: JsonElement, context: DeltaContext) =
        if (context.isFeatureUsed(Feature.CHECK_FIELDS_PRESENCE_ONLY))
            true
        else if (context.isFeatureUsed(Feature.IGNORE_NUMBERS_TYPE) && isNumber(expected) && isNumber(actual))
            true
        else (getObjectType(expected) to getObjectType(actual)).let {
            if (it.first != it.second) {
                context.addMismatch(ValueMismatch(field, MismatchType.TYPE_MISMATCH, it.first, it.second))
                false
            } else true
        }

    fun areArraysSizeMatch(field: String, expected: JsonArray, actual: JsonArray, context: DeltaContext) =
        if (expected.size() != actual.size()) {
            context.addMismatch(ValueMismatch(field, MismatchType.ARRAY_SIZE_MISMATCH, expected.size().toString(), actual.size().toString()))
            false
        } else true

    fun areFieldsMissed(field: String, expected: JsonObject, actual: JsonObject, context: DeltaContext) {
        if (context.isFeatureUsed(Feature.IGNORE_MISSED_FIELDS))
            return
        expected.keySet().subtract(actual.keySet()).let {
            if (it.isNotEmpty()) {
                context.addMismatch(ObjectMismatch(field, MismatchType.OBJECT_MISSED_FIELDS, it.toList()))
            }
        }
    }

    fun areFieldsUnexpected(field: String, expected: JsonObject, actual: JsonObject, context: DeltaContext) {
        if (context.isFeatureUsed(Feature.IGNORE_EXTRA_FIELDS))
            return
        actual.keySet().subtract(expected.keySet()).let {
            if (it.isNotEmpty())
                context.addMismatch(ObjectMismatch(field, MismatchType.OBJECT_EXTRA_FIELDS, it.toList()))
        }
    }

    fun areFieldsEqual(field: String, expected: JsonPrimitive, actual: JsonPrimitive, context: DeltaContext) {
        if (context.isFeatureUsed(Feature.CHECK_FIELDS_PRESENCE_ONLY))
            return
        if (expected != actual)
            context.addMismatch(ValueMismatch(field, MismatchType.VALUE_MISMATCH, expected.toString(), actual.toString()))
    }

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