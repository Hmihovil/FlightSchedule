package io.capsella.flightschedule.util

class Constants {
    companion object {

        // General
        const val AUTHORIZATION = "Bearer z9zpvp4wa52kve6w3xxmma59"
        const val ID = "id"
        const val ORIGIN = "origin"
        const val DESTINATION = "destination"
        const val AIRPORT_CODE = "airport_code"
        const val STATE = "state"
        const val STATE_SUCCESS = "success"
        const val STATE_ERROR = "error"

        // Urls
        private const val URL_SERVER = "https://api.lufthansa.com/v1/"
        const val URL_COUNTRIES = URL_SERVER + "references/countries/"
        const val URL_CITIES = URL_SERVER + "references/cities/"
        const val URL_AIRPORTS = URL_SERVER + "references/airports/"
        const val URL_FLIGHT_SCHEDULES = URL_SERVER + "operations/schedules/"

        // API
        const val URL_ALL_PHOTOS = "https://us-central1-instanew-60742.cloudfunctions.net/allPhotos"

        //Broadcasts
        const val Broadcast_COMPLETE_AIRPORTS_SYNC = "io.capsella.flightschedule.util.COMPLETE_AIRPORTS_SYNC"
        const val Broadcast_COMPLETE_FLIGHT_SCHEDULES_SYNC = "io.capsella.flightschedule.util.COMPLETE_FLIGHT_SCHEDULES_SYNC"
        const val Broadcast_SET_AIRPORT_VALUES = "io.capsella.flightschedule.util.SET_AIRPORT_VALUES"

        //Permissions
        const val LOAD_SETTINGS = "load_settings"
        const val PERMISSION_CALLBACK_CONSTANT = 100
        const val REQUEST_PERMISSION_SETTING = 101
        const val REQUEST_CHECK_SETTINGS = 102
    }
}