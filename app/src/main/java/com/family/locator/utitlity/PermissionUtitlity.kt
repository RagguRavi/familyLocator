package com.family.locator.utitlity

import android.Manifest
import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity


class PermissionUtitlity(var context:Context,var activity:Activity) {



    fun checkAllPermissions() {
        requestPermissions()
        requestReadAndSendSmsPermission()
    }


    fun hasNotificationPolicyPermission() =
        ActivityCompat.checkSelfPermission(context,
            Manifest.permission.ACCESS_NOTIFICATION_POLICY) == PackageManager.PERMISSION_GRANTED


     fun hasExternalReadPermission() =
        ActivityCompat.checkSelfPermission(context,
            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

     fun hasLocationForeGround() =
        ActivityCompat.checkSelfPermission(context,
            Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

     fun hasLocationBackground() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        }

    fun hasPhonePermission() =
        ActivityCompat.checkSelfPermission(context,
            Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED

    fun isSmsPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_SMS
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestReadAndSendSmsPermission() {

        if(!isSmsPermissionGranted()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.READ_SMS
                )
            ) {
                // You may display a non-blocking explanation here, read more in the documentation:
                // https://developer.android.com/training/permissions/requesting.html
            }

            var permissionRequests = mutableListOf<String>();
            permissionRequests.add(Manifest.permission.READ_SMS)
            permissionRequests.add(Manifest.permission.SEND_SMS)
            permissionRequests.add(Manifest.permission.RECEIVE_SMS)
            ActivityCompat.requestPermissions(activity, permissionRequests.toTypedArray(), 0);
        }
    }

    fun requestPermissions() {
        var permissionRequests = mutableListOf<String>();

        if(!hasExternalReadPermission()) {
            permissionRequests.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if(!hasLocationForeGround()) {
            permissionRequests.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        if(!hasLocationBackground()) {
            permissionRequests.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }

        if(!hasPhonePermission()) {
            permissionRequests.add(Manifest.permission.READ_PHONE_STATE)
        }

        if(!hasNotificationPolicyPermission()) {
            permissionRequests.add(Manifest.permission.ACCESS_NOTIFICATION_POLICY)
        }

        if(permissionRequests.isNotEmpty()) {
            ActivityCompat.requestPermissions(activity,permissionRequests.toTypedArray(),0)
        }
    }


}