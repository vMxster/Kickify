package it.unibo.kickify.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import it.unibo.kickify.data.models.Achievement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AchievementsRepository (
    private val dataStore: DataStore<Preferences>,
) {
    private val achievementPrefix = "achievement_"

    // key to store if achievement is achieved
    private fun getAchievedKey(id: Int) = booleanPreferencesKey("${achievementPrefix}${id}_achieved")
    // key to store achievement date
    private fun getAchievedDateKey(id: Int) = stringPreferencesKey("${achievementPrefix}${id}_date")

    suspend fun saveAchievementState(achievement: Achievement) {
        dataStore.edit { preferences ->
            preferences[getAchievedKey(achievement.id)] = achievement.achieved
            preferences[getAchievedDateKey(achievement.id)] = achievement.achievedDate ?: ""
        }
    }

    fun loadAchievementStates(): Flow<Map<Int, Pair<Boolean, String?>>> {
        return dataStore.data.map { preferences ->
            val loadedStates = mutableMapOf<Int, Pair<Boolean, String?>>()
            preferences.asMap().forEach { (key, value) ->
                if (key.name.startsWith(achievementPrefix)) {
                    val parts = key.name.split("_")
                    if (parts.size >= 3) {
                        val id = parts[1].toIntOrNull()
                        if (id != null) {
                            if (key.name.endsWith("_achieved") && value is Boolean) {
                                val dateKey = getAchievedDateKey(id)
                                val date = preferences[dateKey]
                                loadedStates[id] = Pair(value, date?.takeIf { it.isNotBlank() })
                            }
                        }
                    }
                }
            }
            loadedStates
        }
    }

    suspend fun resetAllAchievementStates(allAchievementIds: List<Int>) {
        dataStore.edit { preferences ->
            for (id in allAchievementIds) {
                preferences.remove(getAchievedKey(id))
                preferences.remove(getAchievedDateKey(id))
            }
        }
    }
}