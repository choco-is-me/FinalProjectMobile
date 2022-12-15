package com.moh.alarmclock.Interactable.Selectable;

import java.util.ArrayList;
import java.util.List;

public interface SelectableList {

    /**
     * an array list to store the selected items in
     */
    List<SelectableItem> selectedItems = new ArrayList<>();


    /**
     * if we want all the elements to be selected
     */
    void selectAllElements();

    /**
     * if we want to deselect all the elements
     */
    void deselectAllElements();


    /**
     * when the user clicks on an element to
     * be selected
     */
    void onSelect(int position);



}
