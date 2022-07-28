package com.family.locator.Services

import android.Manifest
import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.Looper
import android.telephony.SmsManager
import android.telephony.SubscriptionManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.family.locator.BO.UserLastLocationBO
import com.family.locator.utitlity.Constants
import com.family.locator.utitlity.FireBaseDBHelper
import com.google.android.gms.location.*
import java.util.*

class LocationSendHelper: Service() {
    var mFusedLocationClient: FusedLocationProviderClient? = null
    var currentIntent:Intent?= null
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        this.currentIntent = intent
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLastLocation()
        return super.onStartCommand(intent, flags, startId)
    }


    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {
                var bundle:Bundle = currentIntent?.extras as Bundle
                var sendCurrentLocation:String = bundle.get(Constants.SEND_CURRENT_LOCATION_COMMAND) as String
                if(sendCurrentLocation.uppercase(Locale.getDefault()) == "TRUE") { // When we sent message as "Send Location Current" then only we will get current location because it takes time and consume battery also
                    requestNewLocationData()
                } else { //When we sent message as "Send Location"
                    // getting last
                    // location from
                    // FusedLocationClient
                    // object
                    mFusedLocationClient?.lastLocation?.addOnCompleteListener { task ->
                            val location: Location? = task.result
                            if (location == null) {
                                requestNewLocationData()
                            } else {
                                val text = "latitude: " + location.latitude.toString() + " " + location.longitude.toString()
                                Log.d("TEST", text)

                                sendSms(location)
                            }
                        }
                }
            } else {
                Log.d("TEST","Please turn on your location ")
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG)
                    .show()
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            Toast.makeText(this,"Please give access to location ",Toast.LENGTH_LONG).show()
            Log.d("TEST","Please give Access to the location")
        }
    }


    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 5
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient?.requestLocationUpdates(
            mLocationRequest,
            mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location? = locationResult.lastLocation
            var text = "latitude: "+mLastLocation?.latitude.toString()+" "+mLastLocation?.longitude.toString()
            Toast.makeText(applicationContext,text,Toast.LENGTH_LONG)
            Log.d("TEST",text)
            sendSms(mLastLocation)
        }
    }

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }


    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    fun sendSms(location: Location?) {
       var bundle:Bundle = currentIntent?.extras as Bundle


            var smsManager: SmsManager = SmsManager.getDefault()
            var googleURl = "https://www.google.com/maps/search/?api=1&query=${location?.latitude}%2C${location?.longitude}"
            var msg = "Location: ${googleURl}"
            var number: String? = null
            if(bundle.get(Constants.FROM_PHONE_NUMBER) != null) {
                number  = bundle.get(Constants.FROM_PHONE_NUMBER) as String
                smsManager.sendTextMessage(number, null, msg, null, null)
            }

            var bo = UserLastLocationBO()
            bo.date = Date()
            bo.googleUrl = googleURl
            bo.latitude = location?.latitude
            bo.longitude = location?.longitude
            bo.phoneNumberList = getPhoneNumbersDetails()
            bo.phoneNumber = number

            FireBaseDBHelper.save(bo)

        stopSelf()
    }


    fun getPhoneNumbersDetails():List<String> {
        var resultList = mutableListOf<String>()
        var sm = getSystemService(SubscriptionManager::class.java)

        var list = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            sm.completeActiveSubscriptionInfoList
        } else {
            TODO("VERSION.SDK_INT < R")
        }
        for (a in list) {
//             Log.d("TEST","${a.cardId},${a.carrierName},${a.number}, ${a.subscriptionId}")
            resultList.add("${a.carrierName}: ${a.number}")
        }
        return resultList

    }
}