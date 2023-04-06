package org.mkfl3x.jsondelta

import com.google.gson.*
import org.mkfl3x.jsondelta.Checks.areArraysSizeMatch
import org.mkfl3x.jsondelta.Checks.areFieldsEqual
import org.mkfl3x.jsondelta.Checks.areFieldsMissed
import org.mkfl3x.jsondelta.Checks.areFieldsUnexpected
import org.mkfl3x.jsondelta.Checks.areTypesEqual
import org.mkfl3x.jsondelta.Checks.isJsonValid

class JsonDelta {

    private val gson = Gson()

    private val features = mutableSetOf<Feature>()

    fun feature(feature: Feature, enable: Boolean) = apply {
        if (enable) features.add(feature) else features.remove(feature)
    }

    fun getUsedFeatures() = features.toList()

    fun compare(expected: Any, actual: Any, vararg ignoredFields: String) =
        compare(gson.toJson(expected), gson.toJson(actual), *ignoredFields)

    fun compare(expected: String, actual: Any, vararg ignoredFields: String) =
        compare(expected, gson.toJson(actual), *ignoredFields)

    fun compare(expected: Any, actual: String, vararg ignoredFields: String) =
        compare(gson.toJson(expected), actual, *ignoredFields)

    fun compare(expected: String, actual: String, vararg ignoredFields: String): JsonDeltaReport =
        DeltaContext(ignoredFields.asList(), features).apply {
            listOf(
                isJsonValid(expected, "expected", this),
                isJsonValid(actual, "actual", this)
            ).let { if (it.any { x -> x.not() }) return@apply }
            comparisonResolver("root", JsonParser.parseString(expected), JsonParser.parseString(actual), this)
        }.getReport()

    private fun comparisonResolver(field: String, expected: JsonElement, actual: JsonElement, context: DeltaContext) {
        if (context.isFieldIgnored(field))
            return
        if (areTypesEqual(field, expected, actual, context).not())
            return
        when (expected) {
            is JsonArray -> compareArrays(field, expected, actual.asJsonArray, context)
            is JsonObject -> compareObjects(field, expected, actual.asJsonObject, context)
            is JsonPrimitive -> compareFields(field, expected, actual.asJsonPrimitive, context)
        }
    }

    private fun compareArrays(field: String, expected: JsonArray, actual: JsonArray, context: DeltaContext) {
        if (areArraysSizeMatch(field, expected, actual, context).not())
            return
        expected.forEachIndexed { i, _ -> comparisonResolver("$field[${i + 1}]", expected[i], actual[i], context) }
    }

    private fun compareObjects(field: String, expected: JsonObject, actual: JsonObject, context: DeltaContext) {
        areFieldsMissed(field, expected, actual, context)
        areFieldsUnexpected(field, expected, actual, context)
        expected.asMap().forEach {
            if (actual.get(it.key) == null) return@forEach
            comparisonResolver("$field.${it.key}", it.value, actual.get(it.key), context)
        }
    }

    private fun compareFields(field: String, expected: JsonPrimitive, actual: JsonPrimitive, context: DeltaContext) {
        areFieldsEqual(field, expected, actual, context)
    }
}