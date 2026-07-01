
package com.mirzamadil.customarc

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * A Jetpack Compose arc/gauge progress indicator, in the spirit of
 * [SemiCircleProgressView](https://github.com/BlackFlagBin/SemiCircleProgress) — redrawn as a
 * segmented gauge with a gradient sweep of radial ticks, a faint full-sweep track, and an
 * optional label/status pair around the value (e.g. a battery readout with an offline status).
 *
 * Typography is fully overridable: pass [fontFamily] to apply one font across the label, value,
 * and status text, or override [labelStyle]/[valueStyle]/[statusStyle] individually for per-line
 * control (size, weight, letter spacing, etc).
 */
@Composable
fun ArcProgressView(
    progress: Float,
    modifier: Modifier = Modifier,
    maxProgress: Float = 100f,
    startAngle: Float = 140f,
    sweepAngle: Float = 260f,
    trackColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    trackStrokeWidth: Dp = 1.dp,
    segmentCount: Int = 30,
    segmentThickness: Dp = 6.dp,
    segmentLength: Dp = 22.dp,
    segmentColors: List<Color> = listOf(
        Color(0xFFE5484D),
        Color(0xFF9C4FD6),
        Color(0xFF4C6FFF),
    ),
    fontFamily: FontFamily? = null,
    label: String? = null,
    labelStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    labelColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    valueStyle: TextStyle = MaterialTheme.typography.displaySmall,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
    valueFormatter: (Float) -> String = { value ->
        "${((value / maxProgress) * 100f).roundToInt()}%"
    },
    statusText: String? = null,
    statusStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    statusColor: Color = MaterialTheme.colorScheme.error,
    animationSpec: AnimationSpec<Float> = tween(durationMillis = 900, easing = FastOutSlowInEasing),
) {
    require(segmentCount > 0) { "segmentCount must be positive, was $segmentCount" }
    require(segmentColors.isNotEmpty()) { "segmentColors must not be empty" }

    val clampedProgress = progress.coerceIn(0f, maxProgress)
    val animatedProgress by animateFloatAsState(
        targetValue = clampedProgress,
        animationSpec = animationSpec,
        label = "CustomArcProgress",
    )

    Box(modifier = modifier, contentAlignment = BiasAlignment(0f, -0.15f)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val trackStrokeWidthPx = trackStrokeWidth.toPx()
            val outerRadius = size.minDimension / 2f - trackStrokeWidthPx / 2f
            val trackDiameter = outerRadius * 2f

            drawArc(
                color = trackColor,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(center.x - outerRadius, center.y - outerRadius),
                size = Size(trackDiameter, trackDiameter),
                style = Stroke(width = trackStrokeWidthPx, cap = StrokeCap.Round),
            )

            val litCount = if (maxProgress <= 0f) {
                0
            } else {
                (segmentCount * (animatedProgress / maxProgress)).roundToInt().coerceIn(0, segmentCount)
            }

            if (litCount > 0) {
                val segmentInnerRadius = (outerRadius - segmentLength.toPx()).coerceAtLeast(0f)
                val angleStep = sweepAngle / segmentCount
                val segmentThicknessPx = segmentThickness.toPx()

                for (i in 0 until litCount) {
                    val angleRad = Math.toRadians((startAngle + angleStep * (i + 0.5f)).toDouble())
                    val dx = cos(angleRad).toFloat()
                    val dy = sin(angleRad).toFloat()
                    val fraction = if (litCount == 1) 0f else i / (litCount - 1).toFloat()

                    drawLine(
                        color = colorAlong(segmentColors, fraction),
                        start = Offset(center.x + dx * segmentInnerRadius, center.y + dy * segmentInnerRadius),
                        end = Offset(center.x + dx * outerRadius, center.y + dy * outerRadius),
                        strokeWidth = segmentThicknessPx,
                        cap = StrokeCap.Butt,
                    )
                }
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (label != null) {
                Text(text = label, style = labelStyle.withFontFamily(fontFamily), color = labelColor)
            }
            Text(
                text = valueFormatter(animatedProgress),
                style = valueStyle.withFontFamily(fontFamily),
                color = valueColor,
            )
            if (statusText != null) {
                Text(text = statusText, style = statusStyle.withFontFamily(fontFamily), color = statusColor)
            }
        }
    }
}

private fun TextStyle.withFontFamily(fontFamily: FontFamily?): TextStyle =
    if (fontFamily == null) this else copy(fontFamily = fontFamily)

private fun colorAlong(colors: List<Color>, fraction: Float): Color {
    if (colors.size == 1) return colors[0]
    val scaled = fraction.coerceIn(0f, 1f) * (colors.size - 1)
    val index = scaled.toInt().coerceIn(0, colors.size - 2)
    return lerp(colors[index], colors[index + 1], scaled - index)
}
