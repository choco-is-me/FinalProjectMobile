package com.moh.alarmclock;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.moh.alarmclock.Clock.MoStopWatch.StopWatch;
import com.moh.alarmclock.Clock.MoStopWatch.StopWatchRecyclerAdapter;
import com.moh.alarmclock.Section.SectionManager;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerUtils;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerView;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoNormal.MoCardRecyclerView;

public class StopWatchManager {

    private TextView minute;
    private TextView second;
    private TextView milliSecond;
    private Button start;
    private Button stop;
    private Button lap;
    private MoCardRecyclerView cardRecyclerView;
    private MoRecyclerView recyclerView;
    private StopWatchRecyclerAdapter adapter;
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
            StopWatch.universal.start();
            this.stop.setText(StopWatch.universal.getStopString());
            this.lap.setText(StopWatch.universal.getLapString());
            this.stop.setBackgroundColor(StopWatch.universal.getStopColor());
        });
        this.stop.setBackgroundColor(activity.getColor(R.color.error_color));
        this.stop.setOnClickListener(view -> {
            StopWatch.universal.stop();
            this.stop.setBackgroundColor(StopWatch.universal.getStopColor());
            this.stop.setText(StopWatch.universal.getStopString());
            this.lap.setText(StopWatch.universal.getLapString());
        });
        this.lap.setOnClickListener(view -> {
            StopWatch.universal.lap();
            updateRecyclerView();
            this.lap.setText(StopWatch.universal.getLapString());
            this.stop.setBackgroundColor(StopWatch.universal.getStopColor());
        });
        StopWatch.universal.setActivity(activity);
        StopWatch.universal.setStopWatchTv(this.minute, this.second, this.milliSecond);
        StopWatch.universal.setButtons(this.start, this.stop, this.lap);
        StopWatch.universal.changeButtonText();



        cardRecyclerView = root.findViewById(R.id.cardRecycler_stopWatch_laps);
        adapter = new StopWatchRecyclerAdapter(this.activity, StopWatch.universal.getLaps());
        recyclerView = MoRecyclerUtils.get(cardRecyclerView.getRecyclerView(), adapter)
                .setReverseLayout(true)
                .show();


        SectionManager.getInstance().subscribe((v) -> {
            // do not update the text views anymore since we are not on the page
            StopWatch.universal.setUpdateTextViews(v == SectionManager.STOP_WATCH_SECTION);
        });

    }

    public void updateRecyclerView() {
        activity.runOnUiThread(() -> {
            adapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(StopWatch.universal.getLapsCount() - 1);
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
        this.stop.setBackgroundColor(StopWatch.universal.getStopColor());
        updateRecyclerView();
    }
}