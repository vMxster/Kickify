package it.unibo.kickify.ui.screens.achievements

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
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
import it.unibo.kickify.ui.theme.BluePrimary

@Composable
fun AchievementsScreen(
    navController: NavController,
    achievementsViewModel: AchievementsViewModel
) {
    val achievements by achievementsViewModel.achievements.collectAsStateWithLifecycle()
    val secretAchievements by remember { mutableStateOf(achievements.filter { it.secretAchievement }) }
    val unlockedSecretAchievements by remember { mutableStateOf(secretAchievements.filter { !it.achieved }) }
    val stdAchievements by remember { mutableStateOf(achievements.filter { !it.secretAchievement }) }

    ScreenTemplate(
        screenTitle = stringResource(R.string.myAchievements),
        navController = navController,
        showTopAppBar = true,
        bottomAppBarContent = { BottomBar(navController) },
        showModalDrawer = true,
        achievementsViewModel = achievementsViewModel
    ) {
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp).padding(vertical = 8.dp)
        ) {
            item {
                Text(
                    text = stringResource(R.string.allAchievements),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth().padding(10.dp)
                )
            }

            // show standard achievements
            items(stdAchievements) { achievement ->
                AchievementRow(achievement = achievement)
            }

            // show secret achievements only when unlocked
            if(secretAchievements.isNotEmpty()){
                item {
                    Text(
                        text = stringResource(R.string.secretAchievements),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.fillMaxWidth().padding(10.dp)
                    )
                }
                item {
                    Text(text =
                            if(unlockedSecretAchievements.isEmpty())
                                stringResource(R.string.allSecretAchievementsUnlocked)
                            else pluralStringResource(
                                id = R.plurals.secretAchievementsHint,
                                count = unlockedSecretAchievements.size,
                                formatArgs = arrayOf(unlockedSecretAchievements.size)
                            ),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.fillMaxWidth().padding(10.dp)
                    )
                }
                items(secretAchievements){ achievement ->
                    if(achievement.achieved) {
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

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = stringResource(achievement.titleResId),
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp)
            )
            Text(
                text = stringResource(achievement.descriptionResId),
                style = TextStyle(fontSize = 15.sp)
            )
            Text(
                text = if (!achievement.achieved) stringResource(R.string.notAchieved_achievement)
                else stringResource(R.string.achieved_achievement) + " ${achievement.achievedDate}",
                style = TextStyle(
                    fontSize = 15.sp,
                    color = if (!achievement.achieved) Color.Red else BluePrimary
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
            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            modifier = Modifier.size(48.dp)
        )
    } else {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "",
            modifier = Modifier.size(48.dp),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
        )
    }
}