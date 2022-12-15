package com.moh.alarmclock.List;

import java.util.Arrays;

public class List {


    /**
     *
     * @param list
     * is an string in a format of when you call
     * List.toString e.g [2,23,4]
     * @param data
     * @return
     */
    public static java.util.List<String> get(java.util.List<String> list, String data){
        String nb = data.substring(1,data.length()-1);
        String[] objects = nb.split(",");
        list.addAll(Arrays.asList(objects));
        return list;
    }



    public static java.util.List<Integer> get(java.util.List<Integer> integers, java.util.List<String> strings){
        for(String s: strings){
            if(!s.isEmpty()){
                integers.add(Integer.parseInt(s.trim()));
            }
        }
        return integers;
    }






}
