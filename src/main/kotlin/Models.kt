abstract class Mismatch(open val type: MismatchType)

data class FatalMismatch(override val type: MismatchType) : Mismatch(type)

data class ObjectMismatch(override val type: MismatchType, val fields: String) : Mismatch(type)

data class ValueMismatch(override val type: MismatchType, val expected: String, val actual: String) : Mismatch(type)

class DeltaReport(val ignoredFields: List<String>) {

    val success: Boolean
        get() = mismatches.isEmpty()

    private val mismatches = mutableMapOf<String, MutableList<Mismatch>>()

    fun addMismatch(field: String, mismatch: Mismatch?) {
        mismatch?.apply {
            if (mismatches[field] == null)
                mismatches[field] = mutableListOf()
            mismatches[field]!!.add(mismatch)
        }
    }
}

enum class MismatchType {
    NOT_VALID_JSON,
    OBJECT_TYPE_MISMATCH,
    VALUE_TYPE_MISMATCH,
    VALUE_MISMATCH,
    MISSED_FIELDS,
    EXTRA_FIELDS,
    ARRAY_SIZE_MISMATCH
}