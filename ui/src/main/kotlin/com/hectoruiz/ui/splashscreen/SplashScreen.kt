package com.hectoruiz.ui.splashscreen

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hectoruiz.domain.commons.Constants.DELAY_SPLASH_SCREEN_MS
import com.hectoruiz.ui.R
import com.hectoruiz.ui.applist.theme.SoftonicTheme
import kotlinx.coroutines.delay


@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    SoftonicTheme {
        SplashScreen {}
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplashScreen(navigateToAppList: () -> Unit) {

    var animationRunning by remember { mutableStateOf(true) }
    val infiniteTransition = rememberInfiniteTransition(label = "splashTransition")

    val animatedColor by infiniteTransition.animateColor(
        initialValue = Color.Blue,
        targetValue = Color.Cyan,
        animationSpec = infiniteRepeatable(
            tween(1500, easing = LinearOutSlowInEasing),
            RepeatMode.Reverse,
        ), label = "colorAnimation"
    )

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000),
            repeatMode = RepeatMode.Reverse
        ), label = "scaleAnimation"
    )

    LaunchedEffect(animationRunning) {
        delay(DELAY_SPLASH_SCREEN_MS)
        animationRunning = false
        navigateToAppList()
    }

    Scaffold { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (animationRunning) {
                Icon(
                    painter = painterResource(id = R.drawable.splash_logo_24),
                    contentDescription = null,
                    tint = animatedColor,
                    modifier = Modifier
                        .testTag(TAG_SPLASH_ICON)
                        .scale(scale)
                        .size(64.dp)
                )
            }
        }
    }
}

const val TAG_SPLASH_ICON = "splashIcon"