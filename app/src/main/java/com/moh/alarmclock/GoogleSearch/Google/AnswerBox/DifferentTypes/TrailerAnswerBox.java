package com.moh.alarmclock.GoogleSearch.Google.AnswerBox.DifferentTypes;


import com.moh.alarmclock.GoogleSearch.Google.AnswerBox.AnswerBox;

public class TrailerAnswerBox extends AnswerBox {

    public static final String LINK_CODE = "<div class=\"twQ0Be\" style=\"height:312px\">";


    String link;

    public TrailerAnswerBox(String data)
    {
        super(data);
        this.initValues();
    }

    private void initValues()
    {
        this.link = super.extractData(LINK_CODE);
    }


    @Override
    public void getResult() {

        //return this.link;
    }
}
