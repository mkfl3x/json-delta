import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import org.mkfl3x.jsondelta.*

class IgnoreArraysOrderTest : BaseTest() {

    private val expectedArrayJson = "[1, 2, 3]"

    private val actualArrayJson = "[3, 2, 1]"

    private val expectedArrayFieldJson = """
        {
          "val": true,
          "arr": [
            {"x": 1 , "y": 2, "arr": [1, 2, 3]},
            {"x": 3 , "y": 4, "arr": [4, 5, 6]},
            {"x": 5 , "y": 6, "arr": [7, 8, 9]}
          ]
        }
    """.trimIndent()

    private val actualArrayFieldJson = """
        {
          "val": true,
          "arr": [
            {"x": 3 , "y": 4, "arr": [4, 5, 6]},
            {"x": 1 , "y": 2, "arr": [1, 2, 3]},
            {"x": 5 , "y": 6, "arr": [7, 8, 9]}
          ]
        }
    """.trimIndent()

    private val expectedDuplicatedArrayElementsJson = "[1, 1, 1]"

    private val actualDuplicatedArrayElementsJson = "[3, 1, 1]"

    @Test
    fun compareSimpleArrays() {
        val report = JsonDelta()
            .feature(Feature.IGNORE_ARRAYS_ORDER, true)
            .compare(expectedArrayJson, actualArrayJson)
        checkReportSuccess(report)
    }

    @Test
    fun compareObjectsWithArrayField() {
        val report = JsonDelta()
            .feature(Feature.IGNORE_ARRAYS_ORDER, true)
            .compare(expectedArrayFieldJson, actualArrayFieldJson, "root.val")
        checkReportSuccess(report)
    }

    @Test
    fun compareArraysWithDuplicatedValues() {
        val report = JsonDelta()
            .feature(Feature.IGNORE_ARRAYS_ORDER, true)
            .compare(expectedDuplicatedArrayElementsJson, actualDuplicatedArrayElementsJson)
        checkReportFailed(report)
        checkMismatchesPresence(
            report,
            ArrayElementNotFoundMismatch("root[3]", MismatchType.ARRAY_ELEMENT_NOT_FOUND)
        )
    }

    @Test
    fun compareArrayWithIgnoringArrayIndex() {
        JsonDelta()
            .feature(Feature.IGNORE_ARRAYS_ORDER, true).apply {
                assertAll(
                    {
                        assertThrows<IgnoreArrayIndexException> {
                            compare(expectedArrayJson, actualArrayJson, "root[1]")
                        }
                    },
                    {
                        assertThrows<IgnoreArrayIndexException> {
                            compare(expectedArrayJson, actualArrayJson, "root\\[[1,2]\\]")
                        }
                    },
                    {
                        assertThrows<IgnoreArrayIndexException> {
                            compare(expectedArrayJson, actualArrayJson, "root\\[[1-3]\\]")
                        }
                    }
                )
            }
    }
}