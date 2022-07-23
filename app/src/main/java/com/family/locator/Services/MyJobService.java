package com.family.locator.Services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

public class MyJobService extends JobService {
    private static String TAG = "MyJobService";
    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG,"Hey Job is started");
        doBackgroundWork();
        return true;
    }

    private void doBackgroundWork() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for(int i=0;i<10;i++) {
                        Log.d(TAG,"Runnning background task for number: "+i);
                        Thread.sleep(1000);
                    }
                } catch (Exception e) {
                    Log.e(TAG,"Error while doing background work",e);
                }
            }
        }).start();
    }
    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
