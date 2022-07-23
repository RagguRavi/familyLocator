package com.family.locator.utitlity;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.family.locator.BO.UserLastLocationBO;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ktx.Firebase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

public class FireBaseDBHelper {
    static final String TAG = "FireBaseDBHelper";
    public static void save(UserLastLocationBO bo) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        Map<String, Object> user = new HashMap<>();
        user.put("phoneNumber", bo.getPhoneNumber());
        user.put("latitude", bo.getLatitude());
        user.put("longitude", bo.getLongitude());
        user.put("date",bo.getDate());
        user.put("phoneModal",getDeviceName());
        user.put("phoneNumberInPhone",bo.getPhoneNumberList());
        user.put("googleMapUrl",bo.getGoogleUrl());

        database.collection("users")
                .add(user);

    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return model.toUpperCase();
        }
        return manufacturer.toUpperCase() + " " + model;
    }




    public static void getFCMToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Log.w("TEST", "Fetching FCM registration token failed", task.getException());
                    return;
                }

                // Get new FCM registration token
                String token = task.getResult();

                // Log and toast

                Log.d("TEST", token);
//                Toast.makeText(this, "Token is ${token}", Toast.LENGTH_SHORT).show()
            }

        });
    }
}
