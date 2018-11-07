package io.capsella.flightschedule.activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import io.capsella.flightschedule.R
import io.capsella.flightschedule.adapter.AirportAdapter
import io.capsella.flightschedule.dao.AirportDao
import io.capsella.flightschedule.dao.FlightSceduleDao
import io.capsella.flightschedule.model.Airport
import io.capsella.flightschedule.util.Constants

class AirportSelectionActivity : AppCompatActivity(), View.OnClickListener {

    private val TAG = AirportSelectionActivity::class.java.simpleName

    private lateinit var backBtn: ImageView
    private lateinit var titleTxt: TextView
    private lateinit var originTxt: EditText
    private lateinit var destTxt: EditText
    private lateinit var continueTxt: TextView
    private lateinit var noResultsImg: ImageView
    private lateinit var noResultsTxt: TextView
    private lateinit var recyclerView: RecyclerView

    private lateinit var proximaNovaBold: Typeface
    private lateinit var proximaNovaSemiBold: Typeface
    private lateinit var proximaNovaRegular: Typeface

    private lateinit var setAirportValuesBroadcastReceiver: SetAirportValuesBroadcastReceiver
    private lateinit var completeFlightSchedulesSyncBroadcastReceiver: CompleteFlightSchedulesSyncBroadcastReceiver
    private lateinit var inputMethodManager: InputMethodManager
    private lateinit var progressDialog: ProgressDialog
    private var airports: MutableList<Airport>? = null
    private var origin = ""
    private var destination = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_airport_selection)

        proximaNovaBold = Typeface.createFromAsset(assets, "Proxima Nova Bold.ttf")
        proximaNovaSemiBold = Typeface.createFromAsset(assets, "Proxima Nova SemiBold.ttf")
        proximaNovaRegular = Typeface.createFromAsset(assets, "Proxima Nova Regular.ttf")

        setAirportValuesBroadcastReceiver = SetAirportValuesBroadcastReceiver()
        completeFlightSchedulesSyncBroadcastReceiver = CompleteFlightSchedulesSyncBroadcastReceiver()
        inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        progressDialog = ProgressDialog(this)
        airports = AirportDao(this@AirportSelectionActivity).getAirports("")

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
            R.id.continue_ -> {
                if (origin.isEmpty()) {
                    Toast.makeText(this, resources.getString(R.string.select_start_location), Toast.LENGTH_SHORT).show()
                } else if (destination.isEmpty()) {
                    Toast.makeText(this, resources.getString(R.string.select_end_location), Toast.LENGTH_SHORT).show()
                } else {
                    progressDialog.setMessage(resources.getString(R.string.searching_for_available_flights))
                    progressDialog.setCancelable(false)
                    progressDialog.show()

                    FlightSceduleDao(this).fetchFlightSchedules(origin, destination)
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(setAirportValuesBroadcastReceiver, IntentFilter(Constants.Broadcast_SET_AIRPORT_VALUES))
        registerReceiver(completeFlightSchedulesSyncBroadcastReceiver, IntentFilter(Constants.Broadcast_COMPLETE_FLIGHT_SCHEDULES_SYNC))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(setAirportValuesBroadcastReceiver)
        unregisterReceiver(completeFlightSchedulesSyncBroadcastReceiver)
    }

    private fun initViews() {

        backBtn = bind(R.id.back)
        titleTxt = bind(R.id.title)
        originTxt = bind(R.id.origin)
        destTxt = bind(R.id.dest)
        continueTxt = bind(R.id.continue_)
        noResultsImg = bind(R.id.no_results_img)
        noResultsTxt = bind(R.id.no_results_txt)
        recyclerView = bind(R.id.recycler_view)

        titleTxt.typeface = proximaNovaBold
        originTxt.typeface = proximaNovaRegular
        destTxt.typeface = proximaNovaRegular
        continueTxt.typeface = proximaNovaSemiBold
        noResultsTxt.typeface = proximaNovaSemiBold

        backBtn.setOnClickListener(this)
        titleTxt.setOnClickListener(this)
        continueTxt.setOnClickListener(this)

        originTxt.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                airports = AirportDao(this@AirportSelectionActivity).getAirports(s.toString())
                setData()
            }
        })

        destTxt.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                airports = AirportDao(this@AirportSelectionActivity).getAirports(s.toString())
                setData()
            }
        })
    }

    private fun setData() {

        if (airports != null && airports!!.isNotEmpty()) {
            recyclerView.visibility = View.VISIBLE
            noResultsImg.visibility = View.GONE
            noResultsTxt.visibility = View.GONE

            var adapter = AirportAdapter(this, airports)
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = adapter
        } else {
            recyclerView.visibility = View.GONE
            noResultsImg.visibility = View.VISIBLE
            noResultsTxt.visibility = View.VISIBLE
        }
    }

    inner class SetAirportValuesBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {

            val airportCode = intent.getStringExtra(Constants.AIRPORT_CODE)
            if (originTxt.isFocused) {
                originTxt.setText(AirportDao(this@AirportSelectionActivity).getAirport(airportCode)!!.name)
                origin = airportCode
                Log.d(TAG, "Airport Code Origin: $origin")

                destTxt.requestFocus()
                if (originTxt.windowToken != null)
                    inputMethodManager.hideSoftInputFromWindow(originTxt.windowToken, 0)
            } else {
                destTxt.setText(AirportDao(this@AirportSelectionActivity).getAirport(airportCode)!!.name)
                destination = airportCode
                Log.d(TAG, "Airport Code Destination: $origin")

                if (destTxt.windowToken != null)
                    inputMethodManager.hideSoftInputFromWindow(destTxt.windowToken, 0)
            }

            airports = AirportDao(this@AirportSelectionActivity).getAirports("")
            setData()
        }
    }

    inner class CompleteFlightSchedulesSyncBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {

            if (progressDialog.isShowing) progressDialog.dismiss()

            val returnIntent = Intent()
            returnIntent.putExtra(Constants.ORIGIN, origin)
            returnIntent.putExtra(Constants.DESTINATION, destination)
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        }
    }
}
