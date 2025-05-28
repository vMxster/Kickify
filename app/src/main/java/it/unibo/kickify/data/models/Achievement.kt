package it.unibo.kickify.data.models

data class Achievement(
    val id: Int,
    val titleResId: Int,
    val descriptionResId: Int,
    val resourceIconID: Int,
    val secretAchievement: Boolean,
    var achieved: Boolean,
    var achievedDate: String?
)