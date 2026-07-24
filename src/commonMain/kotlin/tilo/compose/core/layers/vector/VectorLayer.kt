package tilo.compose.core.layers.vector

import tilo.compose.core.feature.FeatureLayerStyle
import tilo.compose.core.feature.source.FeatureSource
import tilo.compose.core.layers.Layer

/**
 * A layer that provides vector features from a `FeatureSource`.
 *
 * The renderer queries `FeatureSource` for features to render. The layer keeps
 * an identity and lifecycle hooks.
 */
interface VectorLayer : Layer {
    /**
     * Source of vector features for this layer.
     */
    val source: FeatureSource

    /**
     * Rendering strategy chosen by the layer.
     */
    val renderStrategy: VectorRenderStrategy
        get() = VectorRenderStrategy.Immediate

    /**
     * Layer-level style used as the default for point, line, polygon, label,
     * and selected feature rendering.
     */
    val style: FeatureLayerStyle
        get() = FeatureLayerStyle()
}

sealed interface VectorRenderStrategy {
    data object Immediate : VectorRenderStrategy

    /**
     * Draw vectors immediately after simplifying lines and polygon rings with Douglas-Peucker.
     *
     * [tolerancePx] is the maximum screen-space deviation. The renderer converts it to map units
     * at the current zoom, so lower zoom levels simplify more aggressively while higher zooms
     * retain progressively more source detail.
     */
    data class ImmediateLod(
        val tolerancePx: Double = 1.5,
    ) : VectorRenderStrategy {
        init {
            require(tolerancePx.isFinite() && tolerancePx > 0.0) {
                "tolerancePx must be finite and positive"
            }
        }
    }

    data class CachedBitmap(
        val scale: Double = 1.0,
        val paddingPx: Int = 128,
        val invalidateOnZoomDelta: Double = 0.35,
    ) : VectorRenderStrategy
}
