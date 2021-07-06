package com.example.mefitness.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.mefitness.R;

import static java.lang.Thread.sleep;

public class SplashScreen extends AppCompatActivity {

    private final int SPLASH_SCREEN_TIME = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        splashScreen(SPLASH_SCREEN_TIME);
    }

    private void splashScreen(int timeSplashScreen) {
        final Thread thread = new Thread(() -> {
            try {
                startAnimation();
                sleep(timeSplashScreen);
                startActivity();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    private void startActivity() {
        startActivity(new Intent(SplashScreen.this, LoginActivity.class));
        overridePendingTransition(R.anim.zoom_in, R.anim.static_animation);
        finish();
    }

    private void startAnimation() {
        ImageView img = findViewById(R.id.animationContainer);
        img.setBackgroundResource(R.drawable.animation);
        AnimationDrawable frameAnimation = (AnimationDrawable) img.getBackground();
        frameAnimation.start();
    }
}