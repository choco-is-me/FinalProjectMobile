package com.moh.alarmclock.Interactable.Selectable;

public class SelectableUtils {

    /**
     * selects all the items of the iterable
     * @param items iterable of selectable items
     */
    public static void selectAllItems(Iterable<? extends SelectableItem> items){
        turnAllItems(true,items);
    }


    /**
     * deselects all the items of the iterable
     * @param items iterable of selectable items
     */
    public static void deselectAllItems(Iterable<? extends SelectableItem> items){
        turnAllItems(false,items);
    }

    /**
     * changes the selected state to the boolean given
     * @param b
     * @param items
     */
    private static void turnAllItems(boolean b,Iterable<? extends SelectableItem> items){
        for(SelectableItem s : items){
            s.setSelected(b);
        }
    }




}
