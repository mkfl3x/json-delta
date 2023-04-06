import org.junit.jupiter.api.Test
import org.mkfl3x.jsondelta.Feature
import org.mkfl3x.jsondelta.JsonDelta
import org.mkfl3x.jsondelta.MismatchType
import org.mkfl3x.jsondelta.ObjectMismatch

class ExtraFieldsTest : BaseTest() {

    private val expected = """
        {
          "a": 1,
          "b": {
            "x": 1.1
          }
        }
    """.trimIndent()

    private val actual = """
        {
          "a": 1,
          "b": {
            "x": 1.1,
            "y": 2.2,
            "z": 3.3
          },
          "c": 2
        }
    """.trimIndent()

    @Test
    fun checkExtraFields() {
        val report = JsonDelta().compare(expected, actual)

        checkReportFailed(report)
        checkMismatchesPresence(
            report,
            ObjectMismatch("root", MismatchType.OBJECT_EXTRA_FIELDS, listOf("c")),
            ObjectMismatch("root.b", MismatchType.OBJECT_EXTRA_FIELDS, listOf("y", "z"))
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
            ObjectMismatch("root", MismatchType.OBJECT_EXTRA_FIELDS, listOf("c")),
            ObjectMismatch("root.b", MismatchType.OBJECT_EXTRA_FIELDS, listOf("y", "z"))
        )
    }

    @Test
    fun checkIgnoredExtraFields() {
        val report = JsonDelta()
            .feature(Feature.IGNORE_EXTRA_FIELDS, true)
            .compare(expected, actual)

        checkReportSuccess(report)
    }

    @Test
    fun checkFieldsOnlyIgnoredExtraFields() {
        val report = JsonDelta()
            .feature(Feature.IGNORE_EXTRA_FIELDS, true)
            .feature(Feature.CHECK_FIELDS_PRESENCE_ONLY, true)
            .compare(expected, actual)

        checkReportSuccess(report)
    }
}