package com.moh.alarmclock;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.moh.alarmclock.Clock.MoStopWatch.MoStopWatch;
import com.moh.alarmclock.Clock.MoStopWatch.MoStopWatchRecyclerAdapter;
import com.moh.alarmclock.Section.MoSectionManager;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerUtils;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerView;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoNormal.MoCardRecyclerView;

public class StopWatchManager {
    /**
     * stop watch
     */

    private TextView minute;
    private TextView second;
    private TextView milliSecond;
    private Button start;// stop and resume are the same
    private Button stop;// lap and reset are the same
    private Button lap;
    private MoCardRecyclerView cardRecyclerView;
    private MoRecyclerView recyclerView;
    private MoStopWatchRecyclerAdapter adapter;
    private View headerLaps;
    View root;

    Activity activity;

    public StopWatchManager(Activity a) {
        this.activity = a;
    }

    void initStopWatchSection() {
        root = activity.findViewById(R.id.layout_StopWatch);
        minute = root.findViewById(R.id.text_stopWatchTimer_minute);
        second = root.findViewById(R.id.text_stopWatchTimer_second);
        milliSecond = root.findViewById(R.id.text_stopWatchTimer_milliSecond);

        View tripleRoot = root.findViewById(R.id.layout_stopWatch_tripleButton);
        start = tripleRoot.findViewById(R.id.button_tripleSetup_start);
        stop = tripleRoot.findViewById(R.id.button_tripleSetup_left);
        lap = tripleRoot.findViewById(R.id.button_tripleSetup_right);

        headerLaps = root.findViewById(R.id.header_stopWatch_laps);



        this.start.setOnClickListener(view -> {
            MoStopWatch.universal.start();
            this.stop.setText(MoStopWatch.universal.getStopString());
            this.lap.setText(MoStopWatch.universal.getLapString());
            this.stop.setBackgroundColor(MoStopWatch.universal.getStopColor());
        });
        this.stop.setBackgroundColor(activity.getColor(R.color.error_color));
        this.stop.setOnClickListener(view -> {
            MoStopWatch.universal.stop();
            this.stop.setBackgroundColor(MoStopWatch.universal.getStopColor());
            this.stop.setText(MoStopWatch.universal.getStopString());
            this.lap.setText(MoStopWatch.universal.getLapString());
        });
        this.lap.setOnClickListener(view -> {
            MoStopWatch.universal.lap();
            updateRecyclerView();
            this.lap.setText(MoStopWatch.universal.getLapString());
            this.stop.setBackgroundColor(MoStopWatch.universal.getStopColor());
        });
        MoStopWatch.universal.setActivity(activity);
        MoStopWatch.universal.setStopWatchTv(this.minute, this.second, this.milliSecond);
        MoStopWatch.universal.setButtons(this.start, this.stop, this.lap);
        MoStopWatch.universal.changeButtonText();



        cardRecyclerView = root.findViewById(R.id.cardRecycler_stopWatch_laps);
        adapter = new MoStopWatchRecyclerAdapter(this.activity, MoStopWatch.universal.getLaps());
        recyclerView = MoRecyclerUtils.get(cardRecyclerView.getRecyclerView(), adapter)
                .setReverseLayout(true)
                .show();


        MoSectionManager.getInstance().subscribe((v) -> {
            // do not update the text views anymore since we are not on the page
            MoStopWatch.universal.setUpdateTextViews(v == MoSectionManager.STOP_WATCH_SECTION);
        });

    }

    public void updateRecyclerView() {
        activity.runOnUiThread(() -> {
            adapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(MoStopWatch.universal.getLapsCount() - 1);
        });
        toggleRecyclerView();
    }

    private void toggleRecyclerView() {
        if (adapter.getItemCount() > 0) {
            cardRecyclerView.setVisibility(View.VISIBLE);
            headerLaps.setVisibility(View.VISIBLE);
        } else {
            cardRecyclerView.setVisibility(View.GONE);
            headerLaps.setVisibility(View.GONE);
        }
    }

    public void update() {
        this.stop.setBackgroundColor(MoStopWatch.universal.getStopColor());
        updateRecyclerView();
    }
}