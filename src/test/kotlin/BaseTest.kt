import org.junit.jupiter.api.Assertions.*
import org.mkfl3x.jsondelta.JsonDeltaReport
import org.mkfl3x.jsondelta.Mismatch

abstract class BaseTest {

    protected fun checkReportSuccess(report: JsonDeltaReport) {
        assertTrue(report.equals, "Unexpected report status")
        checkMismatchesCount(report.mismatches.size, 0)
    }

    protected fun checkReportFailed(report: JsonDeltaReport) =
        assertFalse(report.equals, "Unexpected report status")

    protected fun checkMismatchesPresence(report: JsonDeltaReport, vararg expectedMismatches: Mismatch) {
        checkMismatchesCount(report.mismatches.size, expectedMismatches.size)
        expectedMismatches.forEach {
            assertTrue(report.mismatches.contains(it), "\"$it\" not found in 'report.mismatches'")
        }
    }

    private fun checkMismatchesCount(actualMismatches: Int, expectedMismatches: Int) =
        assertEquals(expectedMismatches, actualMismatches, "Unexpected number of mismatches")
}