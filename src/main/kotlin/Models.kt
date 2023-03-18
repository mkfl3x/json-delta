abstract class Mismatch(open val type: MismatchType)

data class StructureMismatch(override val type: MismatchType, val message: String) : Mismatch(type)

data class ObjectMismatch(override val type: MismatchType, val fields: String) : Mismatch(type)

data class ValueMismatch(override val type: MismatchType, val expected: String, val actual: String) : Mismatch(type)

data class ComparisonReport(val success: Boolean, val mismatches: Map<String, MutableList<Mismatch>>)

class DeltaContext(val ignoredFields: List<String>) {

    private val mismatches = mutableMapOf<String, MutableList<Mismatch>>()

    fun addMismatch(field: String, mismatch: Mismatch?) {
        mismatch?.apply {
            if (mismatches[field] == null)
                mismatches[field] = mutableListOf()
            mismatches[field]!!.add(mismatch)
        }
    }

    fun getReport() = ComparisonReport(mismatches.isEmpty(), mismatches)
}

enum class MismatchType {
    NOT_VALID_JSON,
    OBJECT_TYPES_MISMATCH,
    OBJECT_MISSED_FIELDS,
    OBJECT_EXTRA_FIELDS,
    ARRAY_SIZE_MISMATCH,
    VALUE_TYPE_MISMATCH,
    VALUE_MISMATCH
}

enum class Feature {
    IGNORE_EXTRA_FIELDS,
    CHECK_FIELDS_PRESENCE_ONLY
}