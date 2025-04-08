package it.unibo.kickify.ui.screens.onboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.unibo.kickify.R
import it.unibo.kickify.ui.composables.ExpandingDotIndicator
import it.unibo.kickify.ui.theme.KickifyTheme

@Preview
@Composable
fun OnBoardScreen() {
    val totalDots = 3
    var selectedIndex by remember { mutableIntStateOf(0) }

    KickifyTheme {
        Column (
            modifier = Modifier.fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Image(
                painter = when (selectedIndex) {
                    1 -> painterResource(R.drawable.nike_onboard)
                    2 -> painterResource(R.drawable.newbalance_onboard)
                    else -> painterResource(R.drawable.adidas_onboard)
                },
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.FillWidth,
                contentDescription = "Welcome page"
            )
            Text(
                when (selectedIndex) {
                    0 -> stringResource(R.string.onboard_title_1)
                    1 -> stringResource(R.string.onboard_title_2)
                    2 -> stringResource(R.string.onboard_title_3)
                    else -> " "
                }, style = TextStyle(
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
            Spacer(modifier = Modifier.height(16.dp))


            Text(
                when (selectedIndex) { // messages
                    0 -> stringResource(R.string.onboard_text_1)
                    1 -> stringResource(R.string.onboard_text_2)
                    2 -> stringResource(R.string.onboard_text_3)
                    else -> " "
                }, style = TextStyle(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ExpandingDotIndicator(
                    totalDots = totalDots,
                    selectedIndex = selectedIndex,
                    modifier = Modifier.padding(16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    selectedIndex = (selectedIndex + 1) % totalDots
                    if (selectedIndex == (totalDots - 1)) {
                     // when on last page, go to login page
                    }
                }) {
                    Text(
                        when (selectedIndex) {
                            0 -> stringResource(R.string.getStarted_button)
                            else -> stringResource(R.string.nextPage_button)
                        }
                    )
                }
            }
        }
    }
}