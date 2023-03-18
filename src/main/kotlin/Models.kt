abstract class Mismatch(open val type: MismatchType)

data class FatalMismatch(override val type: MismatchType) : Mismatch(type)

data class ObjectMismatch(override val type: MismatchType, val fields: String) : Mismatch(type)

data class ValueMismatch(override val type: MismatchType, val expected: String, val actual: String) : Mismatch(type)

data class DeltaReport(val success: Boolean, val mismatches: Map<String, List<Mismatch>>)

enum class MismatchType {
    NOT_VALID_JSON,
    OBJECT_TYPE_MISMATCH,
    VALUE_TYPE_MISMATCH,
    VALUE_MISMATCH,
    MISSED_FIELDS,
    EXTRA_FIELDS,
    ARRAY_SIZE_MISMATCH
}