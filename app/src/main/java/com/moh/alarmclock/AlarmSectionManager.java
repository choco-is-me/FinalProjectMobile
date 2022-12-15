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

import com.moh.alarmclock.Clock.MoAlarmClock;
import com.moh.alarmclock.Clock.MoAlarmClockManager;
import com.moh.alarmclock.Clock.MoAlarmClockRecyclerAdapter;
import com.moh.alarmclock.Clock.MoClockSugestions.MoClockSuggestionManager;
import com.moh.alarmclock.Clock.MoClockSugestions.MoPrioritySuggestion;
import com.moh.alarmclock.Clock.MoEmptyAlarmException;
import com.moofficial.moessentials.MoEssentials.MoUI.MoFragment.MoOnBackPressed;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSelectable.MoSelectable;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSelectable.MoSelectableInterface.MoOnCanceledListener;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerUtils;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerView;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoBars.MoToolBar;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoNormal.MoCardRecyclerView;

public class AlarmSectionManager implements MoAlarmClockRecyclerAdapter.MoOnActiveClockChanged, MoOnCanceledListener, MoOnBackPressed, MainActivity.SelectModeInterface {
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
    private MoAlarmClockRecyclerAdapter recyclerAdapter;
    private View emptyView;
    private MoToolBar toolBar;
    private MoSelectable<MoAlarmClock> selectable;
    private Iterable<MoPrioritySuggestion> suggestions;
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
        MoAlarmClockManager.refreshScreen = () -> recyclerAdapter.notifyEmptyState().notifyDataSetChanged();
    }

    private void initViews() {
        MoAlarmClockManager.getInstance().load("", activity);
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
        this.recyclerAdapter = new MoAlarmClockRecyclerAdapter(activity, MoAlarmClockManager.getInstance().getAlarms(), this);
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
        MoAlarmClockManager.getInstance().removeSelectedAlarms(this.activity);
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
                suggestions = MoClockSuggestionManager.getSuggestions(activity);
            }
        }.start();
    }

    /**
     * launches the alarm creator activity
     * to let the user make an alarm
     */
    private void launchAlarmCreate() {
        setEnabled(false);
        Intent intent = new Intent(activity, CreateAlarmActivity.class);
        activity.startActivityForResult(intent, CREATE_ALARM_CODE);
    }

    /**
     * enables or disables the buttons
     * so that the user can not open an activity twice
     *
     * @param b
     */
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


    /**
     * showing smart suggestion when user is trying to make a new alarm
     * this way if our intelligent system understands that they want to set an alarm for
     * a specific time, we just make it much easier for them to do
     * this option can be disabled from the settings
     * by using the shared preferences
     *
     * @param anchor the pop up menu is anchored to this view
     */
    private void showSuggestionPopUp(View anchor) {
        // check for preferences set in the setting
        // to see if they even want a suggestion
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
            for (MoPrioritySuggestion ps : suggestions) {
                final MoPrioritySuggestion suggestion = ps;
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


    // user pressed setting menu item
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
            this.subTitle.setText(NEXT_ALARM + MoAlarmClockManager.getInstance().getNextAlarm().getReadableDifference());
        } catch (MoEmptyAlarmException e) {
            this.subTitle.setText(MoAlarmClockManager.getInstance().isEmpty() ? "" : ALL_ALARMS_OFF);
        }
    }

    @Override
    public void onActiveStatusChanged(MoAlarmClock clock) {
        // update the subTitle when a clock changes its status
        setSubtitle();
    }

    @Override
    public void onCardClicked(MoAlarmClock clock) {
        // set the clock to be edited
        CreateAlarmActivity.clock = clock;
        CreateAlarmActivity.startActivityForResult(this.activity, CREATE_ALARM_CODE);
    }

    // called when selectable is canceled or ended
    @Override
    public void onCanceled() {
        updateTitleSubTitle();
        recyclerAdapter.notifyItemRangeChanged(0, recyclerAdapter.getItemCount(), MoAlarmClockRecyclerAdapter.UPDATE_ENABLED_VIEW);
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