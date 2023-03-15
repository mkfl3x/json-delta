import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class ArraysTests {

    private val expected = "[ {\"arr\": [1, 2, 3]}, {\"arr\": [1, 2, 3]} ]"
    private val wrongArraySize = "[ {\"arr\": [1, 2, 3]} ]"
    private val wrongArrayElementType = "[ {\"arr\": [1, 2, 3]}, {\"arr\": [1, 2, \"3\"]} ]"
    private val wrongArrayElementValue = "[ {\"arr\": [1, 2, 3]}, {\"arr\": [1, 2, 4]} ]"

    @Test
    fun equalArrays() {
        val report = JsonDelta().compare(expected, expected)
        assertTrue("Report should be 'success'") { report.success }
    }

    @Test
    fun wrongArraySize() {
        val report = JsonDelta().compare(expected, wrongArraySize)
        assertTrue("Report should not be 'success'") { !report.success }
    }

    @Test
    fun wrongArrayElementType() {
        val report = JsonDelta().compare(expected, wrongArrayElementType)
        assertTrue("Report should not be 'success'") { !report.success }
    }

    @Test
    fun wrongArrayElementValue() {
        val report = JsonDelta().compare(expected, wrongArrayElementValue)
        assertTrue("Report should not be 'success'") { !report.success }
    }
}