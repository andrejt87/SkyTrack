package com.skytrack.app.ui.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skytrack.app.ui.theme.*

@Composable
fun SplashScreen(
    onSplashComplete: () -> Unit,
    onActiveFlight: (Long) -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val activeFlightId by viewModel.activeFlightId.collectAsStateWithLifecycle()
    val gpsReady by viewModel.gpsReady.collectAsStateWithLifecycle()

    val infiniteTransition = rememberInfiniteTransition(label = "plane")
    val planeRotation by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotation"
    )
    val planeX by infiniteTransition.animateFloat(
        initialValue = -20f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "translateX"
    )

    // Navigate once GPS is ready (or after 10s timeout as fallback)
    var timedOut by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(10_000)
        timedOut = true
    }

    LaunchedEffect(gpsReady, timedOut) {
        if (gpsReady || timedOut) {
            val id = activeFlightId
            if (id != null && id > 0) {
                onActiveFlight(id)
            } else {
                onSplashComplete()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DeepBlue, DarkBackground)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Flight,
                contentDescription = "SkyTrack",
                tint = Amber,
                modifier = Modifier
                    .size(80.dp)
                    .rotate(planeRotation)
                    .offset(x = planeX.dp)
            )
            Text(
                text = "SKYTRACK",
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = TextPrimary,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 6.sp
                )
            )
            Text(
                text = "Offline Flight Tracker",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextSecondary,
                    letterSpacing = 2.sp
                )
            )
            Spacer(modifier = Modifier.height(24.dp))
            CircularProgressIndicator(
                color = Amber,
                strokeWidth = 2.dp,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = if (!gpsReady) "Acquiring GPS…" else "Ready",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSecondary
                )
            )
        }
    }
}
