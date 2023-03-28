import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mkfl3x.jsondelta.JsonDelta

class ArraysTests {

    private val expected = "[ {\"arr\": [1, 2, 3]}, {\"arr\": [1, 2, 3]} ]"
    private val wrongArraySize = "[ {\"arr\": [1, 2, 3]} ]"
    private val wrongArrayElementType = "[ {\"arr\": [1, 2, 3]}, {\"arr\": [1, 2, \"3\"]} ]"
    private val wrongArrayElementValue = "[ {\"arr\": [1, 2, 3]}, {\"arr\": [1, 2, 4]} ]"

    @Test
    fun equalArrays() {
        val report = JsonDelta().compare(expected, expected)
        assertTrue(report.success, "Report should be 'success'")
    }

    @Test
    fun wrongArraySize() {
        val report = JsonDelta().compare(expected, wrongArraySize)
        assertTrue(!report.success, "Report should not be 'success'")
    }

    @Test
    fun wrongArrayElementType() {
        val report = JsonDelta().compare(expected, wrongArrayElementType)
        assertTrue(!report.success, "Report should not be 'success'")
    }

    @Test
    fun wrongArrayElementValue() {
        val report = JsonDelta().compare(expected, wrongArrayElementValue)
        assertTrue(!report.success, "Report should not be 'success'")
    }
}