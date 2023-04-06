import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mkfl3x.jsondelta.Feature
import org.mkfl3x.jsondelta.JsonDelta

class FeaturesManagementTest : BaseTest() {

    @Test
    fun addFeatures() {
        val jsonDelta = JsonDelta()
            .feature(Feature.IGNORE_EXTRA_FIELDS, true)
            .feature(Feature.IGNORE_MISSED_FIELDS, true)

        checkFeaturesPresence(jsonDelta, Feature.IGNORE_EXTRA_FIELDS, Feature.IGNORE_MISSED_FIELDS)
    }

    @Test
    fun removeFeature() {
        val jsonDelta = JsonDelta()
            .feature(Feature.IGNORE_EXTRA_FIELDS, true)
            .feature(Feature.IGNORE_MISSED_FIELDS, true)
        jsonDelta.feature(Feature.IGNORE_EXTRA_FIELDS, false)

        checkFeaturesPresence(jsonDelta, Feature.IGNORE_MISSED_FIELDS)
    }

    private fun checkFeaturesPresence(jsonDelta: JsonDelta, vararg expectedFeatures: Feature) {
        assertEquals(expectedFeatures.size, jsonDelta.getUsedFeatures().size, "Unexpected number of features")
        expectedFeatures.forEach {
            assertTrue(jsonDelta.getUsedFeatures().contains(it), "Feature \"$it\" expected, but not found")
        }
    }
}