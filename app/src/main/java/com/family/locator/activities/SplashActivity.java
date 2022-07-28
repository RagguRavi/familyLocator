package com.family.locator.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.family.locator.MainActivity;
import com.family.locator.R;
import com.family.locator.utitlity.FireBaseDBHelper;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ImageView view = findViewById(R.id.familyIcon);

       /* Animation animation = AnimationUtils.loadAnimation(this,R.anim.top_aimation);
        animation.setDuration(2000);
        view.setAnimation(animation);*/
        FireBaseDBHelper.getFCMToken();
        new Handler().postDelayed(() ->
                {
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);

                    finish();
                },1000
        );


    }
}