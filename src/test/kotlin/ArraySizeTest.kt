import org.junit.jupiter.api.Test
import org.mkfl3x.jsondelta.JsonDelta
import org.mkfl3x.jsondelta.MismatchType
import org.mkfl3x.jsondelta.ValueMismatch

class ArraySizeTest : BaseTest() {

    private val expected = "{ \"arr\": [1, 2, 3] }"

    private val actualLess = "{ \"arr\": [1, 2] }"

    private val actualMore = "{ \"arr\": [1, 2, 3, 4] }"

    @Test
    fun checkArrayLessSize() {
        val report = JsonDelta().compare(expected, actualLess)

        checkReportFailed(report)
        checkMismatchesPresence(
            report,
            ValueMismatch("root.arr", MismatchType.ARRAY_SIZE_MISMATCH, "3", "2")
        )
    }

    @Test
    fun checkArrayMoreSize() {
        val report = JsonDelta().compare(expected, actualMore)

        checkReportFailed(report)
        checkMismatchesPresence(
            report,
            ValueMismatch("root.arr", MismatchType.ARRAY_SIZE_MISMATCH, "3", "4")
        )
    }
}