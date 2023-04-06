import org.junit.jupiter.api.Test
import org.mkfl3x.jsondelta.Feature
import org.mkfl3x.jsondelta.JsonDelta
import org.mkfl3x.jsondelta.MismatchType
import org.mkfl3x.jsondelta.ValueMismatch

class TypesTest : BaseTest() {

    private val stringValue = "string" to "{ \"value\": \"hello\" }"

    private val booleanValue = "boolean" to "{ \"value\": true }"

    private val integerValue = "integer" to "{ \"value\": 1 }"

    private val floatValue = "float" to "{ \"value\": 1.0 }"

    private val nullValue = "null" to "{ \"value\": null }"

    private val objectValue = "object" to "{ \"value\": { \"x\": 1, \"y\": 2 } }"

    private val arrayValue = "array" to "{ \"value\": [1, 2, 3] }"

    @Test
    fun checkStringType() {
        compareEqualTypes(stringValue, stringValue)
        compareDiffTypes(stringValue, booleanValue)
    }

    @Test
    fun checkBooleanType() {
        compareEqualTypes(booleanValue, booleanValue)
        compareDiffTypes(booleanValue, integerValue)
    }

    @Test
    fun checkIntegerType() {
        compareEqualTypes(integerValue, integerValue)
        compareDiffTypes(integerValue, floatValue)
    }

    @Test
    fun checkFloatType() {
        compareEqualTypes(floatValue, floatValue)
        compareDiffTypes(floatValue, nullValue)
    }

    @Test
    fun checkNullType() {
        compareEqualTypes(nullValue, nullValue)
        compareDiffTypes(nullValue, objectValue)
    }

    @Test
    fun checkObjectType() {
        compareEqualTypes(objectValue, objectValue)
        compareDiffTypes(objectValue, arrayValue)
    }

    @Test
    fun checkArrayType() {
        compareEqualTypes(arrayValue, arrayValue)
        compareDiffTypes(arrayValue, stringValue)
    }

    @Test
    fun checkNumberTypeIgnoring() {
        val report = JsonDelta()
            .feature(Feature.IGNORE_NUMBERS_TYPE, true)
            .compare(integerValue.second, floatValue.second)
        checkReportSuccess(report)
    }

    private fun compareEqualTypes(expected: Pair<String, String>, actual: Pair<String, String>) {
        val report = JsonDelta().compare(expected.second, actual.second)
        checkReportSuccess(report)
    }

    private fun compareDiffTypes(expected: Pair<String, String>, actual: Pair<String, String>) {
        val report = JsonDelta().compare(expected.second, actual.second)
        checkReportFailed(report)
        checkMismatchesPresence(
            report,
            ValueMismatch("root.value", MismatchType.TYPES_MISMATCH, expected.first, actual.first)
        )
    }
}