package com.nammasanthe.ledger.presentation.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nammasanthe.ledger.R
import com.nammasanthe.ledger.ui.theme.ClayBrown
import com.nammasanthe.ledger.ui.theme.ForestGreen
import com.nammasanthe.ledger.ui.theme.WarmCream
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onFinished: () -> Unit
) {
    val scale = remember { Animatable(0.72f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scale.animateTo(1f, animationSpec = tween(900, easing = FastOutSlowInEasing))
        alpha.animateTo(1f, animationSpec = tween(900, easing = FastOutSlowInEasing))
        delay(650)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(ForestGreen, ClayBrown, WarmCream)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_app_logo),
                contentDescription = null,
                modifier = Modifier
                    .size(128.dp)
                    .scale(scale.value)
                    .alpha(alpha.value),
                tint = WarmCream
            )
            Text(
                text = "Namma-Santhe Ledger",
                style = MaterialTheme.typography.headlineMedium,
                color = WarmCream,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.alpha(alpha.value)
            )
            Text(
                text = "Fast village credit tracking, even offline.",
                style = MaterialTheme.typography.bodyLarge,
                color = WarmCream.copy(alpha = 0.92f),
                modifier = Modifier.alpha(alpha.value)
            )
        }
    }
}
