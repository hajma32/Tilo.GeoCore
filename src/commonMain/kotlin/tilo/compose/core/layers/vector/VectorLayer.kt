package tilo.compose.core.layers.vector

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
}

sealed interface VectorRenderStrategy {
    data object Immediate : VectorRenderStrategy

    data class CachedBitmap(
        val scale: Double = 1.0,
        val paddingPx: Int = 128,
        val invalidateOnZoomDelta: Double = 0.35,
    ) : VectorRenderStrategy
}
