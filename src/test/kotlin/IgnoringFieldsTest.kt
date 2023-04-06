import org.junit.jupiter.api.Test
import org.mkfl3x.jsondelta.JsonDelta

class IgnoringFieldsTest : BaseTest() {

    private val expected = """
        {
          "a": "hello",
          "b": "ciao",
          "c": [ 1, 2, 3 ],
          "d": {
            "x": 1,
            "y": { "a": true, "b": false }
          },
          "e": [
            {
              "i": 1,
              "arr": [100, 200, 300]
            },
            {
              "i": 2,
              "arr": [400, 500, 600]
            }
          ]
        }
    """.trimIndent()

    private val actual = """
        {
          "a": "hello",
          "b": "aloha",
          "c": [ 1, 0, 0 ],
          "d": {
            "x": 1,
            "y": { "a": true, "b": true }
          },
          "e": [
            {
              "i": 1,
              "arr": [100, 200, 300]
            },
            {
              "i": 3,
              "arr": [400, 500, false]
            }
          ]
        }
    """.trimIndent()

    @Test
    fun checkIgnoreFieldsWithExplicitPaths() {
        val ignore = listOf("root.b", "root.c[2]", "root.c[3]", "root.d.y.b", "root.e[2].i", "root.e[2].arr")
        val report = JsonDelta().compare(expected, actual, *ignore.toTypedArray())

        checkReportSuccess(report)
    }

    @Test
    fun checkIgnoreFieldsWithRegex() {
        val ignore = listOf("root.b", "root.c\\[[2-3]\\]", "root.d.y.[a,b,c]", "root.e[2].i", "root.e\\[\\d\\].arr\\[\\d\\]")
        val report = JsonDelta().compare(expected, actual, *ignore.toTypedArray())

        checkReportSuccess(report)
    }
}