package com.example.win8.nokia.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.win8.nokia.GooglePlacesAutocompleteAdapter;
import com.example.win8.nokia.R;
import com.example.win8.nokia.SavePlace;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;


/**
 * Created by win8 on 9/4/2018.
 */

public class PlaceSelector extends AppCompatActivity {
    private GooglePlacesAutocompleteAdapter dataAdapter;
    private ListView listView, list_places;
    private EditText destination;
    private ArrayList<String> keys;
    private ArrayList<SavePlace> name = new ArrayList<SavePlace>();
    private GeoDataClient mGeoDataClient;
    private SharedPreferences log_id;
    static Activity place = null;
    static TextView place_network_status = null;
    TextView outofbound;
    Intent check;



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();

    }

    public void back(View view) {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_selector2);
        place = this;

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        destination = (EditText) findViewById(R.id.destination);
        check = getIntent();
        if (check.hasExtra("type")) {
            if (check.getStringExtra("type").equals("pickup")) {
                destination.setHint("Pick me from ");
            } else {
                destination.setHint("Drop me at");
            }
        }

        outofbound = (TextView) findViewById(R.id.outofbound);
        place_network_status = (TextView) findViewById(R.id.network_status);
        mGeoDataClient = Places.getGeoDataClient(this, null);


        dataAdapter = new GooglePlacesAutocompleteAdapter(PlaceSelector.this, R.layout.list_text_view);

        listView = (ListView) findViewById(R.id.list);
        list_places = (ListView) findViewById(R.id.list_places);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);
        listView.setTextFilterEnabled(true);
        name.clear();
        list_places.setAdapter(new CustomAdapter());
        destination.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                list_places.setVisibility(View.GONE);
                outofbound.setVisibility(View.GONE);
                Handler handle = new Handler();
                handle.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dataAdapter.getFilter().filter(s.toString());
                        keys = dataAdapter.getkeys();
                    }
                }, 2000);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mGeoDataClient.getPlaceById(keys.get(position)).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                        if (task.isSuccessful()) {
                            PlaceBufferResponse places = task.getResult();
                            Place myPlace = places.get(0);
                            destination.setText(myPlace.getAddress());
                            Intent intent = new Intent();
                            intent.putExtra("place", myPlace.getAddress());
                            intent.putExtra("lat", myPlace.getLatLng().latitude);
                            intent.putExtra("lng", myPlace.getLatLng().longitude);
                            intent.putExtra("case", "1");
                            setResult(RESULT_OK, intent);
                            finish();
                            places.release();
                        } else {
                            //log.e(TAG,"Places not found");

                        }
                    }
                });
            }
        });

        list_places.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("place", name.get(position).getPlace());
                intent.putExtra("lat", Double.parseDouble(name.get(position).getLat()));
                intent.putExtra("lng", Double.parseDouble(name.get(position).getLng()));
                intent.putExtra("case", "1");
                setResult(RESULT_OK, intent);
                finish();

            }
        });
    }


    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return name.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }


        @Override
        public View getView(int position, View view, ViewGroup parent) {
            view = getLayoutInflater().inflate(R.layout.place_text_view, null);

            TextView txt = (TextView) view.findViewById(R.id.name);

            txt.setText(name.get(position).getName());


            return view;
        }
    }

    protected void onDestroy(){
        super.onDestroy();
    }
}




