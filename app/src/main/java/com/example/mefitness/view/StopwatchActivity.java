package com.example.mefitness.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.mefitness.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class StopwatchActivity extends AppCompatActivity {

    TextView timerText;
    Button stopStartButton, resetButton, markButton;
    LottieAnimationView lottieAnimationView;

    Timer timer;
    TimerTask timerTask;
    Double time = 0.0;

    boolean timerStarted = false;
    private ArrayAdapter<String> arrayAdapter;
    List<String> tempos;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch);

        Toolbar toolbar = (Toolbar) findViewById(R.id.stopwatch_toolbar);
        toolbar.setTitle("Cronômetro");
        toolbar.setBackgroundColor(getResources().getColor(R.color.primaryColor));
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        timerText = (TextView) findViewById(R.id.timerText);
        stopStartButton = (Button) findViewById(R.id.startStopButton);
        resetButton = findViewById(R.id.stopwatch_reset);
        markButton = findViewById(R.id.button);
        lottieAnimationView = findViewById(R.id.lottie_stopwatch);




        tempos = new ArrayList<>();
        ListView listView = findViewById(R.id.timerStopwatchListView);
        arrayAdapter = new ArrayAdapter<>(this, R.layout.simple_list_item, tempos);
        listView.setAdapter(arrayAdapter);


        timer = new Timer();


        resetButton.setOnClickListener(v -> resetTapped());
        stopStartButton.setOnClickListener(v -> startStopTapped());
        markButton.setOnClickListener(v -> addMark());

    }

    public void addMark(){
       if(stopStartButton.getText() == "STOP") {
           tempos.add(timerText.getText().toString());
           arrayAdapter.notifyDataSetChanged();
       }
       else Toast.makeText(StopwatchActivity.this, "Inicie o cronômetro", Toast.LENGTH_SHORT).show();
    }

    public void resetTapped()
    {
        AlertDialog.Builder resetAlert = new AlertDialog.Builder(this);
        resetAlert.setTitle("Resetar o timer");
        resetAlert.setMessage("Você deseja resetar o cronômetro? ");
        resetAlert.setPositiveButton("Sim", (dialogInterface, i) -> {
            if(timerTask != null)
            {
                timerTask.cancel();
                stopStartButton.setText("START");
                time = 0.0;
                timerStarted = false;
                timerText.setText(formatTime(0,0,0));
                tempos.clear();
                arrayAdapter.notifyDataSetChanged();
                lottieAnimationView.pauseAnimation();
            }
        });

        resetAlert.setNeutralButton("Não", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                //do nothing
            }
        });

        resetAlert.show();

    }

    public void startStopTapped()
    {
        if(timerStarted == false)
        {
            lottieAnimationView.playAnimation();
            timerStarted = true;
            stopStartButton.setText("STOP");

            startTimer();
        }
        else
        {
            lottieAnimationView.pauseAnimation();
            timerStarted = false;
            stopStartButton.setText("START");

            timerTask.cancel();
        }
    }

    private void startTimer()
    {
        timerTask = new TimerTask()
        {
            @Override
            public void run()
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        time++;
                        timerText.setText(getTimerText());
                    }
                });
            }

        };
        timer.scheduleAtFixedRate(timerTask, 0 ,1000);
    }


    private String getTimerText()
    {
        int rounded = (int) Math.round(time);

        int seconds = ((rounded % 86400) % 3600) % 60;
        int minutes = ((rounded % 86400) % 3600) / 60;
        int hours = ((rounded % 86400) / 3600);

        return formatTime(seconds, minutes, hours);
    }

    private String formatTime(int seconds, int minutes, int hours)
    {
        return String.format("%02d",hours) + " : " + String.format("%02d",minutes) + " : " + String.format("%02d",seconds);
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
