package io.capsella.flightschedule.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Dialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import io.codetail.animation.ViewAnimationUtils
import java.text.SimpleDateFormat
import java.util.*

class HelperFunctions{

    companion object {

        private val TAG = HelperFunctions::class.java.simpleName

        fun hasNetworkConnection(context: Context): Boolean {
            var hasWifiConnection = false
            var hasMobileConnection = false

            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.allNetworkInfo
            for (ni in netInfo) {
                if (ni.typeName.equals("WIFI", ignoreCase = true))
                    if (ni.isConnected)
                        hasWifiConnection = true
                if (ni.typeName.equals("MOBILE", ignoreCase = true))
                    if (ni.isConnected)
                        hasMobileConnection = true
            }
            return hasWifiConnection || hasMobileConnection
        }

        fun getDateTimeInMilliSeconds(date: String): Long {

            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            return try {
                val mDate = formatter.parse(date)
                val timeInMilliseconds = mDate.time
                Log.d(TAG, "Date Time Stamp In Milli Seconds: ${timeInMilliseconds}")
                timeInMilliseconds
            } catch (e: Exception) {
                e.printStackTrace()
                0
            }
        }

        fun getDateFromMilliSeconds(milliSeconds: Long): String {

            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

            val date = Date()
            date.time = milliSeconds
            return formatter.format(date)
        }

        fun getFormattedDateWithDayAndTimeString(date: String?, withDay: Boolean, withTime: Boolean): String {

            Log.d(TAG, "Date provided: $date")

            val calendar = Calendar.getInstance()
            val formatter: SimpleDateFormat

            val parts = date!!.trim().split(" ".toRegex())

            val day = parts[0].split("-".toRegex())[2]
            val mon = parts[0].split("-".toRegex())[1]
            val yr = parts[0].split("-".toRegex())[0]

            calendar.set(Integer.parseInt(yr), Integer.parseInt(mon) - 1, Integer.parseInt(day))

            if (withDay) {
                if (withTime) {
                    formatter = SimpleDateFormat("EE, d MMM yyyy, HH:mm")
                    val hr = parts[1].split(":".toRegex())[0]
                    val min = parts[1].split(":".toRegex())[1]
                    val sec = parts[1].split(":".toRegex())[2]

                    calendar.set(
                            Integer.parseInt(yr),
                            Integer.parseInt(mon) - 1,
                            Integer.parseInt(day),
                            Integer.parseInt(hr),
                            Integer.parseInt(min),
                            Integer.parseInt(sec))
                } else {
                    formatter = SimpleDateFormat("EE, d MMM yyyy")
                }
            } else {
                if (withTime) {
                    formatter = SimpleDateFormat("d MMM yyyy, HH:mm")
                    val hr = parts[1].split(":".toRegex())[0]
                    val min = parts[1].split(":".toRegex())[1]
                    val sec = parts[1].split(":".toRegex())[2]

                    calendar.set(
                            Integer.parseInt(yr),
                            Integer.parseInt(mon) - 1,
                            Integer.parseInt(day),
                            Integer.parseInt(hr),
                            Integer.parseInt(min),
                            Integer.parseInt(sec))
                } else {
                    formatter = SimpleDateFormat("d MMM yyyy")
                }
            }

            return formatter.format(Date(calendar.timeInMillis))
        }

        fun getDisplayMetrics(context: Context): DisplayMetrics {
            val displayMetrics = DisplayMetrics()
            val activity = context as AppCompatActivity
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)

            val width = displayMetrics.widthPixels
            val height = displayMetrics.heightPixels

            return displayMetrics
        }

        fun dpToPx(context: Context, dp: Int): Int {
            val displayMetrics = context.resources.displayMetrics
            return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
        }

        fun circularRevealView(type: Int, duration: Int, view: View) {

            var cx = 0
            var cy = 0
            var finalRadius = 0f

            when (type) {

                1 -> {
                    //CENTER
                    cx = view.width / 2
                    cy = view.height / 2
                    finalRadius = Math.max(view.width, view.height).toFloat()
                }
                2 -> {
                    //TOP_LEFT
                    cx = view.left
                    cy = view.top
                    finalRadius = Math.hypot(view.width.toDouble(), view.height.toDouble()).toInt().toFloat()
                }
                3 -> {
                    //TOP_RIGHT
                    cx = view.right
                    cy = view.top
                    finalRadius = Math.hypot(view.width.toDouble(), view.height.toDouble()).toInt().toFloat()
                }
                4 -> {
                    //BOTTOM_LEFT
                    cx = view.left
                    cy = view.bottom
                    finalRadius = Math.hypot(view.width.toDouble(), view.height.toDouble()).toInt().toFloat()
                }
                5 -> {
                    //BOTTOM_RIGHT
                    cx = view.right
                    cy = view.bottom
                    finalRadius = Math.hypot(view.width.toDouble(), view.height.toDouble()).toInt().toFloat()
                }
            }

            val animator = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0f, finalRadius)
            animator.interpolator = AccelerateDecelerateInterpolator()
            animator.duration = duration.toLong()
            view.visibility = View.VISIBLE

            animator.addListener(object : AnimatorListenerAdapter() {
            })
            animator.start()
        }

        fun circularConcealView(type: Int, duration: Int, view: View, appCompatActivity: AppCompatActivity?, dialog: Dialog?) {

            var cx = 0
            var cy = 0
            var initialRadius = 0f

            when (type) {

                1 -> {
                    //CENTER
                    cx = view.width / 2
                    cy = view.height / 2
                    initialRadius = Math.max(view.width, view.height).toFloat()
                }
                2 -> {
                    //TOP_LEFT
                    cx = view.left
                    cy = view.top
                    initialRadius = Math.hypot(view.width.toDouble(), view.height.toDouble()).toInt().toFloat()
                }
                3 -> {
                    //TOP_RIGHT
                    cx = view.right
                    cy = view.top
                    initialRadius = Math.hypot(view.width.toDouble(), view.height.toDouble()).toInt().toFloat()
                }
                4 -> {
                    //BOTTOM_LEFT
                    cx = view.left
                    cy = view.bottom
                    initialRadius = Math.hypot(view.width.toDouble(), view.height.toDouble()).toInt().toFloat()
                }
                5 -> {
                    //BOTTOM_RIGHT
                    cx = view.right
                    cy = view.bottom
                    initialRadius = Math.hypot(view.width.toDouble(), view.height.toDouble()).toInt().toFloat()
                }
            }

            val animator = ViewAnimationUtils.createCircularReveal(view, cx, cy, initialRadius, 0f)
            animator.interpolator = AccelerateDecelerateInterpolator()
            animator.duration = duration.toLong()

            animator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)

                    if (appCompatActivity != null) {
                        dialog!!.dismiss()
                        appCompatActivity.finish()
                        view.visibility = View.GONE
                    } else if (dialog != null) {
                        dialog.dismiss()
                        view.visibility = View.GONE
                    } else {
                        view.visibility = View.INVISIBLE
                    }
                }
            })

            animator.start()
        }
    }
}