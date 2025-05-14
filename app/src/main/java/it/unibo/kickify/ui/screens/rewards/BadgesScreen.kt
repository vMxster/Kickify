package it.unibo.kickify.ui.screens.rewards

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import it.unibo.kickify.R
import it.unibo.kickify.ui.composables.BottomBar
import it.unibo.kickify.ui.composables.ScreenTemplate
import java.time.LocalDate

data class Achievement(
    val title: String,
    val description: String,
    val resourceIconID: Int,
    var achieved: Boolean,
    var achievedDate: String
)

fun getTodayDate(): String{
    return LocalDate.now().toString()
}

fun getDateFrom(year: Int, month: Int, day: Int): String{
    return LocalDate.of(year, month, day).toString()
}

@Composable
fun BadgeScreen(
    navController: NavController
) {
    ScreenTemplate(
        screenTitle = stringResource(R.string.myAchievements),
        navController = navController,
        showTopAppBar = true,
        bottomAppBarContent = { BottomBar(navController) },
        showModalDrawer = true
    ) { contentPadding ->
        val scrollState = rememberScrollState()

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .verticalScroll(scrollState)
        ) {
            val achList = listOf(
                Achievement(
                    title = stringResource(R.string.achievement_1stAppLogin),
                    description = stringResource(R.string.achievement_1stAppLogin_descr),
                    resourceIconID = R.drawable.ach_login,
                    achieved = true,
                    achievedDate = getTodayDate()
                ),
                Achievement(
                    title = stringResource(R.string.achievement_1stReview),
                    description = stringResource(R.string.achievement_1stReview_descr),
                    resourceIconID = R.drawable.ach_review,
                    achieved = true,
                    achievedDate = getDateFrom(year = 2025, month = 5, day = 10)
                ),
                Achievement(
                    title = stringResource(R.string.achievement_1stShareLink),
                    description = stringResource(R.string.achievement_1stShareLink_descr),
                    resourceIconID = R.drawable.ach_share,
                    achieved = false,
                    achievedDate = ""
                ),
                Achievement(
                    title = stringResource(R.string.achievement_1stOrder),
                    description = stringResource(R.string.achievement_1stOrder_descr),
                    resourceIconID = R.drawable.ach_order,
                    achieved = false,
                    achievedDate = ""
                ),
                Achievement(
                    title = stringResource(R.string.achievement_1stItemInWishlist),
                    description = stringResource(R.string.achievement_1stItemInWishlist_descr),
                    resourceIconID = R.drawable.ach_wishlist,
                    achieved = false,
                    achievedDate = ""
                ),
                Achievement(
                    title = stringResource(R.string.achievement_onlyDarkTheme),
                    description = stringResource(R.string.achievement_onlyDarkTheme_descr),
                    resourceIconID = R.drawable.ach_darktheme,
                    achieved = false,
                    achievedDate = ""
                )
            )
            val achievments = remember { mutableStateOf(achList)}

            BadgesCardContainer(cardTitle = stringResource(R.string.allAchievements)) {
                achievments.value.forEach { achievement ->
                    AchievementRow(ach = achievement,
                        onAchieved = { /* update achieved achievements */ }
                    )
                }
            }
        }
    }
}

@Composable
fun BadgesCardContainer(
    cardTitle: String,
    badges: @Composable () -> Unit
){
    Card(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 8.dp).padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 8.dp)
                .padding(vertical = 6.dp)
        ) {
            Text(
                text = cardTitle,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth().padding(10.dp)
            )
        }
        badges()
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
fun GreyedOutImage(imageRes: Int, achieved: Boolean) {
    if(!achieved) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = "",
            tint = Color.White.copy(alpha = 0.7f),
            modifier = Modifier
                .size(48.dp)
        )
    } else {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "",
            modifier = Modifier.size(48.dp)
        )
    }
}

@Composable
fun AchievementRow(ach: Achievement, onAchieved: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GreyedOutImage(imageRes = ach.resourceIconID, achieved = ach.achieved)
        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = ach.title,
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp)
            )
            Text(
                text = ach.description, style = TextStyle(fontSize = 14.sp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if(!ach.achieved) stringResource(R.string.notAchieved_achievement)
                else stringResource(R.string.achieved_achievement) + " ${ach.achievedDate}",
                style = TextStyle(
                    fontSize = 12.sp,
                    color = if(!ach.achieved) Color.Red else Color.Green
                )
            )
            onAchieved(true)
        }
    }
}