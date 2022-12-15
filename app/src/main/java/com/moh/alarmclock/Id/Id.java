package com.moh.alarmclock.Id;

import android.content.Context;

import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoLoadable;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoSavable;

import java.util.Random;

public class Id implements MoSavable, MoLoadable {


    private int id;

    public Id(){
        this.id = getRandomId();
    }

    public Id(int id){
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getSId(){
        return id+"";
    }

    public void setId(int id) {
        this.id = id;
    }

    public static int getRandomId()
    {
        return randInt(12,1003122340);
    }

    public static int getRandomInt() {
        return (int)(Math.random() * 100000000);
    }


    public static int randInt(int min, int max) {

        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }

    @Override
    public void load(String data, Context context) {
        this.id = Integer.parseInt(data);
    }

    @Override
    public String getData() {
        return this.id+"";
    }
}
