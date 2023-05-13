import org.junit.jupiter.api.Test
import org.mkfl3x.jsondelta.JsonDelta

class IgnoreMissedFieldExplicitlyTest : BaseTest() {

    private val expected = """
        {
          "hello": [
            {"a": 1, "b": 2},
            {"a": 1, "b": 2}
          ]
        }
    """.trimIndent()

    private val actual = """
        {
          "hello": [
            {"a": 1},
            {"a": 1, "b": 2}
          ]
        }
    """.trimIndent()

    @Test
    fun ignoreMissedField() {
        val report = JsonDelta().compare(expected, actual, "root.hello[1].b")
        checkReportSuccess(report)
    }
}