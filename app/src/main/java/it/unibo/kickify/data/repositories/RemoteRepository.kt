package it.unibo.kickify.data.repositories

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import it.unibo.kickify.data.database.Trip

class RemoteRepository(
    private val httpClient: HttpClient
) {
    companion object {
        private const val BASE_URL = "http://kickify.altervista.org/"
        private const val TRIPS_ENDPOINT = "$BASE_URL/trips"
    }

    suspend fun fetchTrips(): Result<List<Trip>> {
        return try {
            val trips: List<Trip> = httpClient.get(TRIPS_ENDPOINT).body()
            Result.success(trips)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveTrip(trip: Trip): Result<Trip> {
        return try {
            val response: Trip = if (trip.id == 0) {
                // Creare nuovo
                httpClient.post(TRIPS_ENDPOINT) {
                    contentType(ContentType.Application.Json)
                    setBody(trip)
                }.body()
            } else {
                // Aggiornare esistente
                httpClient.put("$TRIPS_ENDPOINT/${trip.id}") {
                    contentType(ContentType.Application.Json)
                    setBody(trip)
                }.body()
            }
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteTrip(trip: Trip): Result<Boolean> {
        return try {
            httpClient.delete("$TRIPS_ENDPOINT/${trip.id}")
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun syncTrips(localTrips: List<Trip>): Result<List<Trip>> {
        return try {
            val response: List<Trip> = httpClient.post("$TRIPS_ENDPOINT/sync") {
                contentType(ContentType.Application.Json)
                setBody(localTrips)
            }.body()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchRemoteData(): Result<Any> {
        return try {
            val response: Any = httpClient.get("$BASE_URL/data").body()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}