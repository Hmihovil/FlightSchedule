package io.capsella.flightschedule.activity

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.IdRes
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import io.capsella.flightschedule.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import io.capsella.flightschedule.dao.FlightSceduleDao
import io.capsella.flightschedule.model.FlightSchedule
import io.capsella.flightschedule.util.Constants
import io.capsella.flightschedule.dao.CityDao
import io.capsella.flightschedule.model.City
import io.capsella.flightschedule.util.HelperFunctions


class MapActivity : AppCompatActivity(), View.OnClickListener, OnMapReadyCallback {

    private val TAG = MapActivity::class.java.simpleName

    private lateinit var backBtn: ImageView

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
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        backBtn.setOnClickListener(this)
    }

    private fun setData() {
        flightSchedule = FlightSceduleDao(this).getFlightSchedule(intent.getIntExtra(Constants.ID, 0))
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
