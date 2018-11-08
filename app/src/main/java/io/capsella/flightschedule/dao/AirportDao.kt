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
import io.capsella.flightschedule.model.Airport
import io.capsella.flightschedule.util.Constants
import io.capsella.flightschedule.util.HelperFunctions
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class AirportDao {

    private val TAG = AirportDao::class.java.simpleName
    private var context: Context
    private var database: Database? = null

    constructor(context: Context) {
        this.context = context
        this.database = Database(context)
    }

    fun fetchAirports(airportCode: String) {

        "https://api.lufthansa.com/v1/references/airports/?limit=50&offset=0&LHoperated=false"
        val url = "${Constants.URL_AIRPORTS}$airportCode"
        Log.d(TAG, "Url - Get Airports: $url")

        val limit = "100"
        val offset = "0"
        val lhOperated = "0"
        val params: HashMap<String, String> = HashMap()
        params["limit"] = limit
        params["offset"] = offset
        params["LHoperated"] = lhOperated

        val request = object : StringRequest(Request.Method.GET, HelperFunctions.generateGETUrl(url, params), Response.Listener { response ->

            try {
                val jsonObject = JSONObject(response.toString())
                Log.d(TAG, "Airports Return JSON \n----------$jsonObject")

                database!!.open().clear(Database.TABLE_AIRPORTS)
                if(airportCode.isEmpty()){
                    database!!.open().saveAirports(jsonObject.getJSONObject("AirportResource").getJSONObject("Airports").getJSONArray("Airport"))
                }else{
                    database!!.open().insertAirport(jsonObject.getJSONObject("AirportResource").getJSONObject("Airports").getJSONObject("Airport"))
                }
                database!!.close()

                context.sendBroadcast(Intent(Constants.Broadcast_COMPLETE_AIRPORTS_SYNC))

            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }

        }, Response.ErrorListener { error ->
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

        Volley.newRequestQueue(context).add(request)
    }

    fun getAirports(airportCode: String?): MutableList<Airport>? {

        val airports: MutableList<Airport> = ArrayList()

        try {
//            val cursor = ourDatabase.query(REFERENCE_SURVEYS_RESPONSES_TABLE, null, SURVEY_ID + " = " + surveyId + " AND " + REFERENCE_RESPONSE_VALUE_TITLE + " LIKE '%" + filterText + "%'" + " AND " + COMPLETED_REFERENCE + " = 0", null, null, null, RESPONDENT_ID_LABEL + " ASC")
            val where = "${Database.COLUMN_AIRPORT_CODE} LIKE '$airportCode%'"
            Log.d(TAG, "Get Airports Where: $where")
            val cursor = database!!.open().getSQLiteDatabase().query(Database.TABLE_AIRPORTS, null, where, null, null, null, null)
            Log.d(TAG, "Airports Count: ${cursor.count}")

            while (cursor.moveToNext()) {
                val airport = Airport(cursor.getInt(cursor.getColumnIndex(Database.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(Database.COLUMN_AIRPORT_CODE)),
                        cursor.getString(cursor.getColumnIndex(Database.COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndex(Database.COLUMN_LATITUDE)),
                        cursor.getString(cursor.getColumnIndex(Database.COLUMN_LONGITUDE)),
                        cursor.getString(cursor.getColumnIndex(Database.COLUMN_CITY_CODE)),
                        cursor.getString(cursor.getColumnIndex(Database.COLUMN_COUNTRY_CODE)))

                airports.add(airport)
            }
            Log.d(TAG, "Airports Count: ${airports.size}")

            cursor.close()
            database!!.close()
            return airports

        } catch (e: Exception) {
            e.printStackTrace()
            database!!.close()
            return null
        }
    }

    fun getAirport(airportCode: String): Airport? {

        try {
            val where = "${Database.COLUMN_AIRPORT_CODE} = '$airportCode'"
            Log.d(TAG, "Get Airport Where: $where")
            val cursor = database!!.open().getSQLiteDatabase().query(Database.TABLE_AIRPORTS, null, where, null, null, null, null)

            if (cursor.count > 0) {
                cursor.moveToFirst()
                val airport = Airport(cursor.getInt(cursor.getColumnIndex(Database.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(Database.COLUMN_AIRPORT_CODE)),
                        cursor.getString(cursor.getColumnIndex(Database.COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndex(Database.COLUMN_LATITUDE)),
                        cursor.getString(cursor.getColumnIndex(Database.COLUMN_LONGITUDE)),
                        cursor.getString(cursor.getColumnIndex(Database.COLUMN_CITY_CODE)),
                        cursor.getString(cursor.getColumnIndex(Database.COLUMN_COUNTRY_CODE)))

                cursor.close()
                database!!.close()
                return airport
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