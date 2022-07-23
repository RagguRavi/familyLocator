package com.family.locator

import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.Bundle
import android.telephony.SmsManager
import android.telephony.SubscriptionManager
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.family.locator.BroadCastReceiver.SmsBroadCastReceiver
import com.family.locator.utitlity.PermissionUtitlity


class TestActivity : AppCompatActivity(),View.OnClickListener {
    var button:Button? = null
    val TAG = "TestActivity"
    var permissionUtitlity:PermissionUtitlity?=null

    private var smsLisnter: SmsBroadCastReceiver?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        button = findViewById(R.id.requestPermissionButton)
        button?.setOnClickListener(this)

        permissionUtitlity = PermissionUtitlity(this,this)


    }

    override fun onClick(view: View?) {
        var id = view?.id

        when(id) {
            R.id.requestPermissionButton -> {
                permissionUtitlity?.requestPermissions()
                Toast.makeText(applicationContext,"Hey this is permsiiion request button: "+permissionUtitlity?.hasExternalReadPermission()+" "+ permissionUtitlity?.hasLocationForeGround()+" "+permissionUtitlity?.hasLocationBackground(),Toast.LENGTH_LONG).show()
               var sm =  getSystemService(SubscriptionManager::class.java)

               /* var list = sm.completeActiveSubscriptionInfoList
                for(a in list) {
                    Log.d("TEST","${a.cardId},${a.carrierName},${a.number}, ${a.subscriptionId}")
                }*/
                var a = sm.getActiveSubscriptionInfoForSimSlotIndex(0)

                var audioManager = getSystemService(AudioManager::class.java)
               var ringerMode =  audioManager.ringerMode
                var ringModeStr:String = "";

                if(AudioManager.RINGER_MODE_NORMAL == ringerMode) {
                    ringModeStr = "Normal"
                } else if(AudioManager.RINGER_MODE_SILENT == ringerMode) {
                    ringModeStr = "Silent"
                } else if(AudioManager.RINGER_MODE_VIBRATE == ringerMode) {
                    ringModeStr = "Vibration"
                }

                audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL

                val streamMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING)
                val seventyVolume:Int = (streamMaxVolume * 100) as Int
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, seventyVolume, 0);
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


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == 0 && grantResults.isNotEmpty()) {
            for(i in grantResults.indices) {
                if(grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("PermissionGranted","${permissions[i]} granted")
                }
            }
        }
    }
}