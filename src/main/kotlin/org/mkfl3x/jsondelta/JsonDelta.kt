package org.mkfl3x.jsondelta

import com.google.gson.*
import org.mkfl3x.jsondelta.Checks.checkArraySizes
import org.mkfl3x.jsondelta.Checks.checkExtraFields
import org.mkfl3x.jsondelta.Checks.checkFieldType
import org.mkfl3x.jsondelta.Checks.checkFieldValue
import org.mkfl3x.jsondelta.Checks.checkJsonSyntax
import org.mkfl3x.jsondelta.Checks.checkMissedFields
import org.mkfl3x.jsondelta.Checks.checkTypes

class JsonDelta {

    private val features = mutableListOf<Feature>()

    fun featureOn(feature: Feature) = apply { features.add(feature) }

    fun featureOff(feature: Feature) = apply { features.remove(feature) }

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
        if (!features.contains(Feature.IGNORE_EXTRA_FIELDS))
            checkExtraFields(expected, actual)?.apply {
                report.addMismatch(fieldName, this)
                return
            }
        expected.asMap().forEach { comparisonResolver(it.value, actual.get(it.key), "$fieldName.${it.key}", report) }
    }

    private fun compareFields(expected: JsonPrimitive, actual: JsonPrimitive, fieldName: String, report: DeltaContext) {
        if (features.contains(Feature.CHECK_FIELDS_PRESENCE_ONLY))
            return
        report.addMismatch(fieldName, checkFieldType(expected, actual))
        report.addMismatch(fieldName, checkFieldValue(expected, actual))
    }
}