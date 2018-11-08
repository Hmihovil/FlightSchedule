package io.capsella.flightschedule.dao

import android.content.Context
import android.content.Intent
import android.util.Log
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import io.capsella.flightschedule.storage.Database
import io.capsella.flightschedule.model.FlightSchedule
import io.capsella.flightschedule.util.Constants
import io.capsella.flightschedule.util.HelperFunctions
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import com.android.volley.DefaultRetryPolicy


class FlightSceduleDao {

    private val TAG = FlightSceduleDao::class.java.simpleName
    private var context: Context
    private var database: Database? = null

    constructor(context: Context) {
        this.context = context
        this.database = Database(context)
    }

    fun fetchFlightSchedules(originAirportCode: String, destinationAirportCode: String) {

        "https://api.lufthansa.com/v1/operations/schedules/ZRH/FRA/2018-11-07?directFlights=0&limit=100&offset=0"
        val date = HelperFunctions.getCurrentTimeStamp(false)
        val url = "${Constants.URL_FLIGHT_SCHEDULES}$originAirportCode/$destinationAirportCode/$date"
        Log.d(TAG, "Url - Get Flight Schedules: $url")

        val directFlights = "0"
        val limit = "100"
        val offset = "0"
        val params: HashMap<String, String> = HashMap()
        params["directFlights"] = directFlights
        params["limit"] = limit
        params["offset"] = offset

        val request = object : StringRequest(Request.Method.GET, HelperFunctions.generateGETUrl(url, params), Response.Listener { response ->

            try {
                val jsonObject = JSONObject(response.toString())
                Log.d(TAG, "Flight Schedule Return JSON \n----------$jsonObject")

                database!!.open().clear(Database.TABLE_FLIGHT_SCHEDULES)
                database!!.open().saveFlightSchedules(jsonObject.getJSONObject("ScheduleResource").getJSONArray("Schedule"))
                database!!.close()

                context.sendBroadcast(Intent(Constants.Broadcast_COMPLETE_FLIGHT_SCHEDULES_SYNC).putExtra(Constants.STATE, Constants.STATE_SUCCESS))

            } catch (e: IOException) {
                context.sendBroadcast(Intent(Constants.Broadcast_COMPLETE_FLIGHT_SCHEDULES_SYNC).putExtra(Constants.STATE, Constants.STATE_ERROR))
                e.printStackTrace()
            } catch (e: JSONException) {
                context.sendBroadcast(Intent(Constants.Broadcast_COMPLETE_FLIGHT_SCHEDULES_SYNC).putExtra(Constants.STATE, Constants.STATE_ERROR))
                e.printStackTrace()
            }

        }, Response.ErrorListener { error ->
            context.sendBroadcast(Intent(Constants.Broadcast_COMPLETE_FLIGHT_SCHEDULES_SYNC).putExtra(Constants.STATE, Constants.STATE_ERROR))
            error.printStackTrace()
        }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Accept"] = "application/json"
                headers["Authorization"] = Constants.AUTHORIZATION
                return headers
            }
        }

        request.retryPolicy = DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        Volley.newRequestQueue(context).add(request)
    }

    fun getFlightSchedules(): MutableList<FlightSchedule>? {

        val flightSchedules: MutableList<FlightSchedule> = ArrayList()

        try {
            val cursor = database!!.open().getSQLiteDatabase().query(Database.TABLE_FLIGHT_SCHEDULES, null, null, null, null, null, null)
            Log.d(TAG, "Flight Schedule Count: ${cursor.count}")

            while (cursor.moveToNext()) {
                val flightSchedule = FlightSchedule(cursor.getInt(cursor.getColumnIndex(Database.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(Database.COLUMN_DURATION)),
                        cursor.getString(cursor.getColumnIndex(Database.COLUMN_AIRPORT_CODE_DEPARTURE)),
                        cursor.getString(cursor.getColumnIndex(Database.COLUMN_LOCAL_TIME_DEPARTURE)),
                        cursor.getString(cursor.getColumnIndex(Database.COLUMN_AIRPORT_CODE_ARRIVAL)),
                        cursor.getString(cursor.getColumnIndex(Database.COLUMN_LOCAL_TIME_ARRIVAL)),
                        cursor.getInt(cursor.getColumnIndex(Database.COLUMN_FLIGHT_NUMBER)))

                flightSchedules.add(flightSchedule)
            }
            Log.d(TAG, "Airports Count: ${flightSchedules.size}")

            cursor.close()
            database!!.close()
            return flightSchedules

        } catch (e: Exception) {
            e.printStackTrace()
            database!!.close()
            return null
        }
    }

    fun getFlightSchedule(id: Int): FlightSchedule? {

        try {
            val where = "${Database.COLUMN_ID} = $id"
            Log.d(TAG, "Get Flight Schedule Where: $where")
            val cursor = database!!.open().getSQLiteDatabase().query(Database.TABLE_FLIGHT_SCHEDULES, null, where, null, null, null, null)

            if (cursor.count > 0) {
                cursor.moveToFirst()
                val flightSchedule = FlightSchedule(cursor.getInt(cursor.getColumnIndex(Database.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(Database.COLUMN_DURATION)),
                        cursor.getString(cursor.getColumnIndex(Database.COLUMN_AIRPORT_CODE_DEPARTURE)),
                        cursor.getString(cursor.getColumnIndex(Database.COLUMN_LOCAL_TIME_DEPARTURE)),
                        cursor.getString(cursor.getColumnIndex(Database.COLUMN_AIRPORT_CODE_ARRIVAL)),
                        cursor.getString(cursor.getColumnIndex(Database.COLUMN_LOCAL_TIME_ARRIVAL)),
                        cursor.getInt(cursor.getColumnIndex(Database.COLUMN_FLIGHT_NUMBER)))

                cursor.close()
                database!!.close()
                return flightSchedule
            } else {
                cursor.close()
                database!!.close()
                return null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            database!!.close()
            return null
        }
    }
}