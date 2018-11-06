package io.capsella.flightschedule.activity

import android.Manifest
import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.Settings
import android.support.annotation.IdRes
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import io.capsella.flightschedule.R
import io.capsella.flightschedule.dao.AirportDao
import io.capsella.flightschedule.util.Constants
import io.capsella.flightschedule.util.HelperFunctions


class SplashActivity : AppCompatActivity() {

    private val TAG = SplashActivity::class.java.simpleName

    private lateinit var appNameTxt: TextView
    private lateinit var loadingTxt: TextView

    private lateinit var proximaNovaBold: Typeface
    private lateinit var proximaNovaSemiBold: Typeface
    private lateinit var proximaNovaRegular: Typeface

    private var permissionsRequired = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION)

    private var sentToSettings = false
    private lateinit var completeAppDataSyncBroadcastReceiver: CompleteAppDataSyncBroadcastReceiver
    private lateinit var prefs: SharedPreferences
    private lateinit var edit: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        edit = prefs.edit()
        completeAppDataSyncBroadcastReceiver = CompleteAppDataSyncBroadcastReceiver()

        proximaNovaBold = Typeface.createFromAsset(assets, "Proxima Nova Bold.ttf")
        proximaNovaSemiBold = Typeface.createFromAsset(assets, "Proxima Nova SemiBold.ttf")
        proximaNovaRegular = Typeface.createFromAsset(assets, "Proxima Nova Regular.ttf")

        initViews()
    }

    private fun <T : View> Activity.bind(@IdRes res: Int): T {
        @Suppress("UNCHECKED_CAST")
        return findViewById<T>(res)
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(completeAppDataSyncBroadcastReceiver, IntentFilter(Constants.Broadcast_COMPLETE_APP_DATA_SYNC))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(completeAppDataSyncBroadcastReceiver)
    }

    override fun onPostResume() {
        super.onPostResume()

        if (sentToSettings) {
            if (ActivityCompat.checkSelfPermission(this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                proceed()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.PERMISSION_CALLBACK_CONSTANT) {
            //check if all permissions are granted
            var allGranted = false
            for (i in grantResults.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    allGranted = true
                } else {
                    allGranted = false
                    break
                }
            }

            if (allGranted) {
                proceed()
                Log.d("SARIO", "Runtime Permissions: onRequestPermissionsResult All Granted- Permissions granted and GPS enabled")
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[1])
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[2])
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[3])
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[4])) {

                val builder = AlertDialog.Builder(this)
                builder.setCancelable(false)
                builder.setTitle(resources.getString(R.string.multiple_permissions_needed))
                builder.setMessage(resources.getString(R.string.permissions_request_message))
                builder.setPositiveButton(resources.getString(R.string.grant)) { dialog, which ->
                    dialog.cancel()
                    ActivityCompat.requestPermissions(this, permissionsRequired, Constants.PERMISSION_CALLBACK_CONSTANT)
                }
                builder.setNegativeButton(resources.getString(R.string.exit)) { dialog, which ->
                    dialog.cancel()
                    finish()
                }
                builder.show()
            } else {
                requestPermissions()
                Toast.makeText(baseContext, resources.getString(R.string.unable_to_get_permissions), Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            Constants.REQUEST_CHECK_SETTINGS -> {
                if (resultCode == AppCompatActivity.RESULT_OK) proceed()
            }
            Constants.REQUEST_PERMISSION_SETTING -> {
                if (ActivityCompat.checkSelfPermission(this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                    //Got Permission
                    proceed()
                }
            }
        }
    }

    private fun initViews() {

        appNameTxt = bind(R.id.app_name)
        loadingTxt = bind(R.id.loading)

        appNameTxt.typeface = proximaNovaBold
        loadingTxt.typeface = proximaNovaSemiBold

        requestPermissions()
    }

    private fun requestPermissions() {

        if (ActivityCompat.checkSelfPermission(this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, permissionsRequired[2]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, permissionsRequired[3]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, permissionsRequired[4]) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[1])
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[2])
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[3])
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[4])) {
                //Show Information about why you need the permission
                val builder = AlertDialog.Builder(this)
                builder.setCancelable(false)
                builder.setTitle(resources.getString(R.string.multiple_permissions_needed))
                builder.setMessage(resources.getString(R.string.permissions_request_message))
                builder.setPositiveButton(resources.getString(R.string.grant)) { dialog, which ->
                    dialog.cancel()
                    ActivityCompat.requestPermissions(this, permissionsRequired, Constants.PERMISSION_CALLBACK_CONSTANT)
                }
                builder.setNegativeButton(resources.getString(R.string.exit)) { dialog, which ->
                    dialog.cancel()
                    finish()
                }
                builder.show()
            } else if (prefs.getBoolean(permissionsRequired[0], false)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                val builder = AlertDialog.Builder(this)
                builder.setCancelable(false)
                builder.setTitle(resources.getString(R.string.multiple_permissions_needed))
                builder.setMessage(resources.getString(R.string.permissions_request_message))
                builder.setPositiveButton(resources.getString(R.string.grant)) { dialog, which ->
                    dialog.cancel()
                    sentToSettings = true
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivityForResult(intent, Constants.REQUEST_PERMISSION_SETTING)
                    Toast.makeText(baseContext, resources.getString(R.string.go_to_permissions_to_grant_them), Toast.LENGTH_LONG).show()
                }
                builder.setNegativeButton(resources.getString(R.string.exit)) { dialog, which ->
                    dialog.cancel()
                    finish()
                }
                builder.show()
            } else {
                //just request the permission
                ActivityCompat.requestPermissions(this, permissionsRequired, Constants.PERMISSION_CALLBACK_CONSTANT)
            }

            edit.putBoolean(permissionsRequired[0], true)
            edit.commit()
        } else {
            //You already have the permission, just go ahead.
            proceed()
        }
    }

    private fun proceed() {
        if (HelperFunctions.hasNetworkConnection(this)) {
            AirportDao(this).fetchAirports("")
        } else {
            Toast.makeText(this, resources.getText(R.string.no_internet_msg), Toast.LENGTH_SHORT).show()
        }
    }

    inner class CompleteAppDataSyncBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {

            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            finish()
        }
    }
}
