package io.capsella.flightschedule.storage

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject

class Database {

    private val TAG = Database::class.java.simpleName

    private var context: Context
    private var ourHandler: DbHandler? = null
    private var sqliteDatabase: SQLiteDatabase? = null

    constructor(context: Context) {
        this.context = context
        ourHandler = DbHandler(this.context, DATABASE_NAME, null, DATABASE_VERSION)
    }

    companion object {

        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "flight_schedule_database"

        //TABLE NAMES
        const val TABLE_COUNTRIES = "countries"
        const val TABLE_CITIES = "cities"
        const val TABLE_AIRPORTS = "airports"
        const val TABLE_FLIGHT_SCHEDULES = "flight_schedules"

        //COLUMN NAMES
        const val COLUMN_ID = "id"
        const val COLUMN_AIRPORT_CODE = "airport_code"
        const val COLUMN_NAME = "name"
        const val COLUMN_LATITUDE = "latitude"
        const val COLUMN_LONGITUDE = "longitude"
        const val COLUMN_CITY_CODE = "city_code"
        const val COLUMN_COUNTRY_CODE = "country_code"
        const val COLUMN_DURATION = "duration"
        const val COLUMN_AIRPORT_CODE_DEPARTURE = "airport_code_departure"
        const val COLUMN_LOCAL_TIME_DEPARTURE = "local_time_departure"
        const val COLUMN_AIRPORT_CODE_ARRIVAL = "airport_code_arrival"
        const val COLUMN_LOCAL_TIME_ARRIVAL = "local_time_arrival"
        const val COLUMN_FLIGHT_NUMBER = "flight_number"
    }

    private inner class DbHandler(context: Context, DATABASE_NAME: String, k: SQLiteDatabase.CursorFactory?, DATABASE_VERSION: Int)
        : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

