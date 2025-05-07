package it.unibo.kickify.data.repositories

import it.unibo.kickify.data.database.Trip
import kotlinx.coroutines.flow.Flow

class RepositoryHandler(
    private val localRepository: LocalRepository,
    private val remoteRepository: RemoteRepository
) {
    // Delegazioni a LocalRepository
    fun getAllTrips(): Flow<List<Trip>> = localRepository.getAllTrips()
    suspend fun saveTrip(trip: Trip) = localRepository.saveTrip(trip)
    suspend fun deleteTrip(trip: Trip) = localRepository.deleteTrip(trip)

    // Delegazioni a RemoteRepository
    //suspend fun syncTrips() = remoteRepository.syncTrips()
    suspend fun fetchRemoteData() = remoteRepository.fetchRemoteData()
    suspend fun fetchTrips(): Result<List<Trip>> = remoteRepository.fetchTrips()
    suspend fun syncTripsWithServer(trips: List<Trip>): Result<List<Trip>> =
        remoteRepository.syncTrips(trips)
    suspend fun saveRemoteTrip(trip: Trip): Result<Trip> = remoteRepository.saveTrip(trip)
    suspend fun deleteRemoteTrip(trip: Trip): Result<Boolean> = remoteRepository.deleteTrip(trip)
}