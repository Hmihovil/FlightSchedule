package io.capsella.flightschedule.storage

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class PrefsManager(context: Context) {

    companion object {

        private val TAG = PrefsManager::class.java.simpleName

        const val PREFS_FILE_NAME = "flight_schedule_preferences"
        const val ORIGIN = "origin"
        const val DESTINATION = "destination"
    }

    private val prefs: SharedPreferences

    init {
        prefs = context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)
    }

    var origin: String
        get() {
            Log.d(TAG, "Prefs - Origin: ${prefs.getString(ORIGIN, "")}")
            return prefs.getString(ORIGIN, "")
        }
        set(origin) {
            prefs.edit().putString(ORIGIN, origin).commit()
            Log.d(TAG, "Prefs - Origin: $origin")
        }

   var destination: String
        get() {
            Log.d(TAG, "Prefs - Destination: ${prefs.getString(DESTINATION, "")}")
            return prefs.getString(DESTINATION, "")
        }
        set(destination) {
            prefs.edit().putString(DESTINATION, destination).commit()
            Log.d(TAG, "Prefs - Destination: $destination")
        }

    fun clearPreferences() {
        prefs.edit().clear().commit()
    }
}