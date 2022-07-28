package com.family.locator.Services;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.family.locator.BO.FCMTokenBO;
import com.family.locator.DBHelper;
import com.family.locator.utitlity.Constants;
import com.family.locator.utitlity.FireBaseDBHelper;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    static CollectionReference db = null;
    static {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        db = database.collection("log");
    }

    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom()+"\n"+remoteMessage.getData());
        addLog("In onMessageReceived with data :" + remoteMessage.getData());
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use WorkManager.
                scheduleJob(remoteMessage.getData());
            } else {
                // Handle message within 10 seconds
                handleNow();
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

    }

    // [START on_new_token]
    /**
     * There are two scenarios when onNewToken is called:
     * 1) When a new token is generated on initial app startup
     * 2) Whenever an existing token is changed
     * Under #2, there are three scenarios when the existing token is changed:
     * A) App is restored to a new device
     * B) User uninstalls/reinstalls the app
     * C) User clears app data
     */
    @Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        sendRegistrationToServer(token);
    }
    // [END on_new_token]

    private void scheduleJob(Map<String, String> data) {
        DBHelper dbHelper = new DBHelper(this);
        ArrayList<String> list = new ArrayList<>();

        Map<Long,String> mapData = dbHelper.getData();
        for (Map.Entry<Long,String> entry: mapData.entrySet()) {
            list.add(entry.getValue());
        }

        Intent serviceIntent = new Intent(this,LocationSendHelper.class);
        Bundle bundle = new Bundle();

        String phoneNumber = data.get("sendLocationTo");
        boolean isPresent = list.stream().anyMatch( obj -> (phoneNumber.contains(obj)) );

        if(!isPresent) {
            bundle.putString(Constants.FROM_PHONE_NUMBER,null);
        } else {
            bundle.putString(Constants.FROM_PHONE_NUMBER,phoneNumber);
        }

        bundle.putString(Constants.MESSAGE_CONTENT,data.get("message"));
        bundle.putString(Constants.SEND_CURRENT_LOCATION_COMMAND,Boolean.toString(data.get("message").toUpperCase().contains(Constants.SEND_CURRENT_LOCATION_COMMAND)));

        serviceIntent.putExtras(bundle);
        startService(serviceIntent);
    }

    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    private void sendRegistrationToServer(String token) {
        List<String> phoneNumber = getPhoneNumbersDetails();

        FCMTokenBO bo = new FCMTokenBO();
        bo.setNumbers(phoneNumber);
        bo.setToken(token);


        FireBaseDBHelper.saveFcmToken(bo);
    }



    public List<String> getPhoneNumbersDetails() {
        List<String> resultList = new ArrayList();
        SubscriptionManager sm = getSystemService(SubscriptionManager.class);

        List<SubscriptionInfo> list = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            list = sm.getCompleteActiveSubscriptionInfoList();
        }
        for (SubscriptionInfo a: list) {
//             Log.d("TEST","${a.cardId},${a.carrierName},${a.number}, ${a.subscriptionId}")
            resultList.add(a.getCarrierName()+" "+a.getNumber());
        }
        return resultList;

    }
    public static void addLog(String message) {
        Map<String,Object> map = new HashMap<>();
        map.put("tag",TAG);
        map.put("log",message);
        map.put("date",new Date());
        db.add(map);
    }
}
