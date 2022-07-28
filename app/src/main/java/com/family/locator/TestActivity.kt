package com.family.locator

import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.family.locator.BroadCastReceiver.SmsBroadCastReceiver
import com.family.locator.utitlity.PermissionUtility
import com.google.android.material.snackbar.Snackbar


class TestActivity : AppCompatActivity(),View.OnClickListener {
    var button:Button? = null
    val TAG = "TestActivity"
    var permissionUtitlity:PermissionUtility?=null

    private var smsLisnter: SmsBroadCastReceiver?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        button = findViewById(R.id.requestPermissionButton)
        button?.setOnClickListener(this)

        permissionUtitlity = PermissionUtility(this,this)
        permissionUtitlity!!.askForPermissions()
    }

    override fun onClick(view: View?) {
        var id = view?.id

        when(id) {
            R.id.requestPermissionButton -> {

                permissionUtitlity?.askForPermissions()
            }
        }
    }


    fun sendSms() {
        var smsManager:SmsManager = SmsManager.getDefault()

        var number = "9802883865"
        var msg = "hee"
        smsManager.sendTextMessage(number,null,msg,null,null);
        Toast.makeText(applicationContext,"SMS Send",Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        Toast.makeText(this,"Destory is called",Toast.LENGTH_SHORT)
        super.onDestroy()
    }


    private fun askForLocationPermission() {
        Toast.makeText(this,"Asking for permission",Toast.LENGTH_LONG).show()
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                Log.d("TEST","Permission already granted for location")
            }

            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                Log.d("TEST","Permission is not granted already ")
                AlertDialog.Builder(this)
                    .setTitle("Permission Needed")
                    .setMessage("We need to access Background Location to get the location of person when you request for that via message")
                    .setPositiveButton("OK",DialogInterface.OnClickListener { dialog, which ->
                        var list = mutableListOf<String>()
                        list.add(Manifest.permission.ACCESS_FINE_LOCATION)
                        ActivityCompat.requestPermissions(this,list.toTypedArray(),1)
                    })
                    .setNegativeButton("Cancel",DialogInterface.OnClickListener{ dialog,which ->
                          dialog.dismiss()
                        Toast.makeText(this,"Application will not work without Location Permission",Toast.LENGTH_LONG).show()
                    }).show()

            }
            else -> {
                var list = mutableListOf<String>()
                list.add(Manifest.permission.ACCESS_FINE_LOCATION)
                ActivityCompat.requestPermissions(this,list.toTypedArray(),1)

            }
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        if(requestCode == PermissionUtility.LOCATION_PERMISSION_CODE && grantResults.isNotEmpty()) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this,"Permission Granted for location",Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this,"Permission Denied for location",Toast.LENGTH_LONG).show()
            }
        } else if(requestCode == PermissionUtility.SMS_PERMISSION_CODE && grantResults.isNotEmpty()) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this,"Permission Granted for SMS",Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this,"Permission Denied for SMS",Toast.LENGTH_LONG).show()
            }
        }
    }
}