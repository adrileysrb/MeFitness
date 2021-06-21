package com.example.mefitness.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.mefitness.R;

import static java.lang.Thread.sleep;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ImageView img = (ImageView)findViewById(R.id.logoSplash);
                    img.setBackgroundResource(R.drawable.animation);

                    // Get the background, which has been compiled to an AnimationDrawable object.
                    AnimationDrawable frameAnimation = (AnimationDrawable) img.getBackground();

                    // Start the animation (looped playback by default).
                    frameAnimation.start();
                    sleep(3000);
                    Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.zoom_in, R.anim.static_animation);
                    finish();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                finally {

                }
            }
        });
        thread.start();
    }
}