package it.unibo.kickify.ui.screens.onboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import it.unibo.kickify.R
import it.unibo.kickify.ui.composables.ExpandingDotIndicator

val kickifyFontFamily = FontFamily(
    Font(R.font.alexandria_bold, FontWeight.Bold),
    Font(R.font.alexandria_regular, FontWeight.Normal)
)

@Preview
@Composable
fun PagerWithIndicator() {
    val totalDots = 3
    var selectedIndex by remember { mutableIntStateOf(0) }

    Column (
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ){
        Image(
            painter = when(selectedIndex){
                1 -> painterResource(R.drawable.nike_onboard)
                2 -> painterResource(R.drawable.newbalance_onboard)
                else -> painterResource(R.drawable.adidas_onboard)
            },
            contentDescription = "Welcome page")
        Row{
            Text(when (selectedIndex){
                0 -> stringResource(R.string.onboard_title_1)
                1 -> stringResource(R.string.onboard_title_2)
                2 -> stringResource(R.string.onboard_title_3)
                else -> " "
            }, style = TextStyle(
                fontSize = 24.sp,
                fontFamily = kickifyFontFamily,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        Row{
            Text( when (selectedIndex) { // messages
                0 -> stringResource(R.string.onboard_text_1)
                1 -> stringResource(R.string.onboard_text_2)
                2 -> stringResource(R.string.onboard_text_3)
                else -> " "
            }, style = TextStyle(
                fontSize = 10.sp,
                fontFamily = kickifyFontFamily,
                fontWeight = FontWeight.Normal,
                color = Color.White
            )
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        Row (
            modifier = Modifier.fillMaxWidth(),
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
            }) {
                Text(when(selectedIndex) {
                    0 -> stringResource(R.string.getStarted)
                    else -> stringResource(R.string.nextPage)
                }, style = TextStyle(
                    fontFamily = kickifyFontFamily,
                )
                )
            }
        }
    }
}