package com.family.locator.Services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MyJobService extends IntentService {
    private static String TAG = "MyJobService";
     static CollectionReference db = null;

    static {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        db = database.collection("log");
    }
    public MyJobService(){
        super(TAG);
        Log.d(TAG,"MyJobService default constructor Intent of My JobService");
        addLog("MyJobService default constructor Intent of My JobService");
    }
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public MyJobService(String name) {
        super(name);
        addLog("MyJobService with name constructor Intent of My JobService"+name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        addLog("On Handle Intent of My JobService");

    }

    @Override
    public void onStart(@Nullable Intent intent, int startId) {
        super.onStart(intent, startId);
        addLog("onStart of My JobService");
    }

    @Override
    public boolean stopService(Intent name) {
        addLog("stopService of My JobService");
        return super.stopService(name);
    }


    public static void addLog(String message) {
        Map<String,Object> map = new HashMap<>();
        map.put("tag",TAG);
        map.put("log",message);
        map.put("date",new Date());
        db.add(map);
    }
}
