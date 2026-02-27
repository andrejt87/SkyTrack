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
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashComplete: () -> Unit,
    onActiveFlight: (Long) -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val activeFlightId by viewModel.activeFlightId.collectAsStateWithLifecycle()

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

    val alpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(800, easing = EaseInOut),
        label = "alpha"
    )

    LaunchedEffect(activeFlightId) {
        delay(2000)
        val id = activeFlightId
        if (id != null && id > 0) {
            onActiveFlight(id)
        } else {
            onSplashComplete()
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
            Spacer(modifier = Modifier.height(32.dp))
            CircularProgressIndicator(
                color = Amber,
                strokeWidth = 2.dp,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
