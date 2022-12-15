package com.moh.alarmclock;

import android.content.Intent;
import android.net.Uri;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.moh.alarmclock.Clock.AlarmClock;
import com.moh.alarmclock.Clock.AlarmClockManager;
import com.moh.alarmclock.Clock.AlarmClockRecyclerAdapter;
import com.moh.alarmclock.Clock.ClockSugestions.ClockSuggestionManager;
import com.moh.alarmclock.Clock.ClockSugestions.PrioritySuggestion;
import com.moh.alarmclock.Clock.EmptyAlarmException;
import com.moofficial.moessentials.MoEssentials.MoUI.MoFragment.MoOnBackPressed;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSelectable.MoSelectable;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSelectable.MoSelectableInterface.MoOnCanceledListener;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerUtils;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerView;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoBars.MoToolBar;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoNormal.MoCardRecyclerView;

public class AlarmSectionManager implements AlarmClockRecyclerAdapter.MoOnActiveClockChanged, MoOnCanceledListener, MoOnBackPressed, MainActivity.SelectModeInterface {
    /**
     * alarm section
     */
    public static final int CREATE_ALARM_CODE = 1;
    public static final String ALL_ALARMS_OFF = "All alarms are off";
    public static final String NEXT_ALARM = "Next alarm: ";
    public static final String MOH_YAGHOUB = "By Moh Yaghoub";

    MainActivity activity;
    View root;
    private ConstraintLayout rootConstraint;
    private TextView title, subTitle;
    private MoCardRecyclerView cardRecyclerView;
    private MoRecyclerView recyclerView;
    private AlarmClockRecyclerAdapter recyclerAdapter;
    private View emptyView;
    private MoToolBar toolBar;
    private MoSelectable<AlarmClock> selectable;
    private Iterable<PrioritySuggestion> suggestions;
    private boolean showSuggestions;


    public AlarmSectionManager(MainActivity a) {
        this.activity = a;
        this.root = a.findViewById(R.id.layout_alarmClock);
    }

    void initAlarmSection() {
        initViews();
        initRefreshScreen();
    }

    private void initRefreshScreen() {
        AlarmClockManager.refreshScreen = () -> recyclerAdapter.notifyEmptyState().notifyDataSetChanged();
    }

    private void initViews() {
        AlarmClockManager.getInstance().load("", activity);
        initClockSuggestions();

        this.rootConstraint = root.findViewById(R.id.layout_alarmClocks_rootConstraint);
        this.title = root.findViewById(R.id.mo_lib_title);
        this.subTitle = root.findViewById(R.id.mo_lib_subtitle);

        this.toolBar = root.findViewById(R.id.toolbar_alarmClocks_main);
        this.toolBar
                .hideTitle()
                .hideLeft()
                .setMiddleIcon(R.drawable.ic_baseline_add)
                .setMiddleOnClickListener(this::showSuggestionPopUp)
                .setRightOnClickListener(this::showMenu)
                .setExtraIcon(R.drawable.ic_baseline_delete_outline_24)
                .setExtraOnClickListener((v) -> onDeleteClicked())
                .hideExtraButton();

        this.emptyView = root.findViewById(R.id.layout_alarmClocks_emptyView);
        this.emptyView.findViewById(R.id.button_emptyAlarms_addAlarm).setOnClickListener(this::showSuggestionPopUp);

        this.cardRecyclerView = root.findViewById(R.id.card_alarmClocks_recycler);
        this.cardRecyclerView.getCardView().makeTransparent();
        this.recyclerAdapter = new AlarmClockRecyclerAdapter(activity, AlarmClockManager.getInstance().getAlarms(), this);
        this.recyclerAdapter.setEmptyView(this.emptyView).setRecyclerView(cardRecyclerView.getRecyclerView()).setEmptyViewCallback((isEmpty) -> {
            if (isEmpty) {
                this.toolBar.hideMiddle();
            } else {
                this.toolBar.showMiddle();
            }
        });
        this.recyclerView = MoRecyclerUtils.get(cardRecyclerView.getRecyclerView(), recyclerAdapter)
                .setLayoutManagerType(MoRecyclerView.STAGGERED_GRID_LAYOUT_MANAGER)
                .setDynamicallyCalculateSpanCount(true)
                .show();
        this.recyclerView.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        this.recyclerAdapter.notifyEmptyState();
        setSubtitle();

        selectable = new MoSelectable<>(activity, (ViewGroup) root, recyclerAdapter);
        this.selectable.setCounterView(this.title)
                .setSelectAllCheckBox(this.toolBar.getCheckBox())
                .addNormalViews(toolBar.getMiddleButton(), toolBar.getRightButton(), activity.getBottomNavigation(), subTitle)
                .addUnNormalViews(toolBar.getCheckBox(), toolBar.getExtraButton())
                .setOnEmptySelectionListener(() -> selectable.removeAction())
                .setOnCanceledListener(this)
                .setTransitionIn(new TransitionSet().addTransition(new ChangeBounds()).addTransition(new Fade()))
                .setTransitionOut(new TransitionSet().addTransition(new ChangeBounds()).addTransition(new Fade()));

    }

