package com.jsobral.dubbusstop.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.jsobral.dubbusstop.R;
import com.jsobral.dubbusstop.Util;
import com.jsobral.dubbusstop.adapters.MainListAdapter;
import com.jsobral.dubbusstop.dao.HTMLParser;
import com.jsobral.dubbusstop.dao.RESTJSONClient;
import com.jsobral.dubbusstop.dao.RESTXMLClient;
import com.jsobral.dubbusstop.model.BusStop;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Main Activity that displays Real Time Information (RTI) for all the stops saved in the app preferences
 * Displays a max of 4 results per stop. Implements a swipe refresh listener and action mode deletion of items
 * As the activity is started one of the 3 parsers will fetch the RTI.
 */
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,SwipeRefreshLayout.OnRefreshListener {

    private Intent intent;
    private Context context;
    private SharedPreferences savedStops;
    private ListView list;
    private ArrayList<BusStop> busStops;
    private MainListAdapter mAdapter;
    private String list_item="";
    private Object mActionMode;
    private Iterator itr;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        init();

        //check if there is a network connection to fetch the RTI
        if(isNetworkAvailable())
            new ListBusStops().execute();
        else
            Toast.makeText(context,"No Internet Connection",Toast.LENGTH_LONG).show();

        //button that starts the activity to add a new bus stop
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),AddBusStop.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Initialize views, widgets, adapter and variables
     */
    private void init(){

        intent = getIntent();
        context = getApplicationContext();
        savedStops = context.getSharedPreferences("stops", Context.MODE_PRIVATE);

        //setup the swipe refresh listener layout
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeView);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setDistanceToTriggerSync(40);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));

        busStops = new ArrayList<>();

        //setup the list view with adapter
        list = (ListView)findViewById(R.id.list);
        mAdapter = new MainListAdapter(this,busStops);
        list.setAdapter(mAdapter);

        //setup list click listener
        list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
            {
                BusStop busStop = busStops.get((int)arg3);

                Intent intent = new Intent(getApplicationContext(),ListStopDetail.class);
                intent.putExtra("stop",busStop);
                startActivity(intent);
            }
        });

        //setup list long click listener
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,int position, long arg3){
                list_item = String.valueOf(busStops.get(position).getStopId());
                if(mActionMode!=null)
                    return false;

                mActionMode = MainActivity.this.startActionMode(mActionModeCallBack);
                return true;
            }
        });
    }

    /**
     * setup the ActionMode Call Back to be able to delete items on long press of
     * the list item, inflates a menu item to the bar and displays stop name
     */
    private ActionMode.Callback mActionModeCallBack = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.setTitle(list_item);
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_delete,menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        //setup action to click of delete icon in menu
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch(item.getItemId()){
                case R.id.delete_id:
                    //remove the bus stop from the adapter so it isnt displayed anymore
                    for(BusStop busStop:busStops) {
                        if (list_item.equals(busStop.getStopId())) {
                            mAdapter.remove(busStop);
                            mAdapter.notifyDataSetChanged();
                        }
                    }

                    //remove the stop number from the saved stops so it isnt fetched anymore
                    ArrayList<String> stops = Util.getStops(savedStops.getString("stops",""));
                    itr = stops.iterator();
                    String strElement = "";
                    while(itr.hasNext()){
                        strElement= (String)itr.next();
                        if(strElement.equals(list_item))
                            itr.remove();
                    }

                    //save new stops list back to the preferences file
                    savedStops.edit().putString(
                            "stops",
                            Util.getStopsString(stops)
                    ).apply();

                    mode.finish();

                    return true;

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
        }
    };

    /**
     * Method used to check if there is a network connection
     * @return boolean if network available or not
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * List click listener, when a stop is selected a new activity is launched that lists all
     * data for that bus stop passing the BusStop as a Serializable extra
     * @param adapterView
     * @param view
     * @param i
     * @param l
     */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        BusStop busStop = busStops.get((int)l);
        Intent intent = new Intent(getApplicationContext(),ListStopDetail.class);
        intent.putExtra("stop",busStop);
        startActivity(intent);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onRefresh() {
        new ListBusStops().execute();
        swipeRefreshLayout.setRefreshing(false);
    }

    /**
     * AsyncTask to access the DAOs in a different Thread and get the data back to the UI thread
     */
    class ListBusStops extends AsyncTask<Void,BusStop,Void> {

        private ProgressDialog dialog = new ProgressDialog(MainActivity.this);
        private MainListAdapter adapter;

        @Override
        protected void onPreExecute() {
            //adapter to activity adapter
            busStops = new ArrayList<>();
            adapter=(MainListAdapter)list.getAdapter();
            adapter.clear();

            //setup and start the progress spinner
            this.dialog.setMessage("Fetching RTI");
            this.dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            this.dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            //System.out.println("In: doInBackGRound");
            //RESTJSONClient dao1 = new RESTJSONClient();
            //RESTXMLClient dao2 = new RESTXMLClient();
            HTMLParser dao3 = new HTMLParser();

            ArrayList<String> stopNumbers=null;

            //get stop numbers from saved preferences if they exist
            if(savedStops.contains("stops"))
                stopNumbers = Util.getStops(savedStops.getString("stops", ""));

            //if there were bus stops fetch them with DAO
            if(stopNumbers != null && stopNumbers.size()>0) {
                for(String stop:stopNumbers){
                    busStops.add(dao3.getBusStopsRTI(stop));
                }
            }

            //publish stops as they are processed
            for (BusStop busStop:busStops){
                publishProgress(busStop);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(BusStop... values) {
            adapter.add(values[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //kill the progress spinner when all data received
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }
}
