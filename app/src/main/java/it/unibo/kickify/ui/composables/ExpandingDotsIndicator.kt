package it.unibo.kickify.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ExpandingDotIndicator(
    totalDots: Int,
    selectedIndex: Int,
    modifier: Modifier = Modifier,
    dotColor: Color = Color.Gray,
    selectedDotColor: Color = Color.Blue,
    dotSize: Dp = 8.dp,
    selectedDotWidth: Dp = 24.dp,
    dotSpacing: Dp = 8.dp
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(dotSpacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until totalDots) {
            val isSelected = i == selectedIndex
            Box(
                modifier = Modifier
                    .size(if (isSelected) selectedDotWidth else dotSize, dotSize)
                    .clip(RoundedCornerShape(50))
                    .background(if (isSelected) selectedDotColor else dotColor)
            )
        }
    }
}