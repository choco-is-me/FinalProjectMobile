package com.moh.alarmclock.Preference;

import com.moh.alarmclock.String.String;

public class Preference {

    private static final int LIMIT_COUNT_CHAR_SUMMARY = 100;

    private java.lang.String valueSummary;
    private java.lang.String normalSummary;
    private boolean updateSummary;
    private androidx.preference.Preference preference;


    public Preference(androidx.preference.Preference p){
        this.preference = p;
    }

    public Preference setOnPreferenceClickListener(androidx.preference.Preference.OnPreferenceClickListener listener){
        this.preference.setOnPreferenceClickListener(listener);
        return this;
    }


    public Preference setUpdateSummary(boolean updateSummary) {
        this.updateSummary = updateSummary;
        if(this.updateSummary){
            updateSummary(preference.getSharedPreferences().getString(preference.getKey(),""));
        }
        return this;
    }

    public void updateSummary(Object newValue) {
        if(newValue!= null && !newValue.toString().isEmpty()){
            // there is new value, update the description
            preference.setSummary(String.getLimitedCount(valueSummary !=null? valueSummary :newValue.toString(),LIMIT_COUNT_CHAR_SUMMARY));
        }else{
            preference.setSummary(String.getLimitedCount(normalSummary,LIMIT_COUNT_CHAR_SUMMARY));
        }
    }

    public Preference setNormalSummary(java.lang.String normalSummary) {
        this.normalSummary = normalSummary;
        return this;
    }


    public Preference setValueSummary(java.lang.String valueSummary) {
        this.valueSummary = valueSummary;
        return this;
    }

    /**
     * returns the key of the preference
     * @return
     */
    public java.lang.String getKey(){
        return this.preference.getKey();
    }

}
