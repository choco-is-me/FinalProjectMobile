package com.moh.alarmclock;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.moh.alarmclock.Animation.Animation;
import com.moh.alarmclock.Clock.Timer.Timer;
import com.moh.alarmclock.Clock.Timer.TimerPresetPackage.PresetRecyclerAdapter;
import com.moh.alarmclock.Clock.Timer.TimerPresetPackage.TimerPreset;
import com.moh.alarmclock.Clock.Timer.TimerPresetPackage.TimerPresetManager;
import com.moh.alarmclock.Date.TimeUtils;
import com.moh.alarmclock.Runnable.Runnable;
import com.moh.alarmclock.Section.SectionManager;
import com.moh.alarmclock.UI.TextInput;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInflatorView.MoInflaterView;

public class TimerSectionManager implements MainActivity.SelectModeInterface {
    private final MainActivity mainActivity;
    /**
     * timer
     */

    private final String ENTER_TIME = "Please enter a time above first";
    ConstraintLayout timer_liner_layout;
    private TextInputEditText hourTimer;
    private TextInputEditText minuteTimer;
    private TextInputEditText secondTimer;
    private ProgressBar progressBar;
    private Button cancelTimer;
    private Button startTimer;
    private Button pauseTimer;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ImageButton addPresetButton;
    private LinearLayout timer_text_linear_layout;

    Button cancelDeleteButton;
    Button delete;
    private BottomNavigationView bottomNavigation;
    private LinearLayout linearDeleteMode;
    private TextView counterPreset;
    private ConstraintLayout mainLayout;


