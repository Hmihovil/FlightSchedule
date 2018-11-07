package io.capsella.flightschedule.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.capsella.flightschedule.R
import io.capsella.flightschedule.dao.CityDao
import io.capsella.flightschedule.dao.CountryDao
import io.capsella.flightschedule.model.Airport
import io.capsella.flightschedule.util.Constants

class AirportAdapter(var context: Context, var itemArrayList: MutableList<Airport>?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TAG = AirportAdapter::class.java.simpleName

    private val proximaNovaBold = Typeface.createFromAsset(context.assets, "Proxima Nova Bold.ttf")
    private val proximaNovaSemiBold = Typeface.createFromAsset(context.assets, "Proxima Nova SemiBold.ttf")
    private val proximaNovaRegular = Typeface.createFromAsset(context.assets, "Proxima Nova Regular.ttf")

    class ItemViewHolder(row: View) : RecyclerView.ViewHolder(row) {
        var mainLayout: ConstraintLayout = row.findViewById(R.id.main_layout)
        var nameTxt: TextView = row.findViewById(R.id.name)
        var locationTxt: TextView = row.findViewById(R.id.location)
    }

    override fun getItemCount(): Int {
        return itemArrayList!!.size
    }

    private fun getItem(position: Int): Airport {
        return itemArrayList!![position]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.layout_airport_item, parent, false)
        return ItemViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        var airport: Airport = getItem(position)
        var itemViewHolder = holder as ItemViewHolder

        itemViewHolder.nameTxt.text = airport.name
        val city: String? = if (CityDao(context).getCity(airport.cityCode) == null) "n/a" else CityDao(context).getCity(airport.cityCode)!!.name
        val country: String? = if (CountryDao(context).getCountry(airport.countryCode) == null) "n/a" else CountryDao(context).getCountry(airport.countryCode)!!.name
        Log.d(TAG, "Airport City: $city")
        Log.d(TAG, "Airport Country: $country")
        itemViewHolder.locationTxt.text = "$city - $country"

        itemViewHolder.nameTxt.typeface = proximaNovaSemiBold
        itemViewHolder.locationTxt.typeface = proximaNovaRegular

        itemViewHolder.mainLayout.setOnClickListener {
            context.sendBroadcast(Intent(Constants.Broadcast_SET_AIRPORT_VALUES).putExtra(Constants.AIRPORT_CODE, airport.airportCode))
        }
    }
}

