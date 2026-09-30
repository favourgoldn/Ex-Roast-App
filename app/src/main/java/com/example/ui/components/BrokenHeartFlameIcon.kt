package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.ExFlame
import com.example.ui.theme.ExFlameHot

@Composable
fun BrokenHeartFlameIcon(
    modifier: Modifier = Modifier,
    size: Dp = 28.dp,
    flameBrush: Brush = Brush.linearGradient(listOf(ExFlame, ExFlameHot))
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        // Outer Flame Shape formed like stylized broken heart
        val flamePath = Path().apply {
            moveTo(w * 0.5f, h * 0.05f) // top tip of flame
            // Right curve down
            cubicTo(w * 0.85f, h * 0.25f, w * 0.95f, h * 0.60f, w * 0.75f, h * 0.88f)
            // Bottom tip of heart
            cubicTo(w * 0.65f, h * 0.98f, w * 0.55f, h * 1.0f, w * 0.50f, h * 0.92f)
            // Left curve up
            cubicTo(w * 0.45f, h * 1.0f, w * 0.35f, h * 0.98f, w * 0.25f, h * 0.88f)
            cubicTo(w * 0.05f, h * 0.60f, w * 0.15f, h * 0.25f, w * 0.50f, h * 0.05f)
            close()
        }

        drawPath(path = flamePath, brush = flameBrush)

        // Inner Jagged Heart Break (dark cut)
        val breakPath = Path().apply {
            moveTo(w * 0.50f, h * 0.30f)
            lineTo(w * 0.56f, h * 0.45f)
            lineTo(w * 0.44f, h * 0.60f)
            lineTo(w * 0.54f, h * 0.72f)
            lineTo(w * 0.50f, h * 0.86f)
            lineTo(w * 0.48f, h * 0.86f)
            lineTo(w * 0.40f, h * 0.70f)
            lineTo(w * 0.52f, h * 0.58f)
            lineTo(w * 0.42f, h * 0.44f)
            close()
        }
        drawPath(path = breakPath, color = Color(0xFF0B0B0F).copy(alpha = 0.85f))

        // Small inner ember core
        val innerEmber = Path().apply {
            moveTo(w * 0.50f, h * 0.50f)
            cubicTo(w * 0.60f, h * 0.62f, w * 0.58f, h * 0.80f, w * 0.50f, h * 0.85f)
            cubicTo(w * 0.42f, h * 0.80f, w * 0.40f, h * 0.62f, w * 0.50f, h * 0.50f)
            close()
        }
        drawPath(path = innerEmber, color = Color(0xFFFFDF00).copy(alpha = 0.9f))
    }
}
