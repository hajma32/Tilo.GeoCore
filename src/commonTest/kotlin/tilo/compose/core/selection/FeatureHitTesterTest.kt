package tilo.compose.core.selection

import tilo.compose.core.feature.Feature
import tilo.compose.core.geometry.Point
import kotlin.test.Test
import kotlin.test.assertEquals

class FeatureHitTesterTest {
    @Test
    fun returnsAllMatchingFeaturesInInputOrder() {
        val layers =
            listOf(
                FeatureHitTestLayer(
                    id = "upper",
                    features =
                        listOf(
                            FeatureHitTestFeature(
                                feature =
                                    Feature(
                                        key = "upper-point",
                                        geometry = Point(10.0, 10.0),
                                    ),
                            ),
                        ),
                ),
                FeatureHitTestLayer(
                    id = "lower",
                    features =
                        listOf(
                            FeatureHitTestFeature(
                                feature =
                                    Feature(
                                        key = "lower-point",
                                        geometry = Point(10.0, 10.0),
                                    ),
                            ),
                        ),
                ),
            )

        val selections =
            FeatureHitTester().hitTest(
                layers = layers,
                screenPoint = Point(10.0, 10.0),
                worldPoint = Point(10.0, 10.0),
                worldToScreen = { it },
            )

        assertEquals(listOf("upper-point", "lower-point"), selections.map { it.feature.key })
    }
}
