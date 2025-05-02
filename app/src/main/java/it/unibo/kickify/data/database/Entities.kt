package it.unibo.kickify.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity
@Serializable
data class Trip(
    @PrimaryKey(autoGenerate = true)
    @SerialName("id")
    val id: Int = 0,

    @SerialName("name")
    val name: String,

    @SerialName("date")
    val date: String,

    @SerialName("description")
    val description: String,

    @SerialName("imageUrl")
    val imageUri: String? = null
)
