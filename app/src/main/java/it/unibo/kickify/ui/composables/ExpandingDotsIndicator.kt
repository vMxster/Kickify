package it.unibo.kickify.ui.composables

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import it.unibo.kickify.ui.theme.BluePrimary

@Composable
fun PageIndicator(
    numberOfPages: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
    dotColor: Color = Color.Gray,
    selectedDotColor: Color = BluePrimary,
    selectedDotWidth: Dp = 24.dp,
    dotHeight: Dp = 8.dp,
    dotWidth: Dp = 10.dp,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Start
    ) {
        for (index in 0 until numberOfPages) {
            val isSelected = index == currentPage
            val color = if (isSelected) selectedDotColor else dotColor

            // selected dot expanding animation
            val width = animateDpAsState(
                targetValue = if (isSelected) selectedDotWidth else dotWidth,
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
            )
            val height = animateDpAsState(
                targetValue = dotHeight,
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
            )

            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(color)
                    .size(width = width.value, height = height.value)
            )
        }
    }
}