package org.mkfl3x.jsondelta

abstract class Mismatch(open val type: MismatchType)

data class StructureMismatch(override val type: MismatchType) : Mismatch(type)

data class ObjectMismatch(override val type: MismatchType, val fields: String) : Mismatch(type)

data class ValueMismatch(override val type: MismatchType, val expected: String, val actual: String) : Mismatch(type)

data class JsonDeltaReport(val success: Boolean, val mismatches: Map<String, MutableList<Mismatch>>) {

    override fun toString() = """
Status: ${if (success) "success" else "failed"}
${if (mismatches.isNotEmpty()) "Mismatches:" else ""}
${mismatches.map { m -> " Field \"${m.key}\"\n    - ${m.value.joinToString("\n    - ") { printMismatch(it) }}" }.joinToString("\n")}
    """.trimIndent()

    private fun printMismatch(mismatch: Mismatch) = when (mismatch) {
        is StructureMismatch -> " ${mismatch.type.description}."
        is ObjectMismatch -> "${mismatch.type.description}. Fields:\"${mismatch.fields}\""
        is ValueMismatch -> "${mismatch.type.description}. Expected: \"${mismatch.expected}\"; Actual: \"${mismatch.actual}\""
        else -> throw Exception("Unexpected mismatch type")
    }
}

class DeltaContext(val ignoredFields: List<String>) {

    private val mismatches = mutableMapOf<String, MutableList<Mismatch>>()

    fun addMismatch(field: String, mismatch: Mismatch?) {
        mismatch?.apply {
            if (mismatches[field] == null)
                mismatches[field] = mutableListOf()
            mismatches[field]!!.add(mismatch)
        }
    }

    fun getReport() = JsonDeltaReport(mismatches.isEmpty(), mismatches)
}

enum class MismatchType(val description: String) {
    NOT_VALID_JSON("JSON object is not valid"),
    OBJECT_TYPES_MISMATCH("Object types are mismatched"),
    OBJECT_MISSED_FIELDS("Object missed fields"),
    OBJECT_EXTRA_FIELDS("Object contains unexpected fields"),
    ARRAY_SIZE_MISMATCH("Array sizes are mismatched"),
    VALUE_TYPE_MISMATCH("Type of value mismatch"),
    VALUE_MISMATCH("Value mismatch")
}

enum class Feature {
    IGNORE_EXTRA_FIELDS,
    CHECK_FIELDS_PRESENCE_ONLY
}