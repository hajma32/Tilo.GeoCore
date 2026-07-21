package tilo.compose.core.map

import tilo.compose.core.geometry.BoundingBox
import tilo.compose.core.geometry.Point
import tilo.compose.core.projection.IdentityProjection
import tilo.compose.core.projection.Projection
import kotlin.math.log2

/**
 * Mutable engine-level map state, including center, zoom, projection, and viewport.
 */
class MapState(
    center: Point = Point(0.0, 0.0),
    zoom: Double = 0.0,
    val projection: Projection = IdentityProjection,
    val config: MapConfig = MapConfig.Default,
    viewport: Viewport = Viewport.Empty,
) {
    var center: Point = center
        set(value) {
            field = constrainCenter(value)
        }

    var zoom: Double = zoom.coerceIn(config.minZoom, config.maxZoom)
        set(value) {
            field = value.coerceIn(config.minZoom, config.maxZoom)
            center = center
        }

    var viewport: Viewport = viewport
        set(value) {
            field = value
            center = center
        }

    init {
        this.center = center
    }

    fun panBy(
        dx: Double,
        dy: Double,
    ) {
        val worldDelta = viewport.screenToWorld(Point(dx, dy), center, zoom, projection.worldUnitsPerMapUnit)
        val originWorld = viewport.screenToWorld(Point(0.0, 0.0), center, zoom, projection.worldUnitsPerMapUnit)
        center =
            Point(
                x = center.x + (worldDelta.x - originWorld.x),
                y = center.y + (worldDelta.y - originWorld.y),
            )
    }

    fun zoomBy(
        delta: Double,
        focus: Point? = null,
    ) {
        val newZoom = (zoom + delta).coerceIn(config.minZoom, config.maxZoom)
        if (focus == null) {
            zoom = newZoom
            return
        }
        val worldBefore = viewport.screenToWorld(focus, center, zoom, projection.worldUnitsPerMapUnit)
        val worldAfter = viewport.screenToWorld(focus, center, newZoom, projection.worldUnitsPerMapUnit)
        val targetCenter =
            Point(
                center.x + (worldBefore.x - worldAfter.x),
                center.y + (worldBefore.y - worldAfter.y),
            )
        zoom = newZoom
        center = targetCenter
    }

    /**
     * Centers the camera on [bounds] and chooses the largest zoom that keeps
     * the whole rectangle inside the viewport after [paddingPx] is applied.
     * Padding is expressed in physical pixels because this core type has no UI
     * density dependency.
     */
    fun fitBounds(
        bounds: BoundingBox,
        paddingPx: Double = 0.0,
    ) {
        require(
            bounds.minX.isFinite() &&
                bounds.maxX.isFinite() &&
                bounds.minY.isFinite() &&
                bounds.maxY.isFinite(),
        ) {
            "bounds extents must be finite"
        }
        require(bounds.minX <= bounds.maxX && bounds.minY <= bounds.maxY) {
            "bounds minimum extents must not be greater than maximum extents"
        }
        require(paddingPx.isFinite() && paddingPx >= 0.0) {
            "paddingPx must be finite and non-negative"
        }
        require(2.0 * paddingPx < viewport.width && 2.0 * paddingPx < viewport.height) {
            "paddingPx must leave positive viewport width and height"
        }

        val availableWidth = viewport.width - 2.0 * paddingPx
        val availableHeight = viewport.height - 2.0 * paddingPx
        val width = bounds.maxX - bounds.minX
        val height = bounds.maxY - bounds.minY
        val pixelRatio = viewport.pixelRatio

        val horizontalScale = if (width > 0.0) availableWidth / (width * pixelRatio) else Double.POSITIVE_INFINITY
        val verticalScale = if (height > 0.0) availableHeight / (height * pixelRatio) else Double.POSITIVE_INFINITY
        val scale = minOf(horizontalScale, verticalScale)
        val targetZoom =
            if (scale.isFinite()) {
                log2(scale * projection.worldUnitsPerMapUnit)
            } else {
                config.maxZoom
            }

        val targetCenter =
            Point(
                x = (bounds.minX + bounds.maxX) / 2.0,
                y = (bounds.minY + bounds.maxY) / 2.0,
            )
        zoom = targetZoom.coerceIn(config.minZoom, config.maxZoom)
        center = targetCenter
    }

    fun transformSourceToTarget(
        point: Point,
        source: Projection,
        target: Projection,
    ): Point = config.transformer.sourceToTarget(point, source, target)

    fun transformTargetToSource(
        point: Point,
        source: Projection,
        target: Projection,
    ): Point = config.transformer.targetToSource(point, source, target)

    fun worldToScreen(world: Point): Point =
        viewport.worldToScreen(world, center, zoom, projection.worldUnitsPerMapUnit)

    fun screenToWorld(screen: Point): Point =
        viewport.screenToWorld(screen, center, zoom, projection.worldUnitsPerMapUnit)

    private fun constrainCenter(requested: Point): Point {
        val bounds = config.cameraBounds ?: return requested
        val topLeft = viewport.screenToWorld(Point(0.0, 0.0), requested, zoom, projection.worldUnitsPerMapUnit)
        val bottomRight =
            viewport.screenToWorld(
                Point(viewport.width.toDouble(), viewport.height.toDouble()),
                requested,
                zoom,
                projection.worldUnitsPerMapUnit,
            )
        val halfWidth = (bottomRight.x - topLeft.x) / 2.0
        val halfHeight = (topLeft.y - bottomRight.y) / 2.0

        return Point(
            x = requested.x.constrainAxis(bounds.minX, bounds.maxX, halfWidth),
            y = requested.y.constrainAxis(bounds.minY, bounds.maxY, halfHeight),
        )
    }

    private fun Double.constrainAxis(
        minimum: Double,
        maximum: Double,
        viewportHalfExtent: Double,
    ): Double {
        val minimumCenter = minimum + viewportHalfExtent
        val maximumCenter = maximum - viewportHalfExtent
        return if (minimumCenter <= maximumCenter) {
            coerceIn(minimumCenter, maximumCenter)
        } else {
            (minimum + maximum) / 2.0
        }
    }
}
