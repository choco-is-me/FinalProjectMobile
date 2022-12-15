package com.moh.alarmclock.Clock.Timer.TimerPresetPackage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.moh.alarmclock.R;
import com.moh.alarmclock.Runnable.Runnable;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInflatorView.MoInflaterView;

import java.util.ArrayList;

public class PresetRecyclerAdapter extends RecyclerView.Adapter<PresetRecyclerAdapter.TabViewHolder> {


    private ArrayList<TimerPreset> presets;
    private Context context;
    private Runnable runnable;
    private java.lang.Runnable deleteModeRunnable;
    private Runnable onSelectedItemsChanged;

    public static class TabViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView time;
        CardView cardView;

        LinearLayout linearLayout;


        public TabViewHolder(View v) {
            super(v);

            title = v.findViewById(R.id.timer_preset_text);
            time = v.findViewById(R.id.timer_preset_time);
            cardView = v.findViewById(R.id.preset_card_view);
            linearLayout = v.findViewById(R.id.preset_linear_layout);
            //tab = v.findViewById(R.id.tab_view_holder);
        }

        public void removeView(){
            ((ViewGroup) itemView).removeAllViews();
        }


    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public PresetRecyclerAdapter(ArrayList<TimerPreset> myDataset,
                                 Context c, Runnable change,
                                 java.lang.Runnable deleteModeRunnable, Runnable onSelectedItemsChanged) {
        this.presets = myDataset;
        this.context = c;
        this.runnable = change;
        this.deleteModeRunnable = deleteModeRunnable;
        this.onSelectedItemsChanged = onSelectedItemsChanged;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public TabViewHolder onCreateViewHolder(ViewGroup parent,
                                            int viewType) {
        View v = MoInflaterView.inflate(R.layout.timer_preset_layout, parent, parent.getContext());
        return new TabViewHolder(v);
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(TabViewHolder holder, int position) {
        holder.title.setText(presets.get(position).getName());
        holder.time.setText(presets.get(position).getReadableTime());
        holder.cardView.setOnClickListener(view -> onClickCardView(position));

        holder.cardView.setOnLongClickListener(view -> {
            if(!TimerPreset.isInDeleteMode){
                deleteModeRunnable.run();
                onClickCardView(position);
                notifyDataSetChanged();
            }
            return true;
        });

        Drawable drawable;
        if(presets.get(position).isSelected()){
            // red background
            drawable = context.getDrawable(R.drawable.glow_edge_red);
        }else{
            // normal background
            drawable = context.getDrawable(R.drawable.glow_edge);
        }
        holder.linearLayout.setBackground(drawable);
    }


    private void onClickCardView(int position){
        if(TimerPreset.isInDeleteMode){
            // select this preset
            presets.get(position).click();
            notifyItemChanged(position);
            onSelectedItemsChanged.run(TimerPresetManager.getSelectedSize());
        }else{
            // load the timer with preset value
            runnable.run(presets.get(position).getMilliseconds());
        }
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return presets.size();
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }





}
