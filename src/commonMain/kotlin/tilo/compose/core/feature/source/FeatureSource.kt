package tilo.compose.core.feature.source

import tilo.compose.core.feature.Feature
import tilo.compose.core.map.Map

/**
 * Source of vector features for a `VectorLayer`.
 */
interface FeatureSource {
    /**
     * Monotonic or content-derived version used by renderers to invalidate layer caches.
     *
     * Custom mutable sources should update this value whenever returned features can change.
     */
    val version: Long
        get() = 0L

    /**
     * Return features that are relevant for the provided [map] state.
     */
    fun getFeatures(map: Map): List<Feature>
}
