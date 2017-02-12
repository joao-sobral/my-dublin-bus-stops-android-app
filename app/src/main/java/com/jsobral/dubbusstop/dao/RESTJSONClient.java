package com.jsobral.dubbusstop.dao;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import com.jsobral.dubbusstop.model.BusStop;
import com.jsobral.dubbusstop.model.SingleResult;

/**
 * Created by joao on 07/02/17.
 */
public class RESTJSONClient {

    /**
     * retrieve a list of rt info
     * Not used as it is getting an SSL error, cert path not trusted
     * @param busStopNumbers, the id of the bus stop
     * @return list of promotions
     */
    public ArrayList<BusStop> getBusStopsRTI(ArrayList<String> busStopNumbers){

        ArrayList<BusStop> listBusStops = new ArrayList<>();

        for(String stopNumber:busStopNumbers) {

            BusStop busStop = new BusStop();
            busStop.setStopId(stopNumber);
            ArrayList<SingleResult> results = new ArrayList<>();

            String urlString = "https://data.dublinked.ie/cgi-bin/rtpi/realtimebusinformation?stopid="
                    + stopNumber + "&operator=bac&format=xml";

            try {
                URL url = new URL(urlString);
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

                bufferedReader.close();
                inputStream.close();
                connection.disconnect();
                //System.out.println(response.toString());

                JSONParser parser = new JSONParser();
                System.out.println(response.toString());
                try {
                    Object obj = parser.parse(response.toString());

                    JSONObject jsonObject1 = (JSONObject) obj;
                    //System.out.println(jsonObject1.toString());

                    JSONObject jsonObject;
                    JSONArray jsonArray = (JSONArray) jsonObject1.get("results");

                    for (int i = 0; i < jsonArray.size(); i++) {
                        jsonObject = (JSONObject) jsonArray.get(i);
                        //JSONObject SingleResult = (JSONObject) jsonObject.get("roomType");
                        //System.out.println("json: "+ jsonObject.toString());

                        results.add(new SingleResult(
                                jsonObject.get("route").toString(),
                                jsonObject.get("duetime").toString()
                        ));
                    }

                    busStop.setResults(results);

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                listBusStops.add(busStop);

            } catch (MalformedURLException e) {

            }  catch (IOException e) {
                e.printStackTrace();
            }

        }
        return listBusStops;
    }
}
