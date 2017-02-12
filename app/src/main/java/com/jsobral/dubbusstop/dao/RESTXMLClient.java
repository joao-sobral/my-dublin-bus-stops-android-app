package com.jsobral.dubbusstop.dao;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.jsobral.dubbusstop.model.BusStop;
import com.jsobral.dubbusstop.model.SingleResult;

/**
 * Not used as it is getting an SSL error, cert path not trusted
 * Fetch XML data from RTI API
 * Created by joao on 07/02/17.
 */
public class RESTXMLClient {

    public static ArrayList<BusStop> getBusStopsRTI(ArrayList<String> busStopNumbers){

        ArrayList<BusStop> listBusStops = new ArrayList<>();

        for(String stopNumber:busStopNumbers) {
            System.out.println("In:HTTP: BusStop: " + stopNumber);
            BusStop busStop = new BusStop();
            busStop.setStopId(stopNumber);
            ArrayList<SingleResult> results = new ArrayList<>();

            try {
                /*
                URL url = new URL("https://data.dublinked.ie/cgi-bin/rtpi/realtimebusinformation?stopid=7125&operator=bac&routeId=40d&format=xml");
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoInput(true);

                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(inputStream, "iso-8859-1"));
                StringBuilder response = new StringBuilder();
                String line = "";

                while ((line = bufferedReader.readLine()) != null) {
                    response.append(line).append("\n");
                }
                System.out.println("XML Response: "+response.toString());

                bufferedReader.close();
                inputStream.close();
                connection.disconnect();
                */
                DocumentBuilderFactory docFactory =
                        DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder =
                        docFactory.newDocumentBuilder();
                Document doc = docBuilder.parse(
                        new InputSource("https://data.dublinked.ie/cgi-bin/rtpi/realtimebusinformation?stopid=7125&operator=bac&routeId=40d&format=xml")
                        //new InputSource(new ByteArrayInputStream(response.toString().getBytes("utf-8")))
                );

                Node xmlResults = doc.getElementsByTagName("results").item(0);
                NodeList xmlResultsList = xmlResults.getChildNodes();

                for (int i = 0; i < xmlResultsList.getLength(); i++) {
                    Node currentResult = xmlResultsList.item(i);
                    NodeList attrs = currentResult.getChildNodes();
                    SingleResult result = new SingleResult();

                    for (int i2 = 0 ; i2 < attrs.getLength(); i2++){
                        if("duetime".equals(attrs.item(i2).getNodeName())){
                            result.setDueTime(attrs.item(i2).getTextContent());
                        }else if("route".equals(attrs.item(i2).getNodeName())){
                            result.setRoute(attrs.item(i2).getTextContent());
                        }
                    }

                    busStop.getResults().add(result);
                }

            }catch (Exception e) {
                e.printStackTrace();
            }

            listBusStops.add(busStop);
        }

        return listBusStops;
    }
}
