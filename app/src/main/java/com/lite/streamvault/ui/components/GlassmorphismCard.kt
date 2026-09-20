package com.lite.streamvault.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lite.streamvault.ui.theme.DarkCardElevated
import com.lite.streamvault.ui.theme.GlassBorder
import com.lite.streamvault.ui.theme.GlassBorderLight

@Composable
fun GlassmorphismCard(
    modifier: Modifier = Modifier,
    cornerRadius: Int = 24,
    alpha: Float = 0.08f,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(cornerRadius.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = alpha)),
        elevation = CardDefaults.cardElevation(0.dp),
        border = BorderStroke(
            1.dp,
            Brush.linearGradient(colors = listOf(GlassBorder, GlassBorderLight))
        )
    ) {
        content()
    }
}

@Composable
fun GlassBox(
    modifier: Modifier = Modifier,
    alpha: Float = 0.08f,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = alpha),
                        Color.White.copy(alpha = alpha * 0.5f)
                    )
                )
            )
    ) {
        content()
    }
}

@Composable
private fun shimmerBrush(): Brush {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translate by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )
    val base = MaterialTheme.colorScheme.surfaceVariant
    val highlight = DarkCardElevated
    return Brush.linearGradient(
        colors = listOf(base, highlight, base),
        start = Offset(translate, 0f),
        end = Offset(translate + 300f, 300f)
    )
}

@Composable
fun ShimmerContentCard() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        ShimmerBox(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(12.dp))
        )
        Spacer(Modifier.width(12.dp))
        Column {
            Spacer(Modifier.height(10.dp))
            ShimmerLine(Modifier.fillMaxWidth(0.7f).height(14.dp))
            Spacer(Modifier.height(8.dp))
            ShimmerLine(Modifier.fillMaxWidth(0.5f).height(12.dp))
        }
    }
}

@Composable
fun ShimmerPosterCard() {
    Column(modifier = Modifier.padding(4.dp)) {
        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(14.dp))
        )
        Spacer(Modifier.height(6.dp))
        ShimmerLine(Modifier.fillMaxWidth(0.6f).height(12.dp))
        Spacer(Modifier.height(4.dp))
        ShimmerLine(Modifier.fillMaxWidth(0.4f).height(10.dp))
    }
}

@Composable
fun ShimmerBox(modifier: Modifier) {
    Box(
        modifier = modifier.background(shimmerBrush())
    )
}

@Composable
fun ShimmerLine(modifier: Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(shimmerBrush())
    )
}
