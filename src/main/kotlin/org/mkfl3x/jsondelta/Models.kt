package org.mkfl3x.jsondelta

abstract class Mismatch {

    abstract val field: String

    abstract val type: MismatchType

    val description
        get() = type.description

    fun print() = "\"$field\": " + when (this) {
        is StructureMismatch -> "$objectName ${type.description}."
        is ObjectMismatch -> "${type.description}. Fields: ${fields.joinToString()}"
        is ValueMismatch -> "${type.description}. Expected: $expected; Actual: $actual"
        else -> throw Exception("Unexpected mismatch type")
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

class DeltaContext(val ignoredFields: List<String>) {

    private val mismatches = mutableListOf<Mismatch>()

    fun addMismatch(mismatch: Mismatch?) = mismatch?.apply { mismatches.add(mismatch) }

    fun getReport() = JsonDeltaReport(mismatches.isEmpty(), mismatches)
}

enum class MismatchType(val description: String) {
    NOT_VALID_JSON("JSON object is not valid"),
    TYPES_MISMATCH("Object types are mismatched"),
    OBJECT_MISSED_FIELDS("Object missed fields"),
    OBJECT_EXTRA_FIELDS("Object contains unexpected fields"),
    ARRAY_SIZE_MISMATCH("Array sizes are mismatched"),
    VALUE_MISMATCH("Value mismatch")
}

enum class Feature {
    IGNORE_EXTRA_FIELDS,
    IGNORE_MISSED_FIELDS,
    IGNORE_NUMBERS_TYPE,
    CHECK_FIELDS_PRESENCE_ONLY
}