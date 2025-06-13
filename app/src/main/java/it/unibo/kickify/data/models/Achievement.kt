package it.unibo.kickify.data.models

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class Achievement(
    val id: Int,
    @StringRes val titleResId: Int,
    @StringRes val descriptionResId: Int,
    @DrawableRes val resourceIconID: Int,
    val secretAchievement: Boolean,
    val achieved: Boolean,
    val achievedDate: String?
)