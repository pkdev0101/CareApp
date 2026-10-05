package com.careapp.app.util

import com.careapp.app.data.SavedPlace
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object LocationManager {

    private const val EARTH_RADIUS_KM = 6371.0

    fun calculateDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2) * sin(dLon / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return EARTH_RADIUS_KM * c
    }

    fun isWithinGeofence(
        currentLat: Double, currentLon: Double,
        fenceLat: Double, fenceLon: Double,
        radiusMeters: Int = 300
    ): Boolean {
        val distanceKm = calculateDistance(currentLat, currentLon, fenceLat, fenceLon)
        val distanceMeters = distanceKm * 1000
        return distanceMeters <= radiusMeters
    }

    fun checkSafeZone(
        currentLat: Double, currentLon: Double,
        safePlace: SavedPlace,
        radiusMeters: Int = 300
    ): Pair<Boolean, String> {
        val isInZone = isWithinGeofence(currentLat, currentLon, safePlace.latitude, safePlace.longitude, radiusMeters)
        return if (isInZone) {
            Pair(true, "✓ You are at ${safePlace.name}")
        } else {
            val distanceKm = calculateDistance(currentLat, currentLon, safePlace.latitude, safePlace.longitude)
            Pair(false, "⚠️ ${safePlace.name} is ${String.format("%.1f", distanceKm)} km away")
        }
    }

    fun getGeofenceWarning(safePlaces: List<SavedPlace>): String {
        val demoLat = 32.7157
        val demoLon = -117.1611

        return safePlaces
            .mapNotNull { place ->
                val (isInZone, message) = checkSafeZone(demoLat, demoLon, place)
                if (!isInZone) message else null
            }
            .joinToString("\n")
            .takeIf { it.isNotEmpty() } ?: "✓ All safe zones clear"
    }

    fun formatLocationForEmergency(
        latitude: Double, longitude: Double,
        address: String = "Current location"
    ): String {
        return """
            📍 EMERGENCY LOCATION

            Address: $address
            Latitude: $latitude
            Longitude: $longitude

            Accuracy: ~15 meters
            Last Update: Just now

            Show this to emergency services.
        """.trimIndent()
    }

    fun suggestNearestSafePlace(
        currentLat: Double, currentLon: Double,
        safePlaces: List<SavedPlace>
    ): SavedPlace? {
        return safePlaces.minByOrNull { place ->
            calculateDistance(currentLat, currentLon, place.latitude, place.longitude)
        }
    }
}
