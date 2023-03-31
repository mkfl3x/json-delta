package org.mkfl3x.jsondelta

abstract class Mismatch {

    protected abstract val type: MismatchType

    protected val description
        get() = type.description

    fun print() = when (this) {
        is StructureMismatch -> "$objectName ${type.description}."
        is ObjectMismatch -> "${type.description}. Fields: $fields"
        is ValueMismatch -> "${type.description}. Expected: $expected; Actual: $actual"
        else -> throw Exception("Unexpected mismatch type")
    }
}

data class StructureMismatch(override val type: MismatchType, val objectName: String) : Mismatch()

data class ObjectMismatch(override val type: MismatchType, val fields: String) : Mismatch()

data class ValueMismatch(override val type: MismatchType, val expected: String, val actual: String) : Mismatch()

data class JsonDeltaReport(val success: Boolean, val mismatches: Map<String, Mismatch>) {

    override fun toString() = """
    |Status: ${if (success) "success" else "failed"}
    |${if (mismatches.isNotEmpty()) "Mismatches:" else ""}
    |${mismatches.map { "\"${it.key}\": ${it.value.print()}" }.joinToString("\n")}
    """.trimMargin()
}

class DeltaContext(val ignoredFields: List<String>) {

    private val mismatches = mutableMapOf<String, Mismatch>()

    fun addMismatch(field: String, mismatch: Mismatch?) = mismatch?.apply { mismatches[field] = mismatch }

    fun getReport() = JsonDeltaReport(mismatches.isEmpty(), mismatches)
}

enum class MismatchType(val description: String) {
    NOT_VALID_JSON("JSON object is not valid"),
    OBJECT_TYPES_MISMATCH("Object types are mismatched"),
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