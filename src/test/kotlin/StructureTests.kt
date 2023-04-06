import org.junit.jupiter.api.Test
import org.mkfl3x.jsondelta.JsonDelta
import org.mkfl3x.jsondelta.MismatchType
import org.mkfl3x.jsondelta.StructureMismatch

class StructureTests : BaseTest() {

    @Test
    fun checkInvalidObject() {
        val report = JsonDelta().compare("{", "{}")
        checkReportFailed(report)
        checkMismatchesPresence(
            report,
            StructureMismatch("root", MismatchType.NOT_VALID_JSON, "expected")
        )
    }

    @Test
    fun checkInvalidArray() {
        val report = JsonDelta().compare("[]", "]")
        checkReportFailed(report)
        checkMismatchesPresence(
            report,
            StructureMismatch("root", MismatchType.NOT_VALID_JSON, "actual")
        )
    }

    @Test
    fun checkInvalidActualAndExpected() {
        val report = JsonDelta().compare("[[],", "{}}")
        checkReportFailed(report)
        checkMismatchesPresence(
            report,
            StructureMismatch("root", MismatchType.NOT_VALID_JSON, "expected"),
            StructureMismatch("root", MismatchType.NOT_VALID_JSON, "actual")
        )
    }
}