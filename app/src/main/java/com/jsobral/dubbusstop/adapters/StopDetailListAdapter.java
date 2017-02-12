package com.jsobral.dubbusstop.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.jsobral.dubbusstop.R;
import com.jsobral.dubbusstop.model.SingleResult;
import java.util.ArrayList;

/**
 * Created by joao on 07/02/17.
 */

public class StopDetailListAdapter extends ArrayAdapter<SingleResult> {

    private Context context;
    private ArrayList<SingleResult> results = new ArrayList<>();

    public StopDetailListAdapter(Context context, ArrayList<SingleResult> results) {
        super(context, R.layout.result, results);
        this.context = context;
        this.results = results;

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View busStopView = null;

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        busStopView = inflater.inflate(R.layout.result, parent, false);

        TextView routeTextView = (TextView)busStopView.findViewById(R.id.text_result_route);
        routeTextView.setText(String.valueOf(results.get(position).getRoute()));

        TextView dueTimeTextView = (TextView)busStopView.findViewById(R.id.text_result_duetime);
        dueTimeTextView.setText(String.valueOf(results.get(position).getDueTime()));

        return busStopView;
    }

}
