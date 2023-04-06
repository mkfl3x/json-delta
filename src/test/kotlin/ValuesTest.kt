import org.junit.jupiter.api.Test
import org.mkfl3x.jsondelta.Feature
import org.mkfl3x.jsondelta.JsonDelta
import org.mkfl3x.jsondelta.MismatchType
import org.mkfl3x.jsondelta.ValueMismatch

class ValuesTest : BaseTest() {

    private val expected = """
        {
          "stringValue": "hello",
          "booleanValue": true,
          "integerValue": 1,
          "floatValue": 1.0,
          "nullValue": null,
          "objectValue": { "x": 1, "y": 2},
          "arrayValue": [1, 2, 3]
        }
    """.trimIndent()

    private val actual = """
        {
          "stringValue": "ciao",
          "booleanValue": false,
          "integerValue": 2,
          "floatValue": 2.5,
          "nullValue": "x",
          "objectValue": { "x": 1, "y": 3},
          "arrayValue": [1, 4, 3]
        }
    """.trimIndent()

    @Test
    fun checkDiffFieldValues() {
        val report = JsonDelta().compare(expected, actual)
        checkReportFailed(report)
        checkMismatchesPresence(
            report,
            ValueMismatch("root.stringValue", MismatchType.VALUE_MISMATCH, "\"hello\"", "\"ciao\""),
            ValueMismatch("root.booleanValue", MismatchType.VALUE_MISMATCH, "true", "false"),
            ValueMismatch("root.integerValue", MismatchType.VALUE_MISMATCH, "1", "2"),
            ValueMismatch("root.floatValue", MismatchType.VALUE_MISMATCH, "1.0", "2.5"),
            ValueMismatch("root.nullValue", MismatchType.TYPE_MISMATCH, "null", "string"),
            ValueMismatch("root.objectValue.y", MismatchType.VALUE_MISMATCH, "2", "3"),
            ValueMismatch("root.arrayValue[2]", MismatchType.VALUE_MISMATCH, "2", "4")
        )
    }

    @Test
    fun checkNumberTypeIgnoring() {
        val report = JsonDelta()
            .feature(Feature.IGNORE_NUMBERS_TYPE, true)
            .compare("{ \"value\": 1 }", "{ \"value\": 1.5 }")
        checkReportFailed(report)
        checkMismatchesPresence(
            report,
            ValueMismatch("root.value", MismatchType.VALUE_MISMATCH, "1", "1.5")
        )
    }

    @Test
    fun checkFieldsOnly() {
        val report = JsonDelta()
            .feature(Feature.CHECK_FIELDS_PRESENCE_ONLY, true)
            .compare(expected, actual)
        checkReportSuccess(report)
    }
}