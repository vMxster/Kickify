package it.unibo.kickify.ui.screens.achievements

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import it.unibo.kickify.R
import it.unibo.kickify.data.models.Achievement
import it.unibo.kickify.ui.composables.BottomBar
import it.unibo.kickify.ui.composables.ScreenTemplate

@Composable
fun AchievementsScreen(
    navController: NavController,
    achievementsViewModel: AchievementsViewModel
) {
    val achievements by achievementsViewModel.achievements.collectAsStateWithLifecycle()

    ScreenTemplate(
        screenTitle = stringResource(R.string.myAchievements),
        navController = navController,
        showTopAppBar = true,
        bottomAppBarContent = { BottomBar(navController) },
        showModalDrawer = true
    ) {
        Card(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 8.dp).padding(vertical = 8.dp)
        ) {
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Text(
                        text = stringResource(R.string.allAchievements),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.fillMaxWidth().padding(10.dp)
                    )
                }
                items(achievements) { achievement ->
                    // hide secret achievements not yet achieved
                    if(!achievement.secretAchievement || achievement.achieved) {
                        AchievementRow(achievement = achievement)
                    }
                }
            }
        }
    }
}

@Composable
fun AchievementRow(achievement: Achievement) {
    Row(
        modifier = Modifier.padding(16.dp).fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AchievementImage(imageRes = achievement.resourceIconID, achieved = achievement.achieved)
        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = stringResource(achievement.titleResId),
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp)
            )
            Text(
                text = stringResource(achievement.descriptionResId),
                style = TextStyle(fontSize = 14.sp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (!achievement.achieved) stringResource(R.string.notAchieved_achievement)
                else stringResource(R.string.achieved_achievement) + " ${achievement.achievedDate}",
                style = TextStyle(
                    fontSize = 12.sp,
                    color = if (!achievement.achieved) Color.Red else Color.Green
                )
            )
        }
    }
}

@Composable
fun AchievementImage(imageRes: Int, achieved: Boolean) {
    if (!achieved) {
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