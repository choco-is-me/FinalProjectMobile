package com.moh.alarmclock.Clock.MoStopWatch;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moh.alarmclock.Date.MoTimeUtils;
import com.moh.alarmclock.R;
import com.moh.alarmclock.UI.MoTextView;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInflatorView.MoInflaterView;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerAdapters.MoRecyclerAdapter;

import java.util.List;

public class MoStopWatchRecyclerAdapter extends MoRecyclerAdapter<MoStopWatchRecyclerAdapter.MoStopWatchViewHolder, MoLap> {


    public MoStopWatchRecyclerAdapter(Context c, List<MoLap> dataSet) {
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

        TextView indexTv;
        TextView timeLap;
        TextView diffTimeLap;


        public MoStopWatchViewHolder(@NonNull View itemView) {
            super(itemView);
            indexTv = itemView.findViewById(R.id.index_lap);
            timeLap = itemView.findViewById(R.id.time_lap);
            diffTimeLap = itemView.findViewById(R.id.diff_time_lap);
        }

        public void bind(MoLap lap, int index) {
            indexTv.setText(index < 10 ? "0" + index : index + "");
            diffTimeLap.setText(MoTimeUtils.convertToStopWatchFormat(lap.getTimeInMilli()));
            timeLap.setText(MoTimeUtils.convertToStopWatchFormat(lap.getLastDiff()));

            int status =  MoStopWatch.universal.getMoLapManager().getStatus(lap);
            Context c = itemView.getContext();
            switch (status){
                case MoLapManager.HIGHEST_STATUS:
                    MoTextView.syncColor(c.getColor(R.color.error_color),indexTv,timeLap,diffTimeLap);
                    break;
                case MoLapManager.LOWEST_STATUS:
                    MoTextView.syncColor(c.getColor(R.color.resumeButton),indexTv,timeLap,diffTimeLap);
                    break;
                case MoLapManager.NONE_STATUS:
                    MoTextView.syncColor(c.getColor(R.color.color_text_disabled),indexTv,timeLap,diffTimeLap);
                    break;
            }
        }
    }
}
