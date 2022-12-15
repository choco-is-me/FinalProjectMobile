package com.moh.alarmclock.Interactable.Delete;



import com.moh.alarmclock.Interactable.Selectable.SelectableList;

public interface ListDeletable extends SelectableList {

    /**
     * when we want to set a mo delete for that adapter
     * @param d
     */
    void setMoDelete(Delete d);


    /**
     * notifies the data set changed
     * either used when we are transitioning to
     * delete mode or out of it
     */
    void notifySituationChanged();

    /**
     * traverses through the list
     * and deletes the selected elements appropriately
     */
    void deleteSelected();

    /**
     *
     * @returns the size of list
     */
    int size();

}
