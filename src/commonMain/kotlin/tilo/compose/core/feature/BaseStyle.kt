package tilo.compose.core.feature

import kotlin.jvm.JvmInline

sealed interface GeometryStyle

/*
 * Numeric style dimensions are logical display units (DIP/DP), not physical
 * pixels. Platform renderers convert them to device pixels using their density.
 */
@JvmInline
value class ColorValue(
    val argb: ULong,
) {
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

data class PointStyle(
    val shape: PointShape = PointShape.Circle,
    val size: Double = 14.0,
    val fill: FillStyle? = FillStyle(color = ColorValue.Blue),
    val stroke: StrokeStyle? = StrokeStyle(color = ColorValue.White, width = 2.5),
    val icon: PointIconStyle? = null,
) : GeometryStyle

data class LineStyle(
    val casing: StrokeStyle? =
        StrokeStyle(
            color = ColorValue.White,
            width = 6.0,
            lineCap = LineCap.Round,
            lineJoin = LineJoin.Round,
        ),
    val stroke: StrokeStyle =
        StrokeStyle(
            color = ColorValue.Blue,
            width = 3.0,
            lineCap = LineCap.Round,
            lineJoin = LineJoin.Round,
        ),
) : GeometryStyle

data class PolygonStyle(
    val fill: FillStyle? = FillStyle(color = ColorValue(0x331E88E5u)),
    val casing: StrokeStyle? =
        StrokeStyle(
            color = ColorValue.White,
            width = 5.0,
            lineJoin = LineJoin.Round,
        ),
    val stroke: StrokeStyle? =
        StrokeStyle(
            color = ColorValue.Blue,
            width = 2.0,
            lineJoin = LineJoin.Round,
        ),
) : GeometryStyle

enum class LabelFontWeight {
    Normal,
    Medium,
    SemiBold,
    Bold,
}

enum class LabelFontStyle {
    Normal,
    Italic,
}

data class LabelBackgroundStyle(
    val color: ColorValue,
    val opacity: Double = 1.0,
    val cornerRadius: Double = 4.0,
    val paddingHorizontal: Double = 5.0,
    val paddingVertical: Double = 2.0,
)

data class LabelStyle(
    val color: ColorValue = ColorValue.Black,
    val fontSize: Double = 12.0,
    val fontWeight: LabelFontWeight = LabelFontWeight.Bold,
    val fontStyle: LabelFontStyle = LabelFontStyle.Normal,
    val haloColor: ColorValue = ColorValue.White,
    val haloWidth: Double = 3.0,
    val background: LabelBackgroundStyle? = null,
    val bitmapPadding: Double = 2.0,
    val offsetY: Double = 12.0,
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
