package com.lite.streamvault.ui.screens.splash

import android.app.Activity
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lite.streamvault.ui.components.MaintenanceScreen
import com.lite.streamvault.ui.components.UpdateDialog
import com.lite.streamvault.ui.theme.Blue500
import com.lite.streamvault.viewmodel.SplashState
import com.lite.streamvault.viewmodel.SplashViewModel

@Composable
fun SplashScreen(
    onReady: (com.lite.streamvault.domain.model.AppSettings) -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.start(context as? Activity)
    }

    LaunchedEffect(state) {
        when (val s = state) {
            is SplashState.Ready -> onReady(s.settings)
            else -> Unit
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when (val s = state) {
            SplashState.Loading -> SplashVisual()
            is SplashState.MaintenanceMode -> MaintenanceScreen(s.message)
            is SplashState.UpdateAvailable -> {
                SplashVisual()
                UpdateDialog(
                    message = s.message,
                    downloadUrl = s.url,
                    forceUpdate = s.forceUpdate
                )
            }
            is SplashState.Error -> SplashVisual(error = s.message)
            is SplashState.Ready -> SplashVisual()
        }
    }
}

@Composable
private fun SplashVisual(error: String? = null) {
    val transition = rememberInfiniteTransition(label = "splash")
    val scale by transition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Morad TV",
            style = MaterialTheme.typography.displayLarge,
            color = Color.White,
            modifier = Modifier.scale(scale)
        )
        Text(
            text = "Premium Streaming",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.padding(top = 4.dp)
        )
        if (error != null) {
            Text(
                text = error,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.padding(top = 12.dp)
            )
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .padding(bottom = 64.dp)
                .size(42.dp),
            color = Blue500,
            strokeWidth = 4.dp
        )
    }
}
