package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.voice.OrbVoiceState
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun GlowingOrbCanvas(
    state: OrbVoiceState,
    audioRms: Float = 0f,
    modifier: Modifier = Modifier,
    size: Dp = 64.dp,
    onClick: (() -> Unit)? = null
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orb_animation")

    // Slow atmospheric pulse
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Orbital ring 1 rotation
    val angleRing1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (state == OrbVoiceState.THINKING) 1200 else 6000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "angleRing1"
    )

    // Orbital ring 2 rotation (reverse)
    val angleRing2 by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (state == OrbVoiceState.THINKING) 800 else 4500,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "angleRing2"
    )

    // Quantum wave ripple
    val rippleScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ripple"
    )

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .size(size)
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = ripple(bounded = false, radius = size / 2)
                    ) { onClick() }
                } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val baseRadius = this.size.minDimension / 2f

            // Dynamic amplification depending on voice state and audio RMS
            val stateAmp = when (state) {
                OrbVoiceState.LISTENING -> 1.0f + (audioRms * 0.45f)
                OrbVoiceState.THINKING -> pulse * 0.95f
                OrbVoiceState.SPEAKING -> 1.0f + (audioRms * 0.35f)
                OrbVoiceState.IDLE -> pulse
            }

            val coreRadius = baseRadius * 0.58f * stateAmp

            // Dynamic colors by state
            val (c1, c2, c3) = when (state) {
                OrbVoiceState.LISTENING -> Triple(ElectricCyan, NeonPurple, Color(0xFF38BDF8))
                OrbVoiceState.THINKING -> Triple(NeonPink, NeonPurple, ElectricCyan)
                OrbVoiceState.SPEAKING -> Triple(Color(0xFF10B981), ElectricCyan, NeonPurple)
                OrbVoiceState.IDLE -> Triple(ElectricCyan, NeonPurple, NeonPink)
            }

            // Outer glow aura
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        c2.copy(alpha = 0.45f),
                        c1.copy(alpha = 0.25f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = baseRadius * 0.98f
                ),
                center = center,
                radius = baseRadius * 0.98f
            )

            // Ripple ring for speaking / listening
            if (state == OrbVoiceState.SPEAKING || state == OrbVoiceState.LISTENING) {
                val rippleAlpha = (1f - (rippleScale - 1f) / 0.45f).coerceIn(0f, 1f) * 0.5f
                drawCircle(
                    color = c1.copy(alpha = rippleAlpha),
                    center = center,
                    radius = (coreRadius * rippleScale).coerceAtMost(baseRadius * 0.95f),
                    style = Stroke(width = 2.dp.toPx())
                )
            }

            // Inner core sphere
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.9f),
                        c1.copy(alpha = 0.85f),
                        c2.copy(alpha = 0.9f),
                        c3.copy(alpha = 0.95f)
                    ),
                    center = center,
                    radius = coreRadius
                ),
                center = center,
                radius = coreRadius
            )

            // Orbital Ring 1 (tilted oval-like arc)
            val ring1Radius = baseRadius * 0.76f
            val rad1 = Math.toRadians(angleRing1.toDouble())
            val dot1 = Offset(
                x = center.x + (ring1Radius * cos(rad1)).toFloat(),
                y = center.y + (ring1Radius * 0.75f * sin(rad1)).toFloat()
            )

            drawCircle(
                brush = Brush.sweepGradient(
                    listOf(c1.copy(alpha = 0.1f), c1, c2, c1.copy(alpha = 0.1f)),
                    center = center
                ),
                center = center,
                radius = ring1Radius,
                style = Stroke(width = 1.8.dp.toPx())
            )

            // Orbital Dot 1
            drawCircle(
                color = Color.White,
                center = dot1,
                radius = 3.2.dp.toPx()
            )

            // Orbital Ring 2 (counter-tilted)
            val ring2Radius = baseRadius * 0.88f
            val rad2 = Math.toRadians(angleRing2.toDouble())
            val dot2 = Offset(
                x = center.x + (ring2Radius * 0.8f * cos(rad2)).toFloat(),
                y = center.y + (ring2Radius * sin(rad2)).toFloat()
            )

            drawCircle(
                brush = Brush.sweepGradient(
                    listOf(c3.copy(alpha = 0.1f), c3, c2, c3.copy(alpha = 0.1f)),
                    center = center
                ),
                center = center,
                radius = ring2Radius,
                style = Stroke(width = 1.5.dp.toPx())
            )

            // Orbital Dot 2
            drawCircle(
                color = c1,
                center = dot2,
                radius = 2.6.dp.toPx()
            )
        }
    }
}
