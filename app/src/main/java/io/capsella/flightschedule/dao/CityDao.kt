package io.capsella.flightschedule.dao

import android.content.Context
import android.util.Log
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import io.capsella.flightschedule.storage.Database
import io.capsella.flightschedule.model.City
import io.capsella.flightschedule.util.Constants
import io.capsella.flightschedule.util.HelperFunctions
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class CityDao {

    private val TAG = CityDao::class.java.simpleName
    private var context: Context
    private var database: Database? = null

    constructor(context: Context) {
        this.context = context
        this.database = Database(context)
    }

    fun fetchCities() {

        "https://api.lufthansa.com/v1/references/cities/?limit=50&offset=0"
        val url = Constants.URL_CITIES
        Log.d(TAG, "Url - Get Cities: $url")

        val limit = "100"
        val offset = "0"
        val params: HashMap<String, String> = HashMap()
        params["limit"] = limit
        params["offset"] = offset

        val request = object : StringRequest(Request.Method.GET, HelperFunctions.generateGETUrl(url, params), Response.Listener { response ->

            try {
                val jsonObject = JSONObject(response.toString())
                Log.d(TAG, "Cities Return JSON \n----------$jsonObject")

                database!!.open().clear(Database.TABLE_CITIES)
                database!!.open().saveCities(jsonObject.getJSONObject("CityResource").getJSONObject("Cities").getJSONArray("City"))
                database!!.close()

                AirportDao(context).fetchAirports("")

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

    fun getCities(): MutableList<City>? {

        val cities: MutableList<City> = ArrayList()

        try {
            val cursor = database!!.open().getSQLiteDatabase().query(Database.TABLE_CITIES, null, null, null, null, null, null)
            Log.d(TAG, "Cities Count: ${cursor.count}")

            while (cursor.moveToNext()) {
                val city = City(cursor.getInt(cursor.getColumnIndex(Database.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(Database.COLUMN_CITY_CODE)),
                        cursor.getString(cursor.getColumnIndex(Database.COLUMN_COUNTRY_CODE)),
                        cursor.getString(cursor.getColumnIndex(Database.COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndex(Database.COLUMN_LATITUDE)),
                        cursor.getString(cursor.getColumnIndex(Database.COLUMN_LONGITUDE)))

                cities.add(city)
            }
            Log.d(TAG, "Cities Count: ${cities.size}")

            cursor.close()
            database!!.close()
            return cities

        } catch (e: Exception) {
            e.printStackTrace()
            database!!.close()
            return null
        }
    }

    fun getCity(cityCode: String): City? {

        try {
            val where = "${Database.COLUMN_CITY_CODE} = '$cityCode'"
            Log.d(TAG, "Get City Where: $where")
            val cursor = database!!.open().getSQLiteDatabase().query(Database.TABLE_CITIES, null, where, null, null, null, null)

            if (cursor.count > 0) {
                cursor.moveToFirst()
                val city = City(cursor.getInt(cursor.getColumnIndex(Database.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(Database.COLUMN_CITY_CODE)),
                        cursor.getString(cursor.getColumnIndex(Database.COLUMN_COUNTRY_CODE)),
                        cursor.getString(cursor.getColumnIndex(Database.COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndex(Database.COLUMN_LATITUDE)),
                        cursor.getString(cursor.getColumnIndex(Database.COLUMN_LONGITUDE)))

                cursor.close()
                database!!.close()
                return city
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