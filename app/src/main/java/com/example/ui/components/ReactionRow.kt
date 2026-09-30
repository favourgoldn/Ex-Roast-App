package com.example.ui.components

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ExFlame
import com.example.ui.theme.ExRoastTheme

data class ReactionTypeInfo(
    val type: String,
    val emoji: String,
    val label: String
)

val REACTION_TYPES = listOf(
    ReactionTypeInfo("flame", "🔥", "Savage"),
    ReactionTypeInfo("skull", "💀", "I'm dead"),
    ReactionTypeInfo("cringe", "🤦", "Cringe"),
    ReactionTypeInfo("laugh", "😭", "Can't breathe"),
    ReactionTypeInfo("red_flag", "🚩", "Run")
)

@Composable
fun ReactionRow(
    totalCount: Int,
    activeReaction: String?,
    hapticsEnabled: Boolean,
    onSelectReaction: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    fun performHaptic() {
        if (!hapticsEnabled) return
        try {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(15, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(15)
            }
        } catch (_: Exception) {}
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        REACTION_TYPES.forEach { rInfo ->
            val isSelected = activeReaction == rInfo.type
            var pressed by remember { mutableStateOf(false) }
            val scale by animateFloatAsState(
                targetValue = if (pressed) 1.3f else 1.0f,
                animationSpec = spring(dampingRatio = 0.5f),
                finishedListener = { pressed = false },
                label = "reaction_scale"
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isSelected) ExFlame.copy(alpha = 0.2f)
                        else ExRoastTheme.colors.surfaceRaised.copy(alpha = 0.5f)
                    )
                    .clickable {
                        pressed = true
                        performHaptic()
                        onSelectReaction(rInfo.type)
                    }
                    .padding(horizontal = 8.dp, vertical = 5.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = rInfo.emoji,
                        fontSize = 15.sp,
                        modifier = Modifier.scale(scale)
                    )
                    if (isSelected) {
                        Text(
                            text = rInfo.label,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ExFlame
                        )
                    }
                }
            }
        }

        if (totalCount > 0) {
            Text(
                text = "$totalCount",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = ExRoastTheme.colors.textMuted,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}
