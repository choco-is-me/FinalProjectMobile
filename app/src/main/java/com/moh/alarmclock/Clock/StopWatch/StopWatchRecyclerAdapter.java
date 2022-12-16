package com.moh.alarmclock.Clock.StopWatch;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moh.alarmclock.Date.TimeUtils;
import com.moh.alarmclock.R;
import com.moh.alarmclock.UI.TextView;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInflatorView.MoInflaterView;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerAdapters.MoRecyclerAdapter;

import java.util.List;

public class StopWatchRecyclerAdapter extends MoRecyclerAdapter<StopWatchRecyclerAdapter.MoStopWatchViewHolder, Lap> {


    public StopWatchRecyclerAdapter(Context c, List<Lap> dataSet) {
        super(c, dataSet);
    }

    @NonNull
    @Override
    public MoStopWatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MoStopWatchViewHolder(MoInflaterView.inflate(R.layout.lap_item, parent, context));
    }

    @Override
    public void onBindViewHolder(@NonNull MoStopWatchViewHolder holder, int position) {
        holder.bind(dataSet.get(position), position + 1);
    }

    public static class MoStopWatchViewHolder extends RecyclerView.ViewHolder {

        android.widget.TextView indexTv;
        android.widget.TextView timeLap;
        android.widget.TextView diffTimeLap;


        public MoStopWatchViewHolder(@NonNull View itemView) {
            super(itemView);
            indexTv = itemView.findViewById(R.id.index_lap);
            timeLap = itemView.findViewById(R.id.time_lap);
            diffTimeLap = itemView.findViewById(R.id.diff_time_lap);
        }

        public void bind(Lap lap, int index) {
            indexTv.setText(index < 10 ? "0" + index : index + "");
            diffTimeLap.setText(TimeUtils.convertToStopWatchFormat(lap.getTimeInMilli()));
            timeLap.setText(TimeUtils.convertToStopWatchFormat(lap.getLastDiff()));

            int status =  StopWatch.universal.getMoLapManager().getStatus(lap);
            Context c = itemView.getContext();
            switch (status){
                case LapManager.HIGHEST_STATUS:
                    TextView.syncColor(c.getColor(R.color.error_color),indexTv,timeLap,diffTimeLap);
                    break;
                case LapManager.LOWEST_STATUS:
                    TextView.syncColor(c.getColor(R.color.colorPrimary),indexTv,timeLap,diffTimeLap);
                    break;
                case LapManager.NONE_STATUS:
                    TextView.syncColor(c.getColor(R.color.color_text_disabled),indexTv,timeLap,diffTimeLap);
                    break;
            }
        }
    }
}
