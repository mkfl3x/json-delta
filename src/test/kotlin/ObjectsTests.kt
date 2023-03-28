import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mkfl3x.jsondelta.JsonDelta

class ObjectsTests {

    private val expected = """
        {
          "id": 1,
          "data": {
            "text": "hello",
            "num": 123
          }
        }
    """.trimIndent()

    private val missedField = """
        {
          "data": {
            "text": "hello",
            "num": 123
          }
        }
    """.trimIndent()

    private val extraField = """
        {
          "id": 1,
          "data": {
            "text": "hello",
            "num": 123
          },
          "some": null
        }
    """.trimIndent()

    private val wrongElementType = """
        {
          "id": 1,
          "data": {
            "text": "hello",
            "num": "123"
          }
        }
    """.trimIndent()

    private val wrongElementValue = """
        {
          "id": 1,
          "data": {
            "text": "hello",
            "num": 321
          }
        }
    """.trimIndent()

    @Test
    fun equalObjects() {
        val report = JsonDelta().compare(expected, expected)
        assertTrue(report.success, "Report should be 'success'")
    }

    @Test
    fun missedField() {
        val report = JsonDelta().compare(expected, missedField)
        assertTrue(!report.success, "Report should not be 'success'")
    }

    @Test
    fun extraField() {
        val report = JsonDelta().compare(expected, extraField)
        assertTrue(!report.success, "Report should not be 'success'")
    }

    @Test
    fun wrongElementType() {
        val report = JsonDelta().compare(expected, wrongElementType)
        assertTrue(!report.success, "Report should not be 'success'")
    }

    @Test
    fun wrongElementValue() {
        val report = JsonDelta().compare(expected, wrongElementValue)
        assertTrue(!report.success, "Report should not be 'success'")
    }
}