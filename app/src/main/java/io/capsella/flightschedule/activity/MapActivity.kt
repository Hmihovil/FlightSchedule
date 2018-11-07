package io.capsella.flightschedule.activity

import android.app.Activity
import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.IdRes
import android.view.View
import android.widget.ImageView
import io.capsella.flightschedule.R

class MapActivity : AppCompatActivity(), View.OnClickListener {

    private val TAG = MapActivity::class.java.simpleName

    private lateinit var backBtn: ImageView

    private lateinit var proximaNovaBold: Typeface
    private lateinit var proximaNovaSemiBold: Typeface
    private lateinit var proximaNovaRegular: Typeface

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

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    private fun initViews() {

        backBtn = bind(R.id.back)

        backBtn.setOnClickListener(this)
    }

    private fun setData() {

    }
}
