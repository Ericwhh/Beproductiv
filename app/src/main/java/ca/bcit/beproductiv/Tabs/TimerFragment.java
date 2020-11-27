

package ca.bcit.beproductiv.Tabs;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.logging.XMLFormatter;

import ca.bcit.beproductiv.Database.AppDatabase;
import ca.bcit.beproductiv.Database.TimerData;
import ca.bcit.beproductiv.Database.TimerDataDao;
import ca.bcit.beproductiv.Database.TodoItem;
import ca.bcit.beproductiv.IntervalState;
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

    private String timeViewText;
    private TextView timeView;
    private TextView selectedTaskName;
    private TextView selectedTaskDesc;
    private LinearLayout selectedTaskLayout;
    private MaterialCardView selectedTaskCardView;
    private MaterialButton removeSelectedTaskButton;
    private TextView selectAClassText;
    private ViewPager viewPager;

    private AppDatabase appDatabase;

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
        appDatabase = AppDatabase.getInstance(getContext());
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_timer, container, false);
        timeView = root.findViewById(R.id.timeRemaining);
        selectedTaskName = root.findViewById(R.id.todo_name);
        selectedTaskDesc = root.findViewById(R.id.todo_description);
        selectedTaskCardView = root.findViewById(R.id.card_view);
        selectedTaskLayout = root.findViewById(R.id.selectedTaskLayout);
        removeSelectedTaskButton = root.findViewById(R.id.btnRemoveSelectedTask);
        selectAClassText = root.findViewById(R.id.selectATask);

        viewPager = getActivity().findViewById(R.id.pager);
        if(timerState == TimerState.Stopped) resetTimerValues();
        updateCircleProgress();
        updateViewTimeRemaining();
        startTimer();
        addOnClickHandlers();
        updateButtons();
        updateIntervalCheckMarks();
        setUpSelectedTaskHandlers();
        return root;
    }




    private void startTimer(){
        circularProgressBar.setProgressMax((int)(timerTime * MagicTimerRatio));

        new CountDownTimer(millisRemaining, 50) {
            double prevMillisRemaining = Double.MAX_VALUE;
            public void onTick(long millisUntilFinished) {
                if(timerState != TimerState.Running) {
                    cancel();
                    return;
                }

                millisRemaining = millisUntilFinished;
                if (prevMillisRemaining - millisRemaining > 500) {
                    updateViewTimeRemaining();
                    prevMillisRemaining = millisRemaining;
                }

                circularProgressBar.setProgress((int) millisRemaining);

            }
            public void onFinish() {
                intervalState = intervalState.next();
                if(intervalState == IntervalState.COMPLETED_ALL){
                    timerState = TimerState.Completed;
                }
                circularProgressBar.setProgress(0);
                updateButtons();
                updateIntervalCheckMarks();
                resetTimerValues();
                updateCircleProgress();
                updateViewTimeRemaining();
                startTimer();

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
    private void setUpSelectedTaskHandlers() {
        final TimerDataDao timerDataDao = appDatabase.getTimerDataDao();
        timerDataDao.getTodoItemUID().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                AppDatabase db = AppDatabase.getInstance(getContext());
                if(integer != -1){
                    TodoItem selectedTask = db.getTaskDao().findByUID(integer);
                    System.out.println("Selected UID: " + integer + "task name " + selectedTask.name);
                    selectedTaskName.setText(selectedTask.name);
                    selectedTaskDesc.setText(selectedTask.description);
                    selectedTaskCardView.setOnClickListener(null);
                    selectAClassText.setVisibility(View.GONE);
                    selectedTaskLayout.setVisibility(View.VISIBLE);
                } else {
                    selectedTaskLayout.setVisibility(View.GONE);
                    selectAClassText.setVisibility(View.VISIBLE);
                    selectedTaskCardView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            viewPager.setCurrentItem(1);
                        }
                    });

                }

            }
        });
        removeSelectedTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timerDataDao.setTodoItemUID(-1);
            }
        });
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
    };

    private void updateViewTimeRemaining() {
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