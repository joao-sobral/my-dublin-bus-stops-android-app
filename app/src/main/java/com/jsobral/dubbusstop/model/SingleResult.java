package com.jsobral.dubbusstop.model;

import java.io.Serializable;

/**
 * DTO for a single result for RTI as in what Bus is arriving at what time to the BusStop it belongs to
 * Created by joao on 07/02/17.
 */
public class SingleResult implements Serializable {

    private String route;
    private String dueTime;

    public SingleResult(){}

    public SingleResult(String route, String dueTime) {
        this.route = route;
        this.dueTime = dueTime;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getDueTime() {
        return dueTime;
    }

    public void setDueTime(String dueTime) {
        this.dueTime = dueTime;
    }
}
