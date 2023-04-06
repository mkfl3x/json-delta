import org.junit.jupiter.api.Test
import org.mkfl3x.jsondelta.Feature
import org.mkfl3x.jsondelta.JsonDelta
import org.mkfl3x.jsondelta.MismatchType
import org.mkfl3x.jsondelta.ObjectMismatch

class MissedFieldsTest : BaseTest() {

    private val expected = """
        {
          "a": 1,
          "b": 2,
          "c": {
            "x": 1.1,
            "y": 2.2,
            "z": 3.3
          }
        }
    """.trimIndent()

    private val actual = """
        {
          "a": 1,
          "c": {
            "x": 1.1
          }
        }
    """.trimIndent()

    @Test
    fun checkMissedFields() {
        val report = JsonDelta().compare(expected, actual)

        checkReportFailed(report)
        checkMismatchesPresence(
            report,
            ObjectMismatch("root", MismatchType.OBJECT_MISSED_FIELDS, listOf("b")),
            ObjectMismatch("root.c", MismatchType.OBJECT_MISSED_FIELDS, listOf("y", "z"))
        )
    }

    @Test
    fun checkFieldsOnly() {
        val report = JsonDelta()
            .feature(Feature.CHECK_FIELDS_PRESENCE_ONLY, true)
            .compare(expected, actual)

        checkReportFailed(report)
        checkMismatchesPresence(
            report,
            ObjectMismatch("root", MismatchType.OBJECT_MISSED_FIELDS, listOf("b")),
            ObjectMismatch("root.c", MismatchType.OBJECT_MISSED_FIELDS, listOf("y", "z"))
        )
    }

    @Test
    fun checkIgnoredMissedFields() {
        val report = JsonDelta()
            .feature(Feature.IGNORE_MISSED_FIELDS, true)
            .compare(expected, actual)

        checkReportSuccess(report)
    }

    @Test
    fun checkFieldsOnlyIgnoredExtraFields() {
        val report = JsonDelta()
            .feature(Feature.IGNORE_MISSED_FIELDS, true)
            .feature(Feature.CHECK_FIELDS_PRESENCE_ONLY, true)
            .compare(expected, actual)

        checkReportSuccess(report)
    }
}