package com.jsobral.dubbusstop.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.threetenabp.AndroidThreeTen;
import com.jsobral.dubbusstop.R;
import com.jsobral.dubbusstop.model.BusStop;
import com.jsobral.dubbusstop.model.SingleResult;
import com.jsobral.dubbusstop.myviews.CircularProgressBar;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.temporal.ChronoUnit;

import java.util.ArrayList;

/**
 * Created by software on 31/01/17.
 */

public class MainListAdapter extends ArrayAdapter<BusStop> {

    private Context context;
    private ArrayList<BusStop> busStopList = new ArrayList<>();
    private LocalDateTime now;
    private LocalDateTime dueTime;

    /**
     * Constructor
     * @param context the application context
     * @param busStops the list of busStops to apply the adapter
     */
    public MainListAdapter(Context context, ArrayList<BusStop> busStops) {
        super(context, R.layout.stops_list_item, busStops);
        this.context = context;
        this.busStopList = busStops;

        AndroidThreeTen.init(context);
        now = LocalDateTime.now();
        dueTime = LocalDateTime.now();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View busStopView = null;

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        busStopView = inflater.inflate(R.layout.stops_list_item, parent, false);

        LinearLayout busStopLinearLayout = (LinearLayout) busStopView.findViewById(R.id.busStopLinearLayout);

        TextView stopNumberText = (TextView)busStopView.findViewById(R.id.textview_bus_stop_id);
        stopNumberText.setText(String.valueOf(busStopList.get(position).getStopId()));

        TextView stopAddressText = (TextView)busStopView.findViewById(R.id.textview_bus_stop_address);
        String stopAddress = busStopList.get(position).getStopAddress();
        if(stopAddress != null && stopAddress.length()>35)
            stopAddress = stopAddress.substring(0,35)+"...";
        stopAddressText.setText(stopAddress);

        int count =0;
        for(SingleResult result:busStopList.get(position).getResults()){
            count++;
            if (count>4)
                break;

            int dueTimeMin=0;

            //adjust due time from 100 to 60, 15 minutes will be converted to 25, a quarter of circle
            int adjustedclockDueTime =0;

            if(!result.getDueTime().startsWith("Due")) {
                int hour = Integer.parseInt(result.getDueTime().substring(0, 2));
                int min = Integer.parseInt(result.getDueTime().substring(3, 5));
                dueTime = dueTime.withHour(hour);
                dueTime = dueTime.withMinute(min);

                dueTimeMin = (int) now.until(dueTime, ChronoUnit.MINUTES);
                adjustedclockDueTime = (100 * dueTimeMin)/60;

            }

            CircularProgressBar circularProgressBar = (CircularProgressBar)inflater.inflate(R.layout.circular_progress, null);

            int animationDuration = 2000; // 2500ms = 2,5s
            circularProgressBar.setProgressWithAnimation(adjustedclockDueTime,animationDuration);

            circularProgressBar.setTitle(result.getRoute());
            circularProgressBar.setSubTitle(result.getDueTime());

            //background circle color
            circularProgressBar.setBackgroundColor(Color.rgb(112,112,112));

            //foreground ie progress circle color
            circularProgressBar.setColor(getProgressColor(dueTimeMin));

            busStopLinearLayout.addView(circularProgressBar);
        }

        return busStopView;
    }

    private int getProgressColor(int duetime){
        if(duetime<6) return Color.RED;
        else if(duetime<11) return Color.YELLOW;
        else return Color.GREEN;
    }
}
