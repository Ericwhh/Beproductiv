package ca.bcit.beproductiv.Tabs;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.logging.XMLFormatter;

import ca.bcit.beproductiv.IntervalState;
import ca.bcit.beproductiv.TimerNotification;
import ca.bcit.beproductiv.R;
import ca.bcit.beproductiv.TimerState;


public class TimerFragment extends Fragment {
    private TimerState timerState = TimerState.Stopped;
    private IntervalState intervalState;
    private CircularProgressBar circularProgressBar;
    private int timerTime;
    private long millisRemaining;
    private int retrievedShortBreakMin = 5;
    private int retrievedLongBreakMin = 15;
    private int retrievedFocusMin = 30;
    private static final int MILIS_IN_A_SECOND = 1000;
    private static final int SECONDS_IN_A_MIN = 60;
    private static final int SECONDS_IN_AN_HOUR = 3600;
    private static final double MagicTimerRatio = 4.0/3;

    private View root;
    public TimerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TimerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TimerFragment newInstance() {
        TimerFragment fragment = new TimerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intervalState = IntervalState.INTERVAL_ONE;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_timer, container, false);
        if(timerState == TimerState.Stopped) resetTimerValues();
        updateCircleProgress();
        updateViewTimeRemaining();
        startTimer();
        addOnClickHandlers();
        updateButtons();
        updateIntervalCheckMarks();

