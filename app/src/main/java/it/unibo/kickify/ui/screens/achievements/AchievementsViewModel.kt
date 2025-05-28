package it.unibo.kickify.ui.screens.achievements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.unibo.kickify.R
import it.unibo.kickify.data.models.Achievement
import it.unibo.kickify.data.repositories.AchievementsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Date
import java.util.Locale

class AchievementsViewModel(
    private val repository: AchievementsRepository
) : ViewModel() {

    private val predefinedAchievements = listOf(
        Achievement(
            id = 1,
            titleResId = R.string.achievement_1stAppLogin,
            descriptionResId = R.string.achievement_1stAppLogin_descr,
            resourceIconID = R.drawable.ach_login,
            secretAchievement = false,
            achieved = false,
            achievedDate = null
        ),
        Achievement(
            id = 2,
            titleResId = R.string.achievement_1stReview,
            descriptionResId = R.string.achievement_1stReview_descr,
            resourceIconID = R.drawable.ach_review,
            secretAchievement = false,
            achieved = false,
            achievedDate = null
        ),
        Achievement(
            id = 3,
            titleResId = R.string.achievement_1stShareLink,
            descriptionResId = R.string.achievement_1stShareLink_descr,
            resourceIconID = R.drawable.ach_share,
            secretAchievement = false,
            achieved = false,
            achievedDate = null
        ),
        Achievement(
            id = 4,
            titleResId = R.string.achievement_1stOrder,
            descriptionResId = R.string.achievement_1stOrder_descr,
            resourceIconID = R.drawable.ach_order,
            secretAchievement = false,
            achieved = false,
            achievedDate = null
        ),
        Achievement(
            id = 5,
            titleResId = R.string.achievement_1stItemInWishlist,
            descriptionResId = R.string.achievement_1stItemInWishlist_descr,
            resourceIconID = R.drawable.ach_wishlist,
            secretAchievement = false,
            achieved = false,
            achievedDate = null
        ),
        Achievement(
            id = 6,
            titleResId = R.string.achievement_onlyDarkTheme,
            descriptionResId = R.string.achievement_onlyDarkTheme_descr,
            resourceIconID = R.drawable.ach_darktheme,
            secretAchievement = true,
            achieved = false,
            achievedDate = null
        ),
        Achievement(
            id = 7,
            titleResId = R.string.achievement_romaCaputMundi,
            descriptionResId = R.string.achievement_romaCaputMundi_descr,
            resourceIconID = R.drawable.review_light,
            secretAchievement = true,
            achieved = false,
            achievedDate = null
        )
    )

    // to reset only achievements
    private val allPredefinedAchievementIds = predefinedAchievements.map { it.id }

    private val _achievements = MutableStateFlow(predefinedAchievements)
    val achievements: StateFlow<List<Achievement>> = _achievements.asStateFlow()

    init {
        loadAchievements()
    }

    private fun loadAchievements() {
        viewModelScope.launch {
            repository.loadAchievementStates().collect { loadedStates ->
                val updatedAchievements = predefinedAchievements.map { predefined ->
                    val (achievedStatus, achievedDate) = loadedStates.getOrDefault(predefined.id, false to null)
                    predefined.copy(achieved = achievedStatus, achievedDate = achievedDate)
                }.toMutableList()
                _achievements.value = updatedAchievements
            }
        }
    }

    private fun getTodayDate(): String{
        val dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.getDefault())
        return dateFormat.format(Date())
    }

    fun achieveAchievement(achievementId: Int) {
        viewModelScope.launch {
            _achievements.update { currentAchievements ->
                val updatedList = currentAchievements.toMutableList()
                val achievementIndex = updatedList.indexOfFirst { it.id == achievementId }

                if (achievementIndex != -1) {
                    val achievementToUpdate = updatedList[achievementIndex]
                    if (!achievementToUpdate.achieved) {
                        val currentDate = getTodayDate()
                        val updatedAchievement = achievementToUpdate.copy(
                            achieved = true,
                            achievedDate = currentDate
                        )
                        updatedList[achievementIndex] = updatedAchievement
                        repository.saveAchievementState(updatedAchievement)
                    }
                }
                updatedList
            }
        }
    }

    fun resetAllAchievements() {
        viewModelScope.launch {
            repository.resetAllAchievementStates(allPredefinedAchievementIds)
            loadAchievements()
        }
    }

    fun getAchievementById(id: Int): Achievement? {
        return _achievements.value.find { it.id == id }
    }
}