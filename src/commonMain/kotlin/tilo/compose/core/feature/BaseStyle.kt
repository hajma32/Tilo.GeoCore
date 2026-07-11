package tilo.compose.core.feature

import kotlin.jvm.JvmInline

sealed interface GeometryStyle

@JvmInline
value class ColorValue(val argb: ULong) {
    companion object {
        val Transparent = ColorValue(0x00000000u)
        val Black = ColorValue(0xFF111827u)
        val White = ColorValue(0xFFFFFFFFu)
        val Blue = ColorValue(0xFF1E88E5u)
    }
}

enum class LineCap {
    Butt,
    Round,
    Square,
}

enum class LineJoin {
    Miter,
    Round,
    Bevel,
}

data class DashPattern(
    val intervals: List<Double>,
    val phase: Double = 0.0,
)

data class StrokeStyle(
    val color: ColorValue = ColorValue.Black,
    val width: Double = 1.0,
    val opacity: Double = 1.0,
    val lineCap: LineCap = LineCap.Butt,
    val lineJoin: LineJoin = LineJoin.Miter,
    val dash: DashPattern? = null,
)

sealed interface FillPattern {
    data class Hatch(
        val angleDegrees: Double = 45.0,
        val spacing: Double = 8.0,
        val stroke: StrokeStyle = StrokeStyle(width = 1.0),
    ) : FillPattern

    data class Dots(
        val spacing: Double = 8.0,
        val radius: Double = 1.5,
        val color: ColorValue = ColorValue.Black,
    ) : FillPattern
}

data class FillStyle(
    val color: ColorValue = ColorValue.Transparent,
    val opacity: Double = 1.0,
    val pattern: FillPattern? = null,
)

enum class PointShape {
    Circle,
    Square,
    Diamond,
    Triangle,
    Cross,
}

data class PointIcon(
    val id: String,
)

data class PointStyle(
    val shape: PointShape = PointShape.Circle,
    val size: Double = 10.0,
    val fill: FillStyle? = FillStyle(color = ColorValue.Blue),
    val stroke: StrokeStyle? = StrokeStyle(color = ColorValue.White, width = 2.0),
    val icon: PointIcon? = null,
) : GeometryStyle

data class LineStyle(
    val stroke: StrokeStyle = StrokeStyle(color = ColorValue.Blue, width = 2.0),
) : GeometryStyle

data class PolygonStyle(
    val fill: FillStyle? = FillStyle(color = ColorValue(0x331E88E5u)),
    val stroke: StrokeStyle? = StrokeStyle(color = ColorValue.Blue, width = 1.5),
) : GeometryStyle

data class LabelStyle(
    val color: ColorValue = ColorValue.Black,
)

data class FeatureLayerStyle(
    val point: PointStyle? = null,
    val line: LineStyle? = null,
    val polygon: PolygonStyle? = null,
    val label: LabelStyle? = null,
    val selectedPoint: PointStyle? = null,
    val selectedLine: LineStyle? = null,
    val selectedPolygon: PolygonStyle? = null,
    val selectedLabel: LabelStyle? = null,
)

data class BaseStyle(
    val strokeColor: Long? = null,
    val fillColor: Long? = null,
    val strokeWidth: Double? = null
) : GeometryStyle
