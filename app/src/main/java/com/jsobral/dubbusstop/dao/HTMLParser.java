package com.jsobral.dubbusstop.dao;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import com.jsobral.dubbusstop.model.BusStop;
import com.jsobral.dubbusstop.model.SingleResult;

/**
 * HTML Parser, gets the HTML for the dublin bus page with RealTimeInformation for the bus stop
 * Created by joao on 07/02/17.
 */
public class HTMLParser {
    public BusStop getBusStopsRTI(String stop) {

        BusStop busStop = new BusStop();
        busStop.setStopId(stop);
        final String URL =
                "http://dublinbus.ie/RTPI/Sources-of-Real-Time-Information/" +
                        "?searchtype=view&searchquery=";

        final String ELEMENT_ID = "ctl00_FullRegion_MainRegion_ContentColumns_holder_RealTimeStopInformation1_lblStopAddress";

        try {
            System.out.println("stop: " + stop);
            //get HTML
            Document doc = Jsoup.connect(URL + stop).get();
            Element content = doc.getElementById("rtpi-results");
            Elements rows = content.getElementsByTag("tr");
            Element stopName = doc.getElementById(ELEMENT_ID);
            busStop.setStopAddress(stopName.text());
            //System.out.println("Stop address: " + stopName.text());

            for (int i = 1; i < rows.size(); i++) {
                SingleResult result = new SingleResult();
                Element currentRow = rows.get(i);
                result.setRoute(currentRow.child(0).text());
                result.setDueTime(currentRow.child(2).text());
                busStop.getResults().add(result);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return busStop;

    }
}
