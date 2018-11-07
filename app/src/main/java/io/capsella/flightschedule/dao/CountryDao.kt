package io.capsella.flightschedule.dao

import android.content.Context
import android.util.Log
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import io.capsella.flightschedule.storage.Database
import io.capsella.flightschedule.model.Country
import io.capsella.flightschedule.util.Constants
import io.capsella.flightschedule.util.HelperFunctions
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class CountryDao {

    private val TAG = CountryDao::class.java.simpleName
    private var context: Context
    private var database: Database? = null

    constructor(context: Context) {
        this.context = context
        this.database = Database(context)
    }

    fun fetchCountries() {

        "https://api.lufthansa.com/v1/references/countries/?limit=50&offset=0"
        val url = Constants.URL_COUNTRIES
        Log.d(TAG, "Url - Get Countries: $url")

        val limit = "100"
        val offset = "0"
        val params: HashMap<String, String> = HashMap()
        params["limit"] = limit
        params["offset"] = offset

        val request = object : StringRequest(Request.Method.GET, HelperFunctions.generateGETUrl(url, params), Response.Listener { response ->

            try {
                val jsonObject = JSONObject(response.toString())
                Log.d(TAG, "Countries Return JSON \n----------$jsonObject")

                database!!.open().clear(Database.TABLE_COUNTRIES)
                database!!.open().saveCountries(jsonObject.getJSONObject("CountryResource").getJSONObject("Countries").getJSONArray("Country"))
                database!!.close()

                CityDao(context).fetchCities()

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

    fun getCountries(): MutableList<Country>? {

        val countries: MutableList<Country> = ArrayList()

        try {
            val cursor = database!!.open().getSQLiteDatabase().query(Database.TABLE_COUNTRIES, null, null, null, null, null, null)
            Log.d(TAG, "Airports Count: ${cursor.count}")

            while (cursor.moveToNext()) {
                val country = Country(cursor.getInt(cursor.getColumnIndex(Database.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(Database.COLUMN_COUNTRY_CODE)),
                        cursor.getString(cursor.getColumnIndex(Database.COLUMN_NAME)))

                countries.add(country)
            }
            Log.d(TAG, "Countries Count: ${countries.size}")

            cursor.close()
            database!!.close()
            return countries

        } catch (e: Exception) {
            e.printStackTrace()
            database!!.close()
            return null
        }
    }

    fun getCountry(countryCode: String): Country? {

        try {
            val where = "${Database.COLUMN_COUNTRY_CODE} = '$countryCode'"
            Log.d(TAG, "Get Country Where: $where")
            val cursor = database!!.open().getSQLiteDatabase().query(Database.TABLE_COUNTRIES, null, where, null, null, null, null)

            if (cursor.count > 0) {
                cursor.moveToFirst()
                val country = Country(cursor.getInt(cursor.getColumnIndex(Database.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(Database.COLUMN_COUNTRY_CODE)),
                        cursor.getString(cursor.getColumnIndex(Database.COLUMN_NAME)))

                cursor.close()
                database!!.close()
                return country
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