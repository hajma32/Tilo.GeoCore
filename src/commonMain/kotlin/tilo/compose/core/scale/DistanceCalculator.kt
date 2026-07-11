package tilo.compose.core.scale

import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.sinh
import tilo.compose.core.geometry.Point
import tilo.compose.core.projection.Epsg3857Projection
import tilo.compose.core.projection.Epsg4326Projection
import tilo.compose.core.projection.Projection
import tilo.compose.core.transform.TransformationRegistry

/**
 * Computes real-world distance between two points expressed in [projection].
 *
 * Return null when the CRS cannot be measured without application-provided
 * knowledge. This keeps custom or licensed transformations injectable instead
 * of hard-wired into GeoCore.
 */
fun interface DistanceCalculator {
    fun distanceMeters(
        from: Point,
        to: Point,
        projection: Projection,
        transformationRegistry: TransformationRegistry,
    ): Double?
}

object DistanceCalculators {
    /**
     * Uses geodesic distance when possible.
     */
    val Auto: DistanceCalculator = DistanceCalculator { from, to, projection, transformationRegistry ->
        when (projection.id) {
            Epsg4326Projection.id -> haversineMeters(from, to)
            Epsg3857Projection.id -> haversineMeters(from.webMercatorToWgs84(), to.webMercatorToWgs84())
            else -> {
                val toWgs84 = transformationRegistry.find(projection, Epsg4326Projection)
                if (toWgs84 != null) {
                    haversineMeters(
                        toWgs84.sourceToTarget(from),
                        toWgs84.sourceToTarget(to),
                    )
                } else {
                    null
                }
            }
        }
    }

    /**
     * For projected coordinate systems whose units are meters.
     */
    val PlanarMeters: DistanceCalculator = DistanceCalculator { from, to, _, _ ->
        planarMeters(from, to)
    }
}

internal fun planarMeters(from: Point, to: Point): Double {
    val dx = to.x - from.x
    val dy = to.y - from.y
    return sqrt(dx * dx + dy * dy)
}

internal fun haversineMeters(fromLonLat: Point, toLonLat: Point): Double {
    val lat1 = fromLonLat.y.toRadians()
    val lat2 = toLonLat.y.toRadians()
    val deltaLat = (toLonLat.y - fromLonLat.y).toRadians()
    val deltaLon = (toLonLat.x - fromLonLat.x).toRadians()
    val a = sin(deltaLat / 2.0).pow(2.0) +
        cos(lat1) * cos(lat2) * sin(deltaLon / 2.0).pow(2.0)
    val c = 2.0 * atan2Sqrt(a)
    return EarthRadiusMeters * c
}

private fun Point.webMercatorToWgs84(): Point {
    val lon = x / WebMercatorRadiusMeters * 180.0 / PI
    val lat = atan(sinh(y / WebMercatorRadiusMeters)) * 180.0 / PI
    return Point(lon, lat)
}

private fun atan2Sqrt(a: Double): Double =
    kotlin.math.atan2(sqrt(a), sqrt(1.0 - a))

private fun Double.toRadians(): Double = this * PI / 180.0

private const val EarthRadiusMeters = 6_371_008.8
private const val WebMercatorRadiusMeters = 6_378_137.0