        return root;
    }

    private void startTimer(){
        circularProgressBar.setProgressMax((int)(timerTime * MagicTimerRatio));

        new CountDownTimer(millisRemaining, 10) {
            public void onTick(long millisUntilFinished) {
                if(timerState != TimerState.Running) {
                    cancel();
                    return;
                }
                millisRemaining = millisUntilFinished;
                circularProgressBar.setProgress((int) millisRemaining);
                updateViewTimeRemaining();
            }
            public void onFinish() {
                triggerNotification();
                SharedPreferences sharedConfig = PreferenceManager.getDefaultSharedPreferences(getContext());
                String autoStartInterval = sharedConfig.getString("auto_start_interval", "start_manually");
                intervalState = intervalState.next();
                if (autoStartInterval.equals("start_manually")) {
                    timerState = TimerState.Paused;
                }
                if(intervalState == IntervalState.COMPLETED_ALL){
                    timerState = TimerState.Completed;
                }
                updateButtons();
                updateIntervalCheckMarks();
                resetTimerValues();
                updateCircleProgress();
                updateViewTimeRemaining();

                if (autoStartInterval.equals("start_immediately")) {
                    startTimer();
                }
            }

            private void triggerNotification() {
                switch(intervalState) {
                    case INTERVAL_ONE:
                        TimerNotification.notifyShortBreak(getActivity());
                        break;
                    case INTERVAL_TWO:
                        TimerNotification.notifyLongBreak(getActivity());
                        break;
                    case BREAK_ONE:
                    case BREAK_TWO:
                        TimerNotification.notifyIntervalStart(getActivity());
                        break;
                    case COMPLETED_ALL:
                    default:
                        TimerNotification.notifyComplete(getActivity());
                        break;
                }
            }
        }.start();
    }


    private void setTimerIntervals(){
        SharedPreferences sharedConfig = PreferenceManager.getDefaultSharedPreferences(root.getContext());

        if(timerState == TimerState.Stopped) {
            retrievedFocusMin = Integer.parseInt(sharedConfig.getString("interval_focus", "30"));
            retrievedLongBreakMin = Integer.parseInt(sharedConfig.getString("interval_long_break", "15"));
            retrievedShortBreakMin = Integer.parseInt(sharedConfig.getString("interval_break", "5"));
        }
    }

    private void resetTimerValues(){
        setTimerIntervals();
        timerTime = retrievedFocusMin * MILIS_IN_A_SECOND * SECONDS_IN_A_MIN;
        if(intervalState.isBreak()){
            if(intervalState == IntervalState.BREAK_ONE){
                 timerTime = retrievedShortBreakMin * MILIS_IN_A_SECOND * SECONDS_IN_A_MIN;
            } else {
                timerTime = retrievedLongBreakMin * MILIS_IN_A_SECOND * SECONDS_IN_A_MIN;
            }
        }
        millisRemaining = timerTime;

    }

    private void addOnClickHandlers() {
        Button startNowButton = root.findViewById(R.id.startButton);
        startNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timerState = TimerState.Running;
                if(intervalState == IntervalState.COMPLETED_ALL) intervalState = intervalState.next();
                updateIntervalCheckMarks();
                startTimer();
                updateButtons();
            }
        });
        Button pauseButton = root.findViewById(R.id.pauseButton);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timerState = TimerState.Paused;
                updateButtons();
            }
        });

        Button resumeButton = root.findViewById(R.id.resumeButton);
        resumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timerState = TimerState.Running;
                startTimer();
                updateButtons();
            }
        });

        Button stopButton = root.findViewById(R.id.stopButton);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timerState = TimerState.Stopped;
                intervalState = IntervalState.INTERVAL_ONE;
                updateIntervalCheckMarks();
                resetTimerValues();
                updateCircleProgress();
                updateViewTimeRemaining();
                updateButtons();
            }
        });
    }

    private void updateIntervalCheckMarks(){
        ImageView intervalStatus1 = root.findViewById(R.id.intervalStatus1);
        ImageView intervalStatus2 = root.findViewById(R.id.intervalStatus2);
        ImageView intervalStatus3 = root.findViewById(R.id.intervalStatus3);

        int numCheckMarks = intervalState.getCheckMarks();

        switch(numCheckMarks){
            case 1:
                intervalStatus1.setImageResource(R.drawable.ic_interval_complete);
                intervalStatus2.setImageResource(R.drawable.ic_interval_incomplete);
                intervalStatus3.setImageResource(R.drawable.ic_interval_incomplete);
                break;
            case 2:
                intervalStatus1.setImageResource(R.drawable.ic_interval_complete);
                intervalStatus2.setImageResource(R.drawable.ic_interval_complete);
                intervalStatus3.setImageResource(R.drawable.ic_interval_incomplete);
                break;
            case 3:
                intervalStatus1.setImageResource(R.drawable.ic_interval_complete);
                intervalStatus2.setImageResource(R.drawable.ic_interval_complete);
                intervalStatus3.setImageResource(R.drawable.ic_interval_complete);
                break;
            default:
                intervalStatus1.setImageResource(R.drawable.ic_interval_incomplete);
                intervalStatus2.setImageResource(R.drawable.ic_interval_incomplete);
                intervalStatus3.setImageResource(R.drawable.ic_interval_incomplete);
                break;
        }

    }

    private void updateCircleProgress() {
        circularProgressBar = root.findViewById(R.id.progress_circular);
        circularProgressBar.setProgressMax((int)(timerTime * MagicTimerRatio));
        circularProgressBar.setProgress(millisRemaining);
    }

    private void updateButtons(){
        Button startNowButton = root.findViewById(R.id.startButton);
        Button pauseButton = root.findViewById(R.id.pauseButton);
        Button stopButton = root.findViewById(R.id.stopButton);
        Button resumeButton = root.findViewById(R.id.resumeButton);
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
    }

    private void updateViewTimeRemaining(){
        final TextView timeView = root.findViewById(R.id.timeRemaining);
        if(timerState != TimerState.Completed){
            long secondsUntilFinished =  millisRemaining / MILIS_IN_A_SECOND;
            long hours = secondsUntilFinished/SECONDS_IN_AN_HOUR;
            long minutes = (secondsUntilFinished%SECONDS_IN_AN_HOUR)/SECONDS_IN_A_MIN;
            long secs = secondsUntilFinished%SECONDS_IN_A_MIN;
            String time = String.format(Locale.getDefault(),"%d:%02d:%02d", hours, minutes, secs);
            timeView.setText(time);
        }else {
            String completed = "Done";
            timeView.setText(completed);
        }
    }
}