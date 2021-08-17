package com.example.mefitness.view;


import android.os.CountDownTimer;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.airbnb.lottie.LottieAnimationView;
import com.example.mefitness.R;
import com.example.mefitness.viewmodel.CustomBottomDialog;

import java.util.Locale;

public class TimerActivity extends AppCompatActivity implements CustomDialog.CustomDialogListener {

    private static long START_TIME_IN_MILLIS = 10000;
    private TextView mTextViewCountDown;
    private Button mButtonStartPause;
    private Button mButtonReset;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    private LottieAnimationView lottieAnimationView;
    private int seconds, minutes;
    private boolean isCalledOneTime = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        Toolbar toolbar = (Toolbar) findViewById(R.id.timer_toolbar);
        toolbar.setTitle("Temporizador");
        toolbar.setBackgroundColor(getResources().getColor(R.color.primaryColor));
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mTextViewCountDown = findViewById(R.id.text_view_countdown);
        mButtonStartPause = findViewById(R.id.button_start_pause);
        mButtonReset = findViewById(R.id.button_reset);
        lottieAnimationView = findViewById(R.id.lottie_timer);


        mTextViewCountDown.setOnClickListener(v ->{
                openCustomDialog();
            mButtonReset.setVisibility(View.INVISIBLE);
            mButtonStartPause.setVisibility(View.VISIBLE);
        });

        mButtonStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(isCalledOneTime){
                   if (mTimerRunning) {
                       pauseTimer();
                   }
                   else if(START_TIME_IN_MILLIS ==0){
                       Toast.makeText(TimerActivity.this, "Insira um tempo", Toast.LENGTH_SHORT).show();
                   }
                   else {
                       startTimer();
                       lottieAnimationView.setMaxFrame(100);
                       lottieAnimationView.playAnimation();
                   }
               }
               else Toast.makeText(TimerActivity.this, "Insira um tempo", Toast.LENGTH_SHORT).show();
            }
        });
        mButtonReset.setOnClickListener(v -> resetTimer());
    }

    private void startTimer() {
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }
            @Override
            public void onFinish() {
                mTimerRunning = false;
                mButtonStartPause.setText("Start");
                mButtonStartPause.setVisibility(View.INVISIBLE);
                mButtonReset.setVisibility(View.VISIBLE);

                lottieAnimationView.playAnimation();
                lottieAnimationView.pauseAnimation();
            }
        }.start();
        mTimerRunning = true;
        mButtonStartPause.setText("pause");
        mButtonReset.setVisibility(View.INVISIBLE);
    }

    private void pauseTimer() {
        mCountDownTimer.cancel();
        mTimerRunning = false;
        mButtonStartPause.setText("Start");
        mButtonReset.setVisibility(View.VISIBLE);
        lottieAnimationView.pauseAnimation();
    }

    private void resetTimer() {
        mTimeLeftInMillis = START_TIME_IN_MILLIS;
        updateCountDownText();
        mButtonReset.setVisibility(View.INVISIBLE);
        mButtonStartPause.setVisibility(View.VISIBLE);

        lottieAnimationView.playAnimation();
        lottieAnimationView.pauseAnimation();
    }

    private void updateCountDownText() {
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        mTextViewCountDown.setText(timeLeftFormatted);
    }

    public void openCustomDialog(){
        com.example.mefitness.view.CustomDialog customDialog = new com.example.mefitness.view.CustomDialog();
        customDialog.show(getSupportFragmentManager(), "Custom Dialog");

    }

    @Override
    public void appyTexts(String minutes, String seconds) {
        if(seconds.isEmpty()) this.seconds =  0;

        else  this.seconds = Integer.parseInt(seconds);

        if(minutes.isEmpty()) this.minutes = 0;
        else this.minutes = Integer.parseInt(minutes);

        START_TIME_IN_MILLIS = this.seconds*1000+ this.minutes * 60000;
        mTimeLeftInMillis = START_TIME_IN_MILLIS;
        isCalledOneTime = true;
        updateCountDownText();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

}