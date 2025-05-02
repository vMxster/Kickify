package it.unibo.kickify.data.repositories

import android.content.ContentResolver
import it.unibo.kickify.data.database.Trip
import it.unibo.kickify.data.database.TripsDAO
import kotlinx.coroutines.flow.Flow

class LocalRepository(
    private val dao: TripsDAO,
    private val contentResolver: ContentResolver
) {
    fun getAllTrips(): Flow<List<Trip>> = dao.getAll()

    suspend fun saveTrip(trip: Trip) = dao.upsert(trip)

    suspend fun deleteTrip(trip: Trip) = dao.delete(trip)
}