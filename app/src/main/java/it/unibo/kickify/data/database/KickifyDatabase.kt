package it.unibo.kickify.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Trip::class], version = 2)
abstract class KickifyDatabase : RoomDatabase() {
    abstract fun tripsDAO(): TripsDAO
}