    private void onDeleteClicked() {
        AlarmClockManager.getInstance().removeSelectedAlarms(this.activity);
        selectable.removeAction();
        recyclerAdapter.notifyEmptyState().notifyDataSetChanged();
    }

    private void updateTitleSubTitle() {
        this.title.setText(R.string.app_name);
        setSubtitle();
    }

    private void initClockSuggestions() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                suggestions = ClockSuggestionManager.getSuggestions(activity);
            }
        }.start();
    }

    private void launchAlarmCreate() {
        setEnabled(false);
        Intent intent = new Intent(activity, CreateAlarmActivity.class);
        activity.startActivityForResult(intent, CREATE_ALARM_CODE);
    }

    private void setEnabled(boolean b) {
    }

    public boolean showMenu(View anchor) {
        PopupMenu popup = new PopupMenu(activity, anchor);
        popup.getMenuInflater().inflate(R.menu.pop_up_settings, popup.getMenu());
        popup.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.setting_menu_item:
                    onSettingPressed();
                    break;
                case R.id.about_menu_item:
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://mohyaghoub.wixsite.com/profile"));
                    activity.startActivity(browserIntent);
                    break;
            }
            return false;
        });
        popup.show();
        return true;
    }

    private void showSuggestionPopUp(View anchor) {
        if (!showSuggestions || !suggestions.iterator().hasNext()) {
            launchAlarmCreate();
        } else {
            PopupMenu popup = new PopupMenu(activity, anchor);
            popup.getMenu().add("New Alarm");
            popup.getMenu().getItem(0).setOnMenuItemClickListener(menuItem -> {
                launchAlarmCreate();
                return false;
            });
            int i = 1;
            for (PrioritySuggestion ps : suggestions) {
                final PrioritySuggestion suggestion = ps;
                popup.getMenu().add(ps.getTime());
                popup.getMenu().getItem(i).setOnMenuItemClickListener(menuItem -> {
                    suggestion.createAlarm(activity);
                    TransitionManager.beginDelayedTransition((ViewGroup) root, new TransitionSet()
                            .addTransition(new ChangeBounds()).addTransition(new Fade()).setDuration(400));
                    updateAll();
                    return false;
                });
                i++;
            }
            popup.show();
        }
    }

    private void onSettingPressed() {
        Intent intent = new Intent(activity, SettingsActivity.class);
        activity.startActivity(intent);
    }


    public void onResume() {
        this.setEnabled(true);
    }

    public void updateSubTitle() {
        setSubtitle();
    }

    public void updateAll() {
        updateSubTitle();
        recyclerAdapter.notifyEmptyState().notifyDataSetChanged();
    }

    private void setSubtitle() {
        try {
            this.subTitle.setText(NEXT_ALARM + AlarmClockManager.getInstance().getNextAlarm().getReadableDifference());
        } catch (EmptyAlarmException e) {
            this.subTitle.setText(AlarmClockManager.getInstance().isEmpty() ? "" : ALL_ALARMS_OFF);
        }
    }

    @Override
    public void onActiveStatusChanged(AlarmClock clock) {
        setSubtitle();
    }

    @Override
    public void onCardClicked(AlarmClock clock) {
        CreateAlarmActivity.clock = clock;
        CreateAlarmActivity.startActivityForResult(this.activity, CREATE_ALARM_CODE);
    }

    @Override
    public void onCanceled() {
        updateTitleSubTitle();
        recyclerAdapter.notifyItemRangeChanged(0, recyclerAdapter.getItemCount(), AlarmClockRecyclerAdapter.UPDATE_ENABLED_VIEW);
    }

    @Override
    public boolean onBackPressed() {
        if (selectable.isInActionMode()) {
            selectable.removeAction();
            return true;
        }
        return false;
    }

    @Override
    public boolean isSelecting() {
        return recyclerAdapter.isSelecting();
    }
}