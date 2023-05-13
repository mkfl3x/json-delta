import org.junit.jupiter.api.Test
import org.mkfl3x.jsondelta.JsonDelta

class IgnoreExtraFieldExplicitlyTest : BaseTest() {

    private val expected = """
        {
          "hello": [
            {"a": 1, "b": 2},
            {"a": 1}
          ]
        }
    """.trimIndent()

    private val actual = """
        {
          "hello": [
            {"a": 1, "b": 2},
            {"a": 1, "b": 2}
          ]
        }
    """.trimIndent()

    @Test
    fun ignoreExtraField() {
        val report = JsonDelta().compare(expected, actual, "root.hello[2].b")
        checkReportSuccess(report)
    }
}