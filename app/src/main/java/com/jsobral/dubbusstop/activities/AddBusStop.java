package com.jsobral.dubbusstop.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.jsobral.dubbusstop.R;

/**
 * Activity used to add a bus stop to the list
 */
public class AddBusStop extends AppCompatActivity {

    private SharedPreferences savedStops;
    private EditText input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bus_stop);
        init();
    }

    private void init(){
        input = (EditText)findViewById(R.id.editText_input_stop_number);
        //link to the stops data saved in the phone
        savedStops = getApplicationContext().getSharedPreferences("stops", Context.MODE_PRIVATE);

    }
    public void buttonClick(View v) {
        switch (v.getId()){
            //when the add button is clicked
            case R.id.button_addStop:
                String stops="";
                int stopNumber =0;
                //check there is input
                if(input.getText().toString()!= null && !input.getText().toString().equals(""))
                    stopNumber = Integer.parseInt(input.getText().toString());
                //fetch already saved stops
                if(savedStops.contains("stops"))
                    stops = savedStops.getString("stops",null);
                //attach stop to existing data or just add the stop as a first one
                stops += stopNumber+",";
                //System.out.println(stops);
                savedStops.edit().putString("stops",stops).apply();
                //save all stops information back to phone

                //go back to main activity where data will be refreshed
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                break;
        }
    }
}
