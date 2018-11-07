package io.capsella.flightschedule.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.capsella.flightschedule.R
import io.capsella.flightschedule.activity.AirportSelectionActivity
import io.capsella.flightschedule.activity.MapActivity
import io.capsella.flightschedule.dao.AirportDao
import io.capsella.flightschedule.dao.CityDao
import io.capsella.flightschedule.dao.CountryDao
import io.capsella.flightschedule.model.Airport
import io.capsella.flightschedule.model.FlightSchedule
import io.capsella.flightschedule.util.Constants

class FlightScheduleAdapter(var context: Context, var itemArrayList: MutableList<FlightSchedule>?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TAG = FlightScheduleAdapter::class.java.simpleName

    private val proximaNovaBold = Typeface.createFromAsset(context.assets, "Proxima Nova Bold.ttf")
    private val proximaNovaSemiBold = Typeface.createFromAsset(context.assets, "Proxima Nova SemiBold.ttf")
    private val proximaNovaRegular = Typeface.createFromAsset(context.assets, "Proxima Nova Regular.ttf")

    class ItemViewHolder(row: View) : RecyclerView.ViewHolder(row) {
        var mainLayout: ConstraintLayout = row.findViewById(R.id.main_layout)
        var originTxt: TextView = row.findViewById(R.id.origin)
        var destinationTxt: TextView = row.findViewById(R.id.destination)
    }

    override fun getItemCount(): Int {
        return itemArrayList!!.size
    }

    private fun getItem(position: Int): FlightSchedule {
        return itemArrayList!![position]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.layout_flight_schedule, parent, false)
        return ItemViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        var flightSchedule: FlightSchedule = getItem(position)
        var itemViewHolder = holder as ItemViewHolder

        itemViewHolder.originTxt.text = if(AirportDao(context).getAirport(flightSchedule.airportCodeDeparture) == null) flightSchedule.airportCodeDeparture else AirportDao(context).getAirport(flightSchedule.airportCodeDeparture)!!.name
        itemViewHolder.destinationTxt.text = if(AirportDao(context).getAirport(flightSchedule.airportCodeArrival) == null) flightSchedule.airportCodeArrival else AirportDao(context).getAirport(flightSchedule.airportCodeArrival)!!.name

        itemViewHolder.originTxt.typeface = proximaNovaSemiBold
        itemViewHolder.destinationTxt.typeface = proximaNovaRegular

        itemViewHolder.mainLayout.setOnClickListener {
            val activity = context as AppCompatActivity
            activity.startActivity(Intent(context, MapActivity::class.java)
                    .putExtra(Constants.ORIGIN, flightSchedule.airportCodeDeparture)
                    .putExtra(Constants.DESTINATION, flightSchedule.airportCodeArrival))
            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }
}

