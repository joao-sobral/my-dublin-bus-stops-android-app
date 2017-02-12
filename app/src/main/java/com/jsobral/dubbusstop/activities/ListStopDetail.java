package com.jsobral.dubbusstop.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.jsobral.dubbusstop.R;
import com.jsobral.dubbusstop.adapters.StopDetailListAdapter;
import com.jsobral.dubbusstop.model.BusStop;
import com.jsobral.dubbusstop.model.SingleResult;

import java.util.ArrayList;

/**
 * Activity that lists all the data for a particular stop.
 * The main activity only shows data for a max of 4 results per stop.
 */
public class ListStopDetail extends AppCompatActivity {

    private Intent intent;
    private ListView list;
    private ArrayList<SingleResult> results;
    private StopDetailListAdapter mAdapter;

    private TextView stopIdTextView;
    private TextView stopAddressTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_stop_detail);
        init();
    }

    /**
     * Initializes views and adapters displaying data to list
     * data passed as a BusStop Serializable Extra from main activity
     */
    private void init(){
        intent = getIntent();
        BusStop busStop = (BusStop)intent.getSerializableExtra("stop");

        stopIdTextView = (TextView) findViewById(R.id.text_detail_stop_id);
        stopIdTextView.setText(String.valueOf(busStop.getStopId()));

        stopAddressTextView=(TextView)findViewById(R.id.text_detail_stop_address);
        stopAddressTextView.setText(busStop.getStopAddress());

        results = busStop.getResults();
        list = (ListView)findViewById(R.id.list_stop_detail);
        mAdapter = new StopDetailListAdapter(this,results);
        list.setAdapter(mAdapter);

    }

}
