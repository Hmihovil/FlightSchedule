package io.capsella.flightschedule.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import io.capsella.flightschedule.R
import io.capsella.flightschedule.adapter.FlightScheduleAdapter
import io.capsella.flightschedule.dao.AirportDao
import io.capsella.flightschedule.dao.FlightSceduleDao
import io.capsella.flightschedule.model.FlightSchedule
import io.capsella.flightschedule.storage.PrefsManager
import io.capsella.flightschedule.util.Constants


class MainActivity : AppCompatActivity(), View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private lateinit var titleTxt: TextView
    private lateinit var originTxt: TextView
    private lateinit var destTxt: TextView
    private lateinit var noFlightsAvailableTxt: TextView
    private lateinit var noFlightsAvailableImg: ImageView
    private lateinit var originCardView: CardView
    private lateinit var destCardView: CardView
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private lateinit var proximaNovaBold: Typeface
    private lateinit var proximaNovaSemiBold: Typeface
    private lateinit var proximaNovaRegular: Typeface

    private var originAirportCode = ""
    private var destinationAirportCode = ""
    private var flightSchedules: MutableList<FlightSchedule>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        proximaNovaBold = Typeface.createFromAsset(assets, "Proxima Nova Bold.ttf")
        proximaNovaSemiBold = Typeface.createFromAsset(assets, "Proxima Nova SemiBold.ttf")
        proximaNovaRegular = Typeface.createFromAsset(assets, "Proxima Nova Regular.ttf")

        flightSchedules = ArrayList()

        initViews()
        setData()
    }

    private fun <T : View> Activity.bind(@IdRes res: Int): T {
        @Suppress("UNCHECKED_CAST")
        return findViewById<T>(res)
    }


    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onRefresh() {
        if (originAirportCode.isNotEmpty() && destinationAirportCode.isNotEmpty())
            FlightSceduleDao(this).fetchFlightSchedules(originAirportCode, destinationAirportCode)
        else
            swipeRefreshLayout.isRefreshing = false
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.origin_card_view, R.id.dest_card_view -> {
                startActivityForResult(Intent(this, AirportSelectionActivity::class.java), 1000)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            1000 -> {
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    originAirportCode = data!!.getStringExtra(Constants.ORIGIN)
                    destinationAirportCode = data.getStringExtra(Constants.DESTINATION)

                    PrefsManager(this).origin = data.getStringExtra(Constants.ORIGIN)
                    PrefsManager(this).destination = data.getStringExtra(Constants.DESTINATION)

                    setData()
                } else {

                }
            }
        }
    }

    private fun initViews() {

        titleTxt = bind(R.id.title)
        originTxt = bind(R.id.origin)
        destTxt = bind(R.id.dest)
        noFlightsAvailableTxt = bind(R.id.no_flights_available_txt)
        noFlightsAvailableImg = bind(R.id.no_flights_available_img)
        originCardView = bind(R.id.origin_card_view)
        destCardView = bind(R.id.dest_card_view)
        recyclerView = bind(R.id.recycler_view)
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout)
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.isEnabled = false

        titleTxt.typeface = proximaNovaBold
        originTxt.typeface = proximaNovaRegular
        destTxt.typeface = proximaNovaRegular
        noFlightsAvailableTxt.typeface = proximaNovaSemiBold

        titleTxt.setOnClickListener(this)
        originCardView.setOnClickListener(this)
        destCardView.setOnClickListener(this)
    }

    private fun setData() {

        originAirportCode = PrefsManager(this).origin
        destinationAirportCode = PrefsManager(this).destination

        if (originAirportCode.isNotEmpty() && destinationAirportCode.isNotEmpty()) {
            originTxt.text = if (AirportDao(this).getAirport(originAirportCode) == null) originAirportCode else AirportDao(this).getAirport(originAirportCode)!!.name
            destTxt.text = if (AirportDao(this).getAirport(destinationAirportCode) == null) destinationAirportCode else AirportDao(this).getAirport(destinationAirportCode)!!.name
        }

        flightSchedules = FlightSceduleDao(this).getFlightSchedules()

        if (flightSchedules != null && flightSchedules!!.isNotEmpty()) {
            recyclerView.visibility = View.VISIBLE
            noFlightsAvailableImg.visibility = View.GONE
            noFlightsAvailableTxt.visibility = View.GONE

            var adapter = FlightScheduleAdapter(this, flightSchedules)
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = adapter
        } else {
            recyclerView.visibility = View.GONE
            noFlightsAvailableImg.visibility = View.VISIBLE
            noFlightsAvailableTxt.visibility = View.VISIBLE
        }
    }
}
