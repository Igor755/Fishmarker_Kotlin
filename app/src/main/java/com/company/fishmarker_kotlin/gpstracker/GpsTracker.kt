package com.company.fishmarker_kotlin.gpstracker

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.app.Service
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.company.fishmarker_kotlin.R


class GpsTracker(context: Context) : Service(), LocationListener {

    private var mContext: Context? = context

    private var alertDialog: AlertDialog? = null


    // flag for GPS status
    var isGPSEnabled = false

    // flag for network status
    var isNetworkEnabled = false

    // flag for GPS status
    var canGetLocation = false

    //flag for WIFi
    var isWifiEnabled = false

    private var location : Location? = null

    var latitude : Double? = 0.0

    var longitude : Double? = 0.0


    // The minimum distance to change Updates in meters
    private val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 10 // 10 meters


    // The minimum time between updates in milliseconds
    private val MIN_TIME_BW_UPDATES = 1000 * 60 * 1 // 1 minute
        .toLong()
//  private static final long MIN_TIME_BW_UPDATES = 1000 ; // 1 sec

    //  private static final long MIN_TIME_BW_UPDATES = 1000 ; // 1 sec
    // Declaring a Location Manager
    protected var locationManager: LocationManager? = null

    init {
        getLocation()
    }

    @SuppressLint("MissingPermission")
    fun getLocation(): Location? {
        try {
            locationManager = mContext
                ?.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            // getting GPS status
            isGPSEnabled = locationManager!!
                .isProviderEnabled(LocationManager.GPS_PROVIDER)

            // getting network status
            isNetworkEnabled = locationManager!!
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            isWifiEnabled = locationManager!!.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)
            if (!isGPSEnabled && !isNetworkEnabled && !isWifiEnabled) {
                // no network provider is enabled
            } else {
                canGetLocation = true
                if (isNetworkEnabled) {
                    locationManager!!.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this
                    )
                    Log.d("Network", "Network")
                    if (locationManager != null) {
                        location =
                            locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                        if (location != null) {
                            latitude = location!!.latitude
                            longitude = location!!.longitude
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager!!.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this
                        )
                        Log.d("GPS Enabled", "GPS Enabled")
                        if (locationManager != null) {
                            location = locationManager!!
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER)
                            if (location != null) {
                                latitude = location!!.latitude
                                longitude = location!!.longitude
                            }
                        }
                    }
                }
                if (isWifiEnabled) {
                    if (location == null) {
                        locationManager!!.requestLocationUpdates(
                            LocationManager.PASSIVE_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this
                        )
                        Log.d("GPS Enabled", "GPS Enabled")
                        if (locationManager != null) {
                            location = locationManager!!
                                .getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
                            if (location != null) {
                                latitude = location!!.latitude
                                longitude = location!!.longitude
                            }
                        }
                    }
                }
                if (latitude.toString().equals("0.0", ignoreCase = true) ||
                    longitude.toString().equals("0.0", ignoreCase = true)
                ) {
//                  showSettingsAlert();
                    canGetLocation = false
                }
            }
        } catch (e: Exception) {
        }
        return location
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun stopUsingGPS() {
        if (locationManager != null) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return
            }
            locationManager!!.removeUpdates(mContext?.let { GpsTracker(it) })
        }
    }

    /**
     * Function to get latitude
     */
    fun getLatitude(): Double {
        if (location != null) {
            latitude = location!!.latitude
        }

        // return latitude
        return this.latitude!!
    }

    /**
     * Function to get longitude
     */
    fun getLongitude(): Double {
        if (location != null) {
            longitude = location!!.longitude
        }

        // return longitude
        return this.longitude!!
    }

    /**
     * Function to check GPS/wifi enabled
     * @return boolean
     */
    fun canGetLocation(): Boolean {
        return canGetLocation
    }

    /**
     * Function to show settings alert_add_place dialog
     * On pressing Settings button will lauch Settings Options
     */
    fun showSettingsAlert() {
        val alertDialog = AlertDialog.Builder(mContext).create()

        //AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext,AlertDialog.THEME_HOLO_LIGHT);

        // Setting Dialog Title
        alertDialog.setTitle(R.string.gps_settings)

        // Setting Dialog Message
        alertDialog.setMessage(mContext!!.resources.getString(R.string.gps_enable))

        // On pressing Settings button
        alertDialog.setButton(
            Dialog.BUTTON_POSITIVE,
            mContext!!.resources.getString(R.string.settings),
            DialogInterface.OnClickListener { _, _ ->
                val intent =
                    Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                mContext!!.startActivity(intent)
            })
        alertDialog.setButton(
            Dialog.BUTTON_NEGATIVE,
            mContext!!.resources.getString(R.string.cancel),
            DialogInterface.OnClickListener { _, _ ->
                Toast.makeText(
                    mContext,
                    mContext!!.resources.getString(R.string.cancel),
                    Toast.LENGTH_LONG
                ).show()
            })

        // Showing Alert Message
        alertDialog.show()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onLocationChanged(p0: Location?) {
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
    }

    override fun onProviderEnabled(p0: String?) {
    }

    override fun onProviderDisabled(p0: String?) {
    }

}