        override fun onCreate(db: SQLiteDatabase) {
            // TODO Auto-generated method stub
            createDatabases(db)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            // TODO Auto-generated method stub
            dropDatabases(db)
            onCreate(db)
        }
    }

    @Throws(SQLiteConstraintException::class)
    fun createDatabases(db: SQLiteDatabase) {

        //COUNTRIES TABLE
        db.execSQL("CREATE TABLE $TABLE_COUNTRIES ( " +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_COUNTRY_CODE TEXT NOT NULL, " +
                "$COLUMN_NAME TEXT NOT NULL);")

        //CITIES TABLE
        db.execSQL("CREATE TABLE $TABLE_CITIES ( " +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_CITY_CODE TEXT NOT NULL, " +
                "$COLUMN_COUNTRY_CODE TEXT NOT NULL, " +
                "$COLUMN_LATITUDE TEXT NOT NULL, " +
                "$COLUMN_LONGITUDE TEXT NOT NULL, " +
                "$COLUMN_NAME TEXT NOT NULL);")

        //AIRPORTS TABLE
        db.execSQL("CREATE TABLE $TABLE_AIRPORTS ( " +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_AIRPORT_CODE TEXT NOT NULL, " +
                "$COLUMN_NAME TEXT NOT NULL, " +
                "$COLUMN_LATITUDE TEXT NOT NULL, " +
                "$COLUMN_LONGITUDE TEXT NOT NULL, " +
                "$COLUMN_CITY_CODE TEXT NOT NULL, " +
                "$COLUMN_COUNTRY_CODE TEXT NOT NULL);")

        //FLIGHT SCHEDULES TABLE
        db.execSQL("CREATE TABLE $TABLE_FLIGHT_SCHEDULES ( " +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_DURATION TEXT NOT NULL, " +
                "$COLUMN_AIRPORT_CODE_DEPARTURE TEXT NOT NULL, " +
                "$COLUMN_LOCAL_TIME_DEPARTURE TEXT NOT NULL, " +
                "$COLUMN_AIRPORT_CODE_ARRIVAL TEXT NOT NULL, " +
                "$COLUMN_LOCAL_TIME_ARRIVAL TEXT NOT NULL, " +
                "$COLUMN_FLIGHT_NUMBER TEXT NOT NULL);")
    }

    @Throws(SQLiteConstraintException::class)
    fun dropDatabases(db: SQLiteDatabase) {

        db.execSQL("DROP TABLE IF EXISTS $TABLE_COUNTRIES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CITIES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_AIRPORTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_FLIGHT_SCHEDULES")
    }

    @Throws(SQLiteConstraintException::class)
    fun open(): Database {

        if (sqliteDatabase == null) {
            sqliteDatabase = ourHandler!!.writableDatabase
        } else if (!sqliteDatabase!!.isOpen) {
            sqliteDatabase = ourHandler!!.writableDatabase
        }

        return this
    }

    fun getSQLiteDatabase(): SQLiteDatabase {
        return sqliteDatabase!!
    }

    @Throws(SQLiteConstraintException::class)
    fun close() {
        sqliteDatabase!!.close()
    }

    @Throws(SQLiteConstraintException::class)
    fun clear() {
        sqliteDatabase!!.delete(TABLE_COUNTRIES, null, null)
        sqliteDatabase!!.delete(TABLE_CITIES, null, null)
        sqliteDatabase!!.delete(TABLE_AIRPORTS, null, null)
        sqliteDatabase!!.delete(TABLE_FLIGHT_SCHEDULES, null, null)
    }

    @Throws(SQLiteConstraintException::class)
    fun clear(table: String) {
        when (table) {
            TABLE_COUNTRIES -> {
                sqliteDatabase!!.delete(TABLE_COUNTRIES, null, null)
            }
            TABLE_CITIES -> {
                sqliteDatabase!!.delete(TABLE_CITIES, null, null)
            }
            TABLE_AIRPORTS -> {
                sqliteDatabase!!.delete(TABLE_AIRPORTS, null, null)
            }
            TABLE_FLIGHT_SCHEDULES -> {
                sqliteDatabase!!.delete(TABLE_FLIGHT_SCHEDULES, null, null)
            }
        }
    }

    fun saveCountries(jsonArray: JSONArray): Boolean {
        try {

            val cursor = sqliteDatabase!!.query(TABLE_COUNTRIES, null, null, null, null, null, null)
            sqliteDatabase!!.beginTransaction()

            for (i in 0 until jsonArray.length()) {
                Log.d(TAG, "Saving ${i + 1} of ${jsonArray.length()} countries")
                insertCountry(jsonArray.getJSONObject(i))
            }

            sqliteDatabase!!.setTransactionSuccessful()
            sqliteDatabase!!.endTransaction()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    private fun insertCountry(jsonObject: JSONObject): Boolean {

        return try {

            val values = ContentValues()
            values.put(COLUMN_COUNTRY_CODE, jsonObject.getString("CountryCode"))
            var name = jsonObject.getString("CountryCode")
            if (jsonObject.getJSONObject("Names").get("Name") is JSONObject) {
                val names: JSONObject = jsonObject.getJSONObject("Names").getJSONObject("Name")
                name = names.getString("$")
            } else {
                val names: JSONArray = jsonObject.getJSONObject("Names").getJSONArray("Name")
                for (i in 0 until names.length()) {
                    if (names.getJSONObject(i).getString("@LanguageCode") == "en") {
                        name = names.getJSONObject(i).getString("$")
                    }
                }
            }
            values.put(COLUMN_NAME, name)

            val id = sqliteDatabase!!.insertOrThrow(TABLE_COUNTRIES, null, values)
            Log.d(TAG, "Country $name - ${jsonObject.getString("CountryCode")} saved.")

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun saveCities(jsonArray: JSONArray): Boolean {
        try {

            val cursor = sqliteDatabase!!.query(TABLE_CITIES, null, null, null, null, null, null)
            sqliteDatabase!!.beginTransaction()

            for (i in 0 until jsonArray.length()) {
                Log.d(TAG, "Saving ${i + 1} of ${jsonArray.length()} cities")
                insertCity(jsonArray.getJSONObject(i))
            }

            sqliteDatabase!!.setTransactionSuccessful()
            sqliteDatabase!!.endTransaction()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    private fun insertCity(jsonObject: JSONObject): Boolean {

        return try {

            val values = ContentValues()
            values.put(COLUMN_CITY_CODE, jsonObject.getString("CityCode"))
            values.put(COLUMN_COUNTRY_CODE, jsonObject.getString("CountryCode"))
            if (jsonObject.has("Position")) {
                values.put(COLUMN_LATITUDE, jsonObject.getJSONObject("Position").getJSONObject("Coordinate").getDouble("Latitude").toString())
                values.put(COLUMN_LONGITUDE, jsonObject.getJSONObject("Position").getJSONObject("Coordinate").getDouble("Longitude").toString())
            } else {
                values.put(COLUMN_LATITUDE, "")
                values.put(COLUMN_LONGITUDE, "")
            }
            var name = jsonObject.getString("CityCode")
            if (jsonObject.getJSONObject("Names").get("Name") is JSONObject) {
                val names: JSONObject = jsonObject.getJSONObject("Names").getJSONObject("Name")
                name = names.getString("$")
            } else {
                val names: JSONArray = jsonObject.getJSONObject("Names").getJSONArray("Name")
                for (i in 0 until names.length()) {
                    if (names.getJSONObject(i).getString("@LanguageCode") == "en") {
                        name = names.getJSONObject(i).getString("$")
                    }
                }
            }
            values.put(COLUMN_NAME, name)

            val id = sqliteDatabase!!.insertOrThrow(TABLE_CITIES, null, values)
            Log.d(TAG, "City $name - ${jsonObject.getString("CityCode")} saved.")

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun saveAirports(jsonArray: JSONArray): Boolean {
        try {

            val cursor = sqliteDatabase!!.query(TABLE_AIRPORTS, null, null, null, null, null, null)
            sqliteDatabase!!.beginTransaction()

            for (i in 0 until jsonArray.length()) {
                Log.d(TAG, "Saving ${i + 1} of ${jsonArray.length()} airports")
                insertAirport(jsonArray.getJSONObject(i))
            }

            sqliteDatabase!!.setTransactionSuccessful()
            sqliteDatabase!!.endTransaction()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    private fun insertAirport(jsonObject: JSONObject): Boolean {

        return try {

            val values = ContentValues()
            values.put(COLUMN_AIRPORT_CODE, jsonObject.getString("AirportCode"))
            var name = jsonObject.getString("AirportCode")
            if (jsonObject.getJSONObject("Names").get("Name") is JSONObject) {
                val names: JSONObject = jsonObject.getJSONObject("Names").getJSONObject("Name")
                name = names.getString("$")
            } else {
                val names: JSONArray = jsonObject.getJSONObject("Names").getJSONArray("Name")
                for (i in 0 until names.length()) {
                    if (names.getJSONObject(i).getString("@LanguageCode") == "en") {
                        name = names.getJSONObject(i).getString("$")
                    }
                }
            }
            values.put(COLUMN_NAME, name)
            if (jsonObject.has("Position")) {
                values.put(COLUMN_LATITUDE, jsonObject.getJSONObject("Position").getJSONObject("Coordinate").getDouble("Latitude").toString())
                values.put(COLUMN_LONGITUDE, jsonObject.getJSONObject("Position").getJSONObject("Coordinate").getDouble("Longitude").toString())
            } else {
                values.put(COLUMN_LATITUDE, "")
                values.put(COLUMN_LONGITUDE, "")
            }
            values.put(COLUMN_CITY_CODE, jsonObject.getString("CityCode"))
            values.put(COLUMN_COUNTRY_CODE, jsonObject.getString("CountryCode"))

            val id = sqliteDatabase!!.insertOrThrow(TABLE_AIRPORTS, null, values)
            Log.d(TAG, "Airport $name saved.")

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun saveFlightSchedules(jsonArray: JSONArray): Boolean {
        try {

            val cursor = sqliteDatabase!!.query(TABLE_FLIGHT_SCHEDULES, null, null, null, null, null, null)
            sqliteDatabase!!.beginTransaction()

            for (i in 0 until jsonArray.length()) {
                Log.d(TAG, "Saving ${i + 1} of ${jsonArray.length()} flight schedule")
                insertFlightSchedule(jsonArray.getJSONObject(i))
            }

            sqliteDatabase!!.setTransactionSuccessful()
            sqliteDatabase!!.endTransaction()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    private fun insertFlightSchedule(jsonObject: JSONObject): Boolean {

        return try {

            val values = ContentValues()
            values.put(COLUMN_DURATION, jsonObject.getJSONObject("TotalJourney").getString("Duration"))
            if(jsonObject.get("Flight") is JSONArray){

                val jArray = jsonObject.getJSONArray("Flight")
                for (i in 0 until jArray.length()) {
                    Log.d(TAG, "Saving ${i + 1} of ${jArray.length()} flight schedule")

                    val v = ContentValues()
                    values.put(COLUMN_DURATION, jsonObject.getJSONObject("TotalJourney").getString("Duration"))
                    values.put(COLUMN_AIRPORT_CODE_DEPARTURE, jArray.getJSONObject(i).getJSONObject("Departure").getString("AirportCode"))
                    values.put(COLUMN_LOCAL_TIME_DEPARTURE, jArray.getJSONObject(i).getJSONObject("Departure").getJSONObject("ScheduledTimeLocal").getString("DateTime"))
                    values.put(COLUMN_AIRPORT_CODE_ARRIVAL, jArray.getJSONObject(i).getJSONObject("Arrival").getString("AirportCode"))
                    values.put(COLUMN_LOCAL_TIME_ARRIVAL, jArray.getJSONObject(i).getJSONObject("Arrival").getJSONObject("ScheduledTimeLocal").getString("DateTime"))
                    val flightNumber = jArray.getJSONObject(i).getJSONObject("MarketingCarrier").getString("FlightNumber")
                    values.put(COLUMN_FLIGHT_NUMBER, flightNumber)

                    val id = sqliteDatabase!!.insertOrThrow(TABLE_FLIGHT_SCHEDULES, null, values)
                    Log.d(TAG, "Flight Number $flightNumber saved.")
                }
            }else{
                values.put(COLUMN_AIRPORT_CODE_DEPARTURE, jsonObject.getJSONObject("Flight").getJSONObject("Departure").getString("AirportCode"))
                values.put(COLUMN_LOCAL_TIME_DEPARTURE, jsonObject.getJSONObject("Flight").getJSONObject("Departure").getJSONObject("ScheduledTimeLocal").getString("DateTime"))
                values.put(COLUMN_AIRPORT_CODE_ARRIVAL, jsonObject.getJSONObject("Flight").getJSONObject("Arrival").getString("AirportCode"))
                values.put(COLUMN_LOCAL_TIME_ARRIVAL, jsonObject.getJSONObject("Flight").getJSONObject("Arrival").getJSONObject("ScheduledTimeLocal").getString("DateTime"))
                val flightNumber = jsonObject.getJSONObject("Flight").getJSONObject("MarketingCarrier").getString("FlightNumber")
                values.put(COLUMN_FLIGHT_NUMBER, flightNumber)

                val id = sqliteDatabase!!.insertOrThrow(TABLE_FLIGHT_SCHEDULES, null, values)
                Log.d(TAG, "Flight Number $flightNumber saved.")
            }

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