    public TimerSectionManager(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    void initTimerSection() {
        this.hourTimer = mainActivity.findViewById(R.id.hour_timer_tv);
        this.hourTimer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (hourTimer.getText().toString().length() == 2) {
                    minuteTimer.requestFocus();
                }
            }
        });



        this.minuteTimer = mainActivity.findViewById(R.id.minute_timer_tv);
        this.minuteTimer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (minuteTimer.getText().toString().length() == 2) {
                    secondTimer.requestFocus();
                }
            }
        });

        this.secondTimer = mainActivity.findViewById(R.id.seconds_timer_tv);


        this.hourTimer.setOnFocusChangeListener(TextInput.twoDigitFocusChangeListener());
        this.minuteTimer.setOnFocusChangeListener(TextInput.twoDigitFocusChangeListener());
        this.secondTimer.setOnFocusChangeListener(TextInput.twoDigitFocusChangeListener());


        this.cancelTimer = mainActivity.findViewById(R.id.cancel_time_timer_button);
        this.pauseTimer = mainActivity.findViewById(R.id.pause_time_timer_button);
        this.cancelTimer.setVisibility(View.GONE);
        this.pauseTimer.setVisibility(View.GONE);
        this.startTimer = mainActivity.findViewById(R.id.start_timer_button);
        this.progressBar = mainActivity.findViewById(R.id.barTimer);

        this.pauseTimer.setOnClickListener((v) -> {
            if(Timer.universalTimer != null && Timer.universalTimer.isCreated()) {
                Timer.universalTimer.pause(false);
                updatePauseButton(this.pauseTimer, Timer.universalTimer.getPauseButtonText());

            }
        });

        this.cancelTimer.setBackgroundColor(mainActivity.getColor(R.color.error_color));
        this.cancelTimer.setOnClickListener((v) -> {
            if(Timer.universalTimer.isCreated()){
                Timer.universalTimer.cancel(mainActivity);
                changeButtonLayout(true, false, false);
            }
        });

        this.startTimer.setOnClickListener(this::startTimer);


        TimerPresetManager.load("",mainActivity);
        initRecyclerView();


        this.addPresetButton = mainActivity.findViewById(R.id.add_timer_preset);
        this.addPresetButton.setOnClickListener(this::addPreset);


        this.cancelDeleteButton = mainActivity.findViewById(R.id.cancel_delete_preset_mode);
        this.cancelDeleteButton.setOnClickListener(view -> {
            cancelDeleteAlarmMode();
        });
        this.bottomNavigation = mainActivity.findViewById(R.id.bottom_navigation);
        this.linearDeleteMode = mainActivity.findViewById(R.id.delete_mode_preset);
        this.delete = mainActivity.findViewById(R.id.delete_preset_button);
        this.delete.setOnClickListener(view -> {
            TimerPresetManager.deleteSelected(mainActivity);
            cancelDeleteAlarmMode();
        });
        this.timer_text_linear_layout = mainActivity.findViewById(R.id.timer_text_linear_layout);
        this.counterPreset = mainActivity.findViewById(R.id.preset_counter_textview);
        this.counterPreset.setVisibility(View.INVISIBLE);
        this.mainLayout = mainActivity.findViewById(R.id.main_layout);
        turnDelete(false);


        SectionManager.getInstance().subscribe(value -> Timer.universalTimer.setUpdateTextViews(value == SectionManager.TIMER_SECTION));

    }

    private void updatePauseButton(TextView pauseTimer, String pauseButtonText) {
        pauseTimer.setText(pauseButtonText);
        this.pauseTimer.setBackgroundColor(Timer.universalTimer.showingResume()?
                mainActivity.getColor(R.color.colorPrimary):mainActivity.getColor(R.color.colorPrimary));
    }

    private void initRecyclerView() {
        recyclerView = (RecyclerView) mainActivity.findViewById(R.id.preset_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(mainActivity, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new PresetRecyclerAdapter(TimerPresetManager.getPresets(), mainActivity, new Runnable() {
            @Override
            public <T> void run(T... args) {
                setTextsMilli((Long) args[0]);
            }
        }, () -> showDeleteMode(true), new Runnable() {
            @Override
            public <T> void run(T... args) {
                onSizeOfSelectedChanged((Integer) args[0]);
            }
        });
        recyclerView.setAdapter(mAdapter);
    }






    private long getMilliSeconds(){
        String hour = this.hourTimer.getText().toString();
        String minute = this.minuteTimer.getText().toString();
        String second = this.secondTimer.getText().toString();
        if(hour.isEmpty() && minute.isEmpty() && second.isEmpty()){
            return TimeUtils.getTimeInMilli(hourTimer.getHint().toString(),
                    minuteTimer.getHint().toString(),secondTimer.getHint().toString());
        }
        return TimeUtils.getTimeInMilli(hour,minute,second);
    }

    private void showErrorInput(){
        Toast toast= Toast.makeText(mainActivity.getApplicationContext(),
                ENTER_TIME, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }

    private void setTextsMilli(long milli){
        if(!Timer.universalTimer.isCreated()){
            String[] parts = TimeUtils.convertMilli(milli);
            this.hourTimer.setText(parts[0]);
            this.minuteTimer.setText(parts[1]);
            this.secondTimer.setText(parts[2]);
        }

    }

    private void addPreset(View v){
        long milliSeconds = getMilliSeconds();
        if (milliSeconds==0) {
            showErrorInput();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
            builder.setTitle("Timer Preset (" + TimeUtils.convertToReadableFormat(milliSeconds) + ")");
            builder.setMessage("Set a name to add this timer as a preset");

            View dialogView = MoInflaterView.inflate(R.layout.alert_dialog_timer_preset, mainActivity);
            TextInputEditText input = dialogView.findViewById(R.id.textField_alertDialogTimerPreset);
            builder.setView(dialogView);

            // Set up the buttons
            builder.setPositiveButton("Add", null);
            builder.setNegativeButton("Cancel", null);
            AlertDialog alertDialog = builder.create();

            alertDialog.setOnShowListener(dialogInterface -> {
                Button button = ((AlertDialog) alertDialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(view -> {
                    if (input.getText().toString().isEmpty()) {
                        input.setError("Please enter a name to save this preset");
                        return;
                    }
                    TimerPresetManager.add(new TimerPreset(input.getText().toString(),milliSeconds),mainActivity);
                    mAdapter.notifyDataSetChanged();
                    //Dismiss once everything is OK.
                    alertDialog.dismiss();
                });
            });

            alertDialog.show();
        }
    }


    private void startTimer(View v){
        long milliSeconds = getMilliSeconds();
        if (milliSeconds == 0) {
            showErrorInput();
        } else {
            // startService the timer
            if(Timer.universalTimer == null || !Timer.universalTimer.isCreated()){
                hideKeyboardFrom(mainActivity,v);
                Timer.universalTimer = new Timer(new Runnable() {
                    @Override
                    public <T> void run(T... args) {
                        changeButtonLayout((Boolean) args[0],(Boolean) args[1],(Boolean) args[2]);
                    }
                }
                        , mainActivity, milliSeconds,
                        this.progressBar,
                        new Button[]{this.startTimer, this.cancelTimer, this.pauseTimer},
                        this.hourTimer, this.minuteTimer, this.secondTimer);
                Timer.universalTimer.startTimer();
                updatePauseButton(this.pauseTimer, Timer.universalTimer.getPauseButtonText());
                changeButtonLayout(false, true, true);
            }

        }
    }

    private void setTimerValues(int minutes){
        if(minutes > 59){
            this.hourTimer.setText(minutes/60+"");
            this.minuteTimer.setText(minutes%60+"");
        }else {
            this.minuteTimer.setText(minutes <10?"0":""+ minutes);
        }
    }

    private void changeButtonLayout(boolean start, boolean pause, boolean cancel) {
        showRecyclerPreset(start);
        showAddPreset(start);
        Animation.animateNoTag(startTimer,start ? View.VISIBLE : View.GONE,start? Animation.APPEAR: Animation.DISAPPEAR);
        Animation.animateNoTag(cancelTimer,cancel ? View.VISIBLE : View.GONE,cancel? Animation.APPEAR: Animation.DISAPPEAR);
        Animation.animateNoTag(pauseTimer,pause ? View.VISIBLE : View.GONE,pause? Animation.APPEAR: Animation.DISAPPEAR);
    }

    void closeTimerService() {
        if(TimerPreset.isInDeleteMode)
            return;

        Timer.universalTimer.setTimerTextInputs(this.hourTimer, this.minuteTimer, this.secondTimer);
        Timer.universalTimer.setProgressBar(this.progressBar);
        Timer.universalTimer.setButtons(this.startTimer, this.cancelTimer, this.pauseTimer);
        Timer.universalTimer.cancel(mainActivity, true, true);
        /**
         * ui changes
         */
        if (Timer.universalTimer.isCreated()) {
            //mainActivity.changeLayout(false, false, true, true);
            changeButtonLayout(false, true, true);

            updatePauseButton(this.pauseTimer, Timer.universalTimer.getPauseButtonText());
        }else {
            Timer.universalTimer.update();
           // changeButtonLayout(true, false, false);
            //this.pauseTimer.setText(Timer.universalTimer.getPauseButtonText());
        }
    }




    private void onSizeOfSelectedChanged(int size){
        if(size==0){
            // none of them are selected
            cancelDeleteAlarmMode();
        }else{
            turnDelete(true);
        }
        updatePauseButton(counterPreset, String.format("%d Selected",size));
    }



    public void cancelDeleteAlarmMode(){
        showDeleteMode(false);
    }

    private void showDeleteMode(boolean b){
        TimerPreset.isInDeleteMode = b;

        if(!b)
            Animation.clearLog();

        showLinearDelete(b);
        showNavigation(!b);
        showStartButton(!b);
        showTimerText(!b);
        showCounter(b);
        showAddPreset(!b);
        mAdapter.notifyDataSetChanged();

        if(!b){
            // reset all the presets
            TimerPresetManager.selectAllPresets(false);
        }

        if(!b)
            Animation.clearLog();
    }

    private void showLinearDelete(boolean b){
        Animation.animate(this.linearDeleteMode,b?View.VISIBLE:View.INVISIBLE,
                b? Animation.BOTTOM_TO_TOP_FADE_IN: Animation.MOVE_DOWN_FADE_OUT);
    }

    private void showNavigation(boolean b){
        Animation.animate(this.bottomNavigation,b?View.VISIBLE:View.INVISIBLE,
                b? Animation.BOTTOM_TO_TOP_FADE_IN: Animation.MOVE_DOWN_FADE_OUT);
    }

    private void showStartButton(boolean b){
        Animation.animate(this.startTimer,b?View.VISIBLE:View.INVISIBLE,
                b? Animation.FADE_IN: Animation.FADE_OUT);
    }

    private void showTimerText(boolean b){
        Animation.animate(this.timer_text_linear_layout,b?View.VISIBLE:View.INVISIBLE,
                b? Animation.FADE_IN: Animation.FADE_OUT);
    }

    private void showRecyclerPreset(boolean b){
        Animation.animateNoTag(this.recyclerView,b?View.VISIBLE:View.INVISIBLE,
                b? Animation.FADE_IN: Animation.FADE_OUT);
    }

    private void showCounter(boolean b){
        Animation.animateNoTag(this.counterPreset,b?View.VISIBLE:View.INVISIBLE,
                b? Animation.FADE_IN: Animation.FADE_OUT);
    }

    private void showAddPreset(boolean b){
        Animation.animateNoTag(this.addPresetButton,b?View.VISIBLE:View.INVISIBLE,
                b? Animation.APPEAR: Animation.DISAPPEAR);
    }

    private void turnDelete(boolean on) {
        this.delete.setVisibility(on?View.VISIBLE:View.GONE);
        this.cancelDeleteButton.setVisibility(on?View.GONE:View.VISIBLE);
    }


    public void onWindowFocusChanged(){
        if(TimerPreset.isInDeleteMode){
            showDeleteMode(true);
            mAdapter.notifyDataSetChanged();
        }
    }

//    public static void hideKeyboard(Activity activity) {
//        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
//        //Find the currently focused view, so we can grab the correct window token from it.
//        View view = activity.getCurrentFocus();
//        //If no view currently has focus, create a new one, just so we can grab a window token from it
//        if (view == null) {
//            view = new View(activity);
//        }
//        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public boolean isSelecting() {
        return TimerPreset.isInDeleteMode;
    }
}