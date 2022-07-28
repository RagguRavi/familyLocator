package com.family.locator.utitlity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class PermissionUtility(var context:Context, var activity:Activity) {

    companion object {
        public const val LOCATION_PERMISSION_CODE = 1
        public const val SMS_PERMISSION_CODE = 2
        public const val READ_PHONE_STATE_PERMISSION = 3
    }

    fun askForPermissions() {
        askForLocationPermission()
        requestReadAndSendSmsPermission()

    }


    private fun hasBackgroundLocationPermission() = ActivityCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_BACKGROUND_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    

    private fun isSmsPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestReadAndSendSmsPermission() {

        if (!isSmsPermissionGranted()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_SMS)) {
                // You may display a non-blocking explanation here, read more in the documentation:
                // https://developer.android.com/training/permissions/requesting.html
            }

            var permissionRequests = mutableListOf<String>();
            permissionRequests.add(Manifest.permission.READ_SMS)
            permissionRequests.add(Manifest.permission.SEND_SMS)
            permissionRequests.add(Manifest.permission.RECEIVE_SMS)
            ActivityCompat.requestPermissions(activity, permissionRequests.toTypedArray(), SMS_PERMISSION_CODE);
        }
    }


    private fun askForLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.d("TEST", "Permission already granted for location")
            }

            activity.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                Log.d("TEST", "Permission is not granted already ")
                AlertDialog.Builder(activity).setTitle("Permission Needed")
                    .setMessage("We need to access Background Location to get the location of person when you request for that via message")
                    .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                        var list = mutableListOf<String>()
                        list.add(Manifest.permission.ACCESS_FINE_LOCATION)
                        ActivityCompat.requestPermissions(activity, list.toTypedArray(), LOCATION_PERMISSION_CODE)
                    })
                    .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, _ ->
                        dialog.dismiss()
                        Toast.makeText(
                            activity,
                            "Application will not work without Location Permission",
                            Toast.LENGTH_LONG
                        ).show()
                    }).show()

            }
            else -> {
                var list = mutableListOf<String>()
                list.add(Manifest.permission.ACCESS_FINE_LOCATION)
                ActivityCompat.requestPermissions(activity, list.toTypedArray(), 1)

            }
        }

    }



    private fun askForBackGroundLocationAccess() {
        when {
            hasBackgroundLocationPermission() -> {
                Log.d("TEST", "Permission already granted for change Notification Policy")
            }

            activity.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_NOTIFICATION_POLICY) -> {
                AlertDialog.Builder(activity).setTitle("Permission Needed")
                    .setMessage("To change the settings from silent to Sound we need this permission")
                    .setPositiveButton("OK", DialogInterface.OnClickListener { _, _ ->
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        val uri: Uri = Uri.fromParts("package", activity.getPackageName(), null)
                        intent.data = uri
                        activity.startActivity(intent);
                    })
                    .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, _ ->
                        dialog.dismiss()
                        Toast.makeText(
                            activity,
                            "We cann't able to change the volume and Mode of phone without this permission",
                            Toast.LENGTH_LONG
                        ).show()
                    }).show()

            }
            else -> {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                val uri: Uri = Uri.fromParts("package", activity.getPackageName(), null)
                intent.data = uri
                activity.startActivity(intent);
            }
        }

    }

}