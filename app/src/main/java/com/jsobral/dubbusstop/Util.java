package com.jsobral.dubbusstop;

import java.util.ArrayList;

/**
 * provides util method to the app, parsers for the saved stops data
 * Created by joao on 07/02/17.
 */
public class Util {

    /**
     * converts the string with stops to an array of stop numbers
     * @param stopsString
     * @return
     */
   public static ArrayList<String> getStops(String stopsString){

        ArrayList<String> stopNumbers = new ArrayList<>();
        String[] stopsArray=null;

        if(!stopsString.equals("") && stopsString!= null){
            stopsArray = stopsString.split(",");

            for(String stop: stopsArray){
                stopNumbers.add(stop);
            }
        }
        return stopNumbers;
    }

    /**
     * converts an array of stop numbers to a string that can be saved to the shared preferences
     * @param stops
     * @return
     */
    public static String getStopsString(ArrayList<String> stops){
        StringBuilder stringBuilder = new StringBuilder();
        for(String inte:stops){
            stringBuilder.append(inte).append(",");
        }
        return stringBuilder.toString();
    }
}
