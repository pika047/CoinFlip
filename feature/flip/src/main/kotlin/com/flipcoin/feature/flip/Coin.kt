package com.flipcoin.feature.flip

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipcoin.core.model.CoinSide
import kotlin.math.PI
import kotlin.math.sin
import kotlinx.coroutines.launch

private const val FLIP_DURATION_MS = 1_600
private const val FULL_SPINS = 6
private val TossEasing = CubicBezierEasing(0.2f, 0.7f, 0.3f, 1f)

private val GoldLight = Color(0xFFFFE08A)
private val Gold = Color(0xFFD4A017)
private val GoldDark = Color(0xFF8C6A0B)

/**
 * A coin that spins around its vertical axis and arcs upwards each time
 * [flipCount] changes, landing on [side].
 */
@Composable
fun Coin(
    side: CoinSide,
    flipCount: Int,
    onFlipFinished: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 220.dp,
) {
    val rotation = remember { Animatable(side.restingAngle()) }
    val progress = remember { Animatable(1f) }
    val haptics = LocalHapticFeedback.current
    val currentOnFinished by rememberUpdatedState(onFlipFinished)
    // Survives rotation so a finished flip is not replayed when the screen is recreated.
    var animatedFlipCount by rememberSaveable { mutableIntStateOf(flipCount) }

    LaunchedEffect(flipCount) {
        if (flipCount == animatedFlipCount) {
            currentOnFinished()
            return@LaunchedEffect
        }
        animatedFlipCount = flipCount
        val start = rotation.value
        val landing = start + FULL_SPINS * 360f
        val target = landing - (landing.mod(360f)) + side.restingAngle()
        progress.snapTo(0f)
        launch { progress.animateTo(1f, tween(FLIP_DURATION_MS)) }
        rotation.animateTo(target, tween(FLIP_DURATION_MS, easing = TossEasing))
        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        currentOnFinished()
    }

    val tossHeightPx = with(LocalDensity.current) { size.toPx() * 0.6f }
    val angle = rotation.value.mod(360f)
    val showingBack = angle > 90f && angle < 270f

    Box(
        modifier = modifier
            .size(size)
            .graphicsLayer {
                rotationY = rotation.value
                cameraDistance = 16f * density
                translationY = -sin(progress.value * PI).toFloat() * tossHeightPx
                val lift = 1f + 0.25f * sin(progress.value * PI).toFloat()
                scaleX = lift
                scaleY = lift
            },
        contentAlignment = Alignment.Center,
    ) {
        // The back face is drawn pre-mirrored so its text reads correctly after a 180° turn.
        CoinFace(
            label = stringResource(if (showingBack) R.string.flip_tails else R.string.flip_heads),
            modifier = Modifier.graphicsLayer { if (showingBack) rotationY = 180f },
        )
    }
}

private fun CoinSide.restingAngle(): Float = if (this == CoinSide.HEADS) 0f else 180f

@Composable
private fun CoinFace(label: String, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val radius = size.minDimension / 2
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(GoldLight, Gold, GoldDark),
                    center = center + Offset(-radius * 0.3f, -radius * 0.3f),
                    radius = radius * 1.4f,
                ),
                radius = radius,
            )
            drawCircle(color = GoldDark, radius = radius - 2.dp.toPx(), style = Stroke(width = 4.dp.toPx()))
            drawCircle(
                color = GoldDark.copy(alpha = 0.6f),
                radius = radius * 0.78f,
                style = Stroke(width = 2.dp.toPx()),
            )
        }
        Text(
            text = label,
            style = TextStyle(
                color = GoldDark,
                fontSize = 34.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
            ),
        )
    }
}

@Preview
@Composable
private fun CoinPreview() {
    Coin(side = CoinSide.HEADS, flipCount = 0, onFlipFinished = {})
}
