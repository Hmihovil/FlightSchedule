package io.capsella.flightschedule.activity

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.annotation.NonNull
import android.support.constraint.ConstraintLayout
import android.support.design.widget.BottomSheetBehavior
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import io.capsella.flightschedule.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import io.capsella.flightschedule.dao.AirportDao
import io.capsella.flightschedule.dao.FlightSceduleDao
import io.capsella.flightschedule.model.FlightSchedule
import io.capsella.flightschedule.util.Constants
import io.capsella.flightschedule.dao.CityDao
import io.capsella.flightschedule.model.City
import io.capsella.flightschedule.util.HelperFunctions


class MapActivity : AppCompatActivity(), View.OnClickListener, OnMapReadyCallback {

    private val TAG = MapActivity::class.java.simpleName

    private lateinit var backBtn: ImageView
    private lateinit var chevron: ImageView
    private lateinit var peekTitle: TextView
    private lateinit var departureTitle: TextView
    private lateinit var departureFlightNo: TextView
    private lateinit var departureAirport: TextView
    private lateinit var departureTime: TextView
    private lateinit var arrivalTitle: TextView
    private lateinit var arrivalFlightNo: TextView
    private lateinit var arrivalAirport: TextView
    private lateinit var arrivalTime: TextView
    private lateinit var bottomSheetLayout: ConstraintLayout
    private lateinit var sheetBehavior: BottomSheetBehavior<*>

    private lateinit var proximaNovaBold: Typeface
    private lateinit var proximaNovaSemiBold: Typeface
    private lateinit var proximaNovaRegular: Typeface

    private var googleMap: GoogleMap? = null
    private var latLngBounds: LatLngBounds? = null
    private var flightSchedule: FlightSchedule? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        proximaNovaBold = Typeface.createFromAsset(assets, "Proxima Nova Bold.ttf")
        proximaNovaSemiBold = Typeface.createFromAsset(assets, "Proxima Nova SemiBold.ttf")
        proximaNovaRegular = Typeface.createFromAsset(assets, "Proxima Nova Regular.ttf")

        initViews()
        setData()
    }

    private fun <T : View> Activity.bind(@IdRes res: Int): T {
        @Suppress("UNCHECKED_CAST")
        return findViewById<T>(res)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.back -> {
                finish()
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            }
            R.id.peek_title -> {
                if (sheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                    sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    chevron.setImageDrawable(resources.getDrawable(R.drawable.chevron_down))
                } else {
                    sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    chevron.setImageDrawable(resources.getDrawable(R.drawable.chevron_up))
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap

//        // Add a marker in Sydney, Australia, and move the camera.
//        val sydney = LatLng(-34.0, 151.0)
//        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        addTravelRoutePolyline()
    }

    private fun initViews() {

        backBtn = bind(R.id.back)
        chevron = bind(R.id.chevron)
        peekTitle = bind(R.id.peek_title)
        departureTitle = bind(R.id.departure_title)
        departureFlightNo = bind(R.id.departure_flight_no)
        departureAirport = bind(R.id.departure_airport)
        departureTime = bind(R.id.departure_time)
        arrivalTitle = bind(R.id.arrival_title)
        arrivalFlightNo = bind(R.id.arrival_flight_no)
        arrivalAirport = bind(R.id.arrival_airport)
        arrivalTime = bind(R.id.arrival_time)
        bottomSheetLayout = bind(R.id.bottom_sheet_layout)
        sheetBehavior = BottomSheetBehavior.from(bottomSheetLayout)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        peekTitle.typeface = proximaNovaSemiBold
        departureTitle.typeface = proximaNovaRegular
        departureFlightNo.typeface = proximaNovaRegular
        departureAirport.typeface = proximaNovaRegular
        departureTime.typeface = proximaNovaRegular
        arrivalTitle.typeface = proximaNovaRegular
        arrivalFlightNo.typeface = proximaNovaRegular
        arrivalAirport.typeface = proximaNovaRegular
        arrivalTime.typeface = proximaNovaRegular

        backBtn.setOnClickListener(this)
        peekTitle.setOnClickListener(this)

        sheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(@NonNull bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        chevron.setImageDrawable(resources.getDrawable(R.drawable.chevron_down))
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        chevron.setImageDrawable(resources.getDrawable(R.drawable.chevron_up))
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {

                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                    }
                }
            }

            override fun onSlide(@NonNull bottomSheet: View, slideOffset: Float) {

            }
        })
    }

    private fun setData() {
        flightSchedule = FlightSceduleDao(this).getFlightSchedule(intent.getIntExtra(Constants.ID, 0))

        departureFlightNo.text = resources.getString(R.string.flight_no, flightSchedule!!.flightNumber)
        departureAirport.text = AirportDao(this).getAirport(flightSchedule!!.airportCodeDeparture)!!.name
        departureTime.text = flightSchedule!!.localTimeDeparture
        arrivalFlightNo.text = resources.getString(R.string.flight_no, flightSchedule!!.flightNumber)
        arrivalAirport.text = AirportDao(this).getAirport(flightSchedule!!.airportCodeArrival)!!.name
        arrivalTime.text = flightSchedule!!.localTimeArrival
    }

    private fun addTravelRoutePolyline() {

        val origin: City? = CityDao(this).getCity(flightSchedule!!.airportCodeDeparture)
        val destination: City? = CityDao(this).getCity(flightSchedule!!.airportCodeArrival)

        if (origin == null) {
            Toast.makeText(this, resources.getString(R.string.origin_location_unavailable), Toast.LENGTH_SHORT).show()
            return
        }

        if (destination == null) {
            Toast.makeText(this, resources.getString(R.string.dest_location_unavailable), Toast.LENGTH_SHORT).show()
            return
        }

        val originLatLng = LatLng(origin.latitude.toDouble(), CityDao(this).getCity(flightSchedule!!.airportCodeDeparture)!!.longitude.toDouble())
        val destLatLng = LatLng(destination.latitude.toDouble(), CityDao(this).getCity(flightSchedule!!.airportCodeArrival)!!.longitude.toDouble())

        // add origin marker
        googleMap!!.addMarker(MarkerOptions()
                .position(originLatLng)
                .title(origin.name)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_accent)))

        // add destination marker
        googleMap!!.addMarker(MarkerOptions()
                .position(destLatLng)
                .title(destination.name)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_accent)))

        // connect origin and destination markers with a polyline
        val polyline = googleMap!!.addPolyline(PolylineOptions()
                .clickable(true)
                .width(5f)
                .color(Color.RED)
                .geodesic(true)
                .add(originLatLng, destLatLng))

        // zoom to bouds
        val builder = LatLngBounds.Builder()
        builder.include(originLatLng)
        builder.include(destLatLng)
        latLngBounds = builder.build()
        zoomToBounds(latLngBounds)
    }

    private fun zoomToBounds(latLngBounds: LatLngBounds?) {

        if (latLngBounds != null) {
            val width = HelperFunctions.getDisplayMetrics(this).widthPixels
            val height = HelperFunctions.getDisplayMetrics(this).heightPixels - HelperFunctions.dpToPx(this, 56)
            val padding = HelperFunctions.dpToPx(this, 30)
            val cameraUpdate = CameraUpdateFactory.newLatLngBounds(latLngBounds, width, height, padding)
            googleMap!!.animateCamera(cameraUpdate)
        }
    }
}
