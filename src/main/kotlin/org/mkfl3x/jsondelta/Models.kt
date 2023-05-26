package org.mkfl3x.jsondelta

sealed class Mismatch {

    abstract val field: String

    abstract val type: MismatchType

    val description
        get() = type.description

    fun print() = "\"$field\": " + when (this) {
        is StructureMismatch -> "$objectName ${type.description}."
        is ObjectMismatch -> "${type.description}. Fields: ${fields.joinToString()}"
        is ValueMismatch -> "${type.description}. Expected: $expected; Actual: $actual"
    }
}

data class StructureMismatch(
    override val field: String,
    override val type: MismatchType,
    val objectName: String
) : Mismatch()

data class ObjectMismatch(
    override val field: String,
    override val type: MismatchType,
    val fields: List<String>
) : Mismatch()

data class ValueMismatch(
    override val field: String,
    override val type: MismatchType,
    val expected: String,
    val actual: String
) : Mismatch()

data class JsonDeltaReport(val success: Boolean, val mismatches: List<Mismatch>) {

    override fun toString() = """
    |Status: ${if (success) "success" else "failed"}
    |${if (mismatches.isNotEmpty()) "Mismatches:" else ""}
    |${mismatches.joinToString("\n") { it.print() }}
    """.trimMargin()
}

class DeltaContext(private val ignoredFields: List<String>, private val features: Set<Feature>) {

    private val mismatches = mutableListOf<Mismatch>()

    fun isFieldIgnored(field: String) = field in ignoredFields || ignoredFields.any { Regex(it).matches(field) }

    fun isFeatureUsed(feature: Feature) = feature in features

    fun addMismatch(mismatch: Mismatch?) = mismatch?.apply { mismatches.add(mismatch) }

    fun getReport() = JsonDeltaReport(mismatches.isEmpty(), mismatches)
}

enum class MismatchType(val description: String) {
    NOT_VALID_JSON("JSON object is not valid"),
    OBJECT_MISSED_FIELDS("Object doesn't contain expected fields"),
    OBJECT_EXTRA_FIELDS("Object contains unexpected fields"),
    ARRAY_SIZE_MISMATCH("Array size mismatch"),
    VALUE_MISMATCH("Value mismatch"),
    TYPE_MISMATCH("Type mismatch")
}

enum class Feature {
    IGNORE_EXTRA_FIELDS,
    IGNORE_MISSED_FIELDS,
    IGNORE_NUMBERS_TYPE,
    IGNORE_STRING_CASE,
    CHECK_FIELDS_PRESENCE_ONLY
}