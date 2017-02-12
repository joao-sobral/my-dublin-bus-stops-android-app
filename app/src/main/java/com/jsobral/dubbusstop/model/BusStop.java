package com.jsobral.dubbusstop.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO for BusStop
 * Created by joao on 07/02/17.
 */
public class BusStop implements Serializable{

    private String stopId;
    private String stopAddress;
    private ArrayList<SingleResult> results = new ArrayList();

    public BusStop(){}

    public BusStop(String stopId, String stopAddress, ArrayList<SingleResult> results) {
        this.stopId = stopId;
        this.stopAddress = stopAddress;
        this.results = results;
    }

    public String getStopId() {
        return stopId;
    }

    public void setStopId(String stopId) {
        this.stopId = stopId;
    }

    public ArrayList<SingleResult> getResults() {
        return results;
    }

    public void setResults(ArrayList<SingleResult> results) {
        this.results = results;
    }

    public String getStopAddress() {
        return stopAddress;
    }

    public void setStopAddress(String stopAddress) {
        this.stopAddress = stopAddress;
    }
}
