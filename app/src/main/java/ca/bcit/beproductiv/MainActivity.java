package ca.bcit.beproductiv;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Locale;
import java.util.Timer;

public class MainActivity extends AppCompatActivity {
    private int timerLengthMilliSeconds = 60000 ;
    private TimerState timerState = TimerState.Stopped;
    private int milliSecondsPassed = 0;
    private ProgressBar circularProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addOnClickHandlers();

    }

    private void addOnClickHandlers(){
        Button startNowButton = findViewById(R.id.startButton);
        startNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimer();
                timerState = TimerState.Running;
            }
        });
        Button pauseButton = findViewById(R.id.pauseButton);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timerState = TimerState.Paused;
                updateButtons();
            }
        });

        Button resumeButton = findViewById(R.id.resumeButton);
        resumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timerState = TimerState.Running;
                startTimer();
                updateButtons();
            }
        });

        Button stopButton = findViewById(R.id.stopButton);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timerState = TimerState.Stopped;
//                timerLengthMilliSeconds = 0;
                milliSecondsPassed = 0;
                updateButtons();
            }
        });

        Button settingsButton = findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Settings.class);
                startActivity(intent);
            }
        });

        Button scheduleButton = findViewById(R.id.scheduleButton);
        scheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ScheduleActivity.class);
                startActivity(intent);
            }
        });

    }

    private void updateButtons(){
        Button startNowButton = findViewById(R.id.startButton);
        Button pauseButton = findViewById(R.id.pauseButton);
        Button stopButton = findViewById(R.id.stopButton);
        Button resumeButton = findViewById(R.id.resumeButton);
        if(timerState == TimerState.Running){
            stopButton.setVisibility(View.VISIBLE);
            pauseButton.setVisibility(View.VISIBLE);
            startNowButton.setVisibility(View.GONE);
            resumeButton.setVisibility(View.GONE);
        } else if(timerState == TimerState.Paused){
            stopButton.setVisibility(View.VISIBLE);
            pauseButton.setVisibility(View.GONE);
            startNowButton.setVisibility(View.GONE);
            resumeButton.setVisibility(View.VISIBLE);
        } else if(timerState == TimerState.Stopped){
            stopButton.setVisibility(View.GONE);
            pauseButton.setVisibility(View.GONE);
            startNowButton.setVisibility(View.VISIBLE);
            resumeButton.setVisibility(View.GONE);
        } else {
            stopButton.setVisibility(View.GONE);
            pauseButton.setVisibility(View.GONE);
            startNowButton.setVisibility(View.VISIBLE);
            resumeButton.setVisibility(View.GONE);
        }
    };
    private void startTimer(){
        final android.os.Handler handler = new android.os.Handler();
        final TextView timeView = findViewById(R.id.timeRemaining);
        timerState = TimerState.Running;
        handler.post(new Runnable() {
            @Override
            public void run(){
                if(milliSecondsPassed >= timerLengthMilliSeconds) timerState = TimerState.Completed;
                updateButtons();
                circularProgressBar = findViewById(R.id.progress_circular);
                circularProgressBar.setIndeterminate(false);
                circularProgressBar.setMax(timerLengthMilliSeconds);
                circularProgressBar.setProgress(milliSecondsPassed);
                int secondsToDisplay = (timerLengthMilliSeconds - milliSecondsPassed) / 1000;
                int hours = secondsToDisplay/3600;
                int minutes = (secondsToDisplay%3600)/60;
                int secs = secondsToDisplay%60;
                String time = String.format(Locale.getDefault(),"%d:%02d:%02d", hours, minutes, secs);
                timeView.setText(time);
                if (timerState == TimerState.Running) {
                    milliSecondsPassed+= 1000;
                    handler.postDelayed(this,1000);
                }
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
    }
    @Override
    public void onPause(){
        super.onPause();
        if(timerState == TimerState.Running) {
            // TODO start background timer and show notifications
        } else if(timerState == TimerState.Paused){
            // TODO start background timer and show notifications
        }
    }
}