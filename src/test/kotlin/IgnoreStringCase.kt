import org.junit.jupiter.api.Test
import org.mkfl3x.jsondelta.Feature
import org.mkfl3x.jsondelta.JsonDelta
import org.mkfl3x.jsondelta.MismatchType
import org.mkfl3x.jsondelta.ValueMismatch

class IgnoreStringCase : BaseTest() {

    private val expected = "{\"text\": \"hello\"}"

    private val actual = "{\"text\": \"hElLo\"}"

    @Test
    fun ignoreStringCase() {
        val report = JsonDelta()
            .feature(Feature.IGNORE_STRING_CASE, true)
            .compare(expected, actual)
        checkReportSuccess(report)
    }

    @Test
    fun dontIgnoreStringCase() {
        val report = JsonDelta().compare(expected, actual)
        checkReportFailed(report)
        checkMismatchesPresence(
            report,
            ValueMismatch("root.text", MismatchType.VALUE_MISMATCH, "\"hello\"", "\"hElLo\"")
        )
    }
}