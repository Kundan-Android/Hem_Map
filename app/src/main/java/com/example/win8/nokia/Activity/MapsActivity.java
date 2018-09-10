package com.example.win8.nokia.Activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.win8.nokia.Api_interfaces.Pickup_Api;
import com.example.win8.nokia.DirectionsJSONParser;
import com.example.win8.nokia.Pojo.CurrentLocation_pojo;
import com.example.win8.nokia.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.google.android.gms.maps.CameraUpdateFactory.newLatLng;
import static com.google.android.gms.maps.CameraUpdateFactory.zoomTo;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, LocationListener, GoogleApiClient.OnConnectionFailedListener {


    Pickup_Api pickup_api;
    CurrentLocation_pojo currentLocation_pojo;
    LatLng currentLocation_latlng;
    LatLng pickupPoint_latlng;
    TextView DistanceBetween_CurrentLocation_to_PickupPoint;
    Button startTrip;
    double distance;
    LinearLayout pickUp_linear, time_distance_linear;
    LatLng destination_latlng;
    private GoogleMap googleMap;
    private double longitude;
    private double latitude;
    private double pickup_latitude;
    private double pickup_longitude;
    private GoogleApiClient googleApiClient;
    // private EditText pickup_address;
    private EditText destn_address;
    private boolean isMapReady = false;
    private LocationRequest locationRequest;
    private String duration = "";
    private Marker mCurrLocationMarker;

    protected void createLocationRequest() {
        //  locationRequest = new LocationRequest();
       /* locationRequest.setInterval(15 * 1000);
        locationRequest.setFastestInterval(10 * 1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);*/
        locationRequest = new LocationRequest().create()
                .setInterval(5000)
                .setFastestInterval(5000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
        DistanceBetween_CurrentLocation_to_PickupPoint = findViewById(R.id.tv_distance);
        startTrip = findViewById(R.id.bt_start);
        startTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                googleMap.setMyLocationEnabled(true);
              /*  Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                LatLng latLng = new LatLng(latitude, longitude);
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(latLng)
                        .zoom(18).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

*/
            }
        });
        //pickup_address = (EditText) findViewById(R.id.pickup);
        destn_address = (EditText) findViewById(R.id.bt_destination);
        pickUp_linear = findViewById(R.id.pickup_layout);
        time_distance_linear = findViewById(R.id.linearone);
        pickUp_linear.setVisibility(View.INVISIBLE);

     /* if the distance between source and pick_up point
     less than 1000 meters it will be called */
//        if (currentLocation_latlng == pickupPoint_latlng) {
//            pickUp_linear.setVisibility(View.VISIBLE);
//            time_distance_linear.setVisibility(View.VISIBLE);
//            destn_address.requestFocus();
//            destn_address.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (destn_address.hasFocus()) {
//                        Intent intent = new Intent(MapsActivity.this, PlaceSelector.class);
//                        intent.putExtra("type", "pickup");
//                        startActivityForResult(intent, 1);
//                    }
//                }
//            });
//        }

        //Initializing googleapi client


    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // googleApiClient.disconnect();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        isMapReady = true;
    }

    /*Getting current location on maps*/
    private void getCurrentLocation() {
        googleMap.clear();
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        final LocationListener listener = this;
        googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                if (googleMap != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        }
                    }
                    LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, listener);
                }
                return false;
            }
        });
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location != null) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            moveMap();
        }
    }

    /* giving range for location 100 meters*/

    /* we are getting result back from placeSelector activity*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        {
            if (requestCode == 1 && resultCode == RESULT_OK) {
                String name;
                Double latitude, longitude;
                name = data.getStringExtra("place");
                Toast.makeText(this, "nameee" + name, Toast.LENGTH_SHORT).show();
                latitude = data.getDoubleExtra("lat", 0);
                Toast.makeText(this, "latitude" + latitude, Toast.LENGTH_SHORT).show();
                longitude = data.getDoubleExtra("lng", 0);
                Toast.makeText(this, "longitude" + longitude, Toast.LENGTH_SHORT).show();

                if (data.getStringExtra("case").equals("1")) {
                    Log.d("ABC", "name: " + name);
                    destn_address.setText(name);
                    destination_latlng = new LatLng(latitude, longitude);
                    Geocoder geocoder = new Geocoder(getApplicationContext());
                    try {
                        List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                        final String str = addressList.get(0).getAddressLine(0);
                        if (googleMap != null && googleApiClient != null)
                            Log.d("ABC", "adding Marker on destinations." + destination_latlng);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //  drawMarker(latLng);
                                if (isMapReady) {
                                    googleMap.clear();
                                    googleMap.addMarker(new MarkerOptions()
                                            .position(destination_latlng) //setting position
                                            .title("Destination")
                                            .draggable(true)).showInfoWindow();
                                    googleMap.moveCamera(newLatLng(destination_latlng));
                                    //Animating the camera
                                    googleMap.animateCamera(zoomTo(15));
                                    // AddPolyLineToDestination();
                                    AddPolyline();
                                    if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                        return;
                                    }
                                    googleMap.setMyLocationEnabled(true);
                                    googleMap.animateCamera(zoomTo(12));
                                    // time_distance_linear.setVisibility(View.VISIBLE);

                                }
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                } else {
                    Log.d("ABC", "name: " + name);

                }
            }
        }
    }

    private void moveMap() {
        String msg = latitude + ", " + longitude;
        //Creating a LatLng Object to store Coordinates
        currentLocation_latlng = new LatLng(latitude, longitude);
        Geocoder geocoder = new Geocoder(getApplicationContext());
        try {
            List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
            String str = addressList.get(0).getSubLocality();
            googleMap.addMarker(new MarkerOptions()
                    .position(currentLocation_latlng)//setting position
                    .title(str + "(Current Location)")
                    .draggable(true)).showInfoWindow();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String locality = getCompleteAddressString(latitude, longitude);
        //pickup_address.setText(locality);
        //Moving the camera
        googleMap.moveCamera(newLatLng(currentLocation_latlng));
        //Animating the camera
        googleMap.animateCamera(zoomTo(15));


    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return strAdd;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getCurrentLocation();
        postData();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onLocationChanged(Location location) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (location != null) {
            /*Getting Current Location  longitude and latitude*/
            double longitude3 = location.getLongitude();
            double latitude3 = location.getLatitude();
            LatLng latLng = new LatLng(latitude3, longitude3);
            currentLocation_latlng = latLng;
            /*moving the map to location through moveMap() method*/
            pickupPoint_latlng = new LatLng(pickup_latitude, pickup_longitude);
            moveMap();
            distance = CalculateByDistance(currentLocation_latlng, pickupPoint_latlng);
            DistanceBetween_CurrentLocation_to_PickupPoint.setText(distance + " km" + duration);

            //Place current location marker
            //  LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            Bitmap b = null;
            b = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.carfinal), 100, 60, false);
            final BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(b);
            if (mCurrLocationMarker != null) {
                mCurrLocationMarker.remove();
            }
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Position");
            markerOptions.icon(icon);
            mCurrLocationMarker = googleMap.addMarker(markerOptions);
        }
    }

    /* Showing pickupPoint in Googlemaps through this method*/
    private void getPickupLocation(double pickup_latitude, double pickup_longitude) {
        //String msg = latitude1 + ", " + longitude1;
        //Creating a LatLng Object to store Coordinates
        pickupPoint_latlng = new LatLng(pickup_latitude, pickup_longitude);
        Geocoder geocoder = new Geocoder(getApplicationContext());
        try {
            List<Address> addressList = geocoder.getFromLocation(pickup_latitude, pickup_longitude, 1);
            String str = addressList.get(0).getSubLocality();
            googleMap.addMarker(new MarkerOptions()
                    .position(pickupPoint_latlng) //setting position
                    .title(str + "(PickUp Point)")
                    .draggable(true)).showInfoWindow();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*Moving the camera*/
        googleMap.moveCamera(newLatLng(pickupPoint_latlng));
        /*Animating the camera*/
        googleMap.animateCamera(zoomTo(12));


    }

   /* private void AddPolyLineToDestination() {
        String url = getDirectionsUrl(currentLocation_latlng, destination_latlng);
        DownloadTask downloadTask = new DownloadTask();
        *//* Start downloading json data from Google Directions API*//*
        downloadTask.execute(url);
        distance = CalculateByDistance(currentLocation_latlng, destination_latlng);
        // String dis = new Double(distance).toString();
       // DistanceBetween_CurrentLocation_to_PickupPoint.setText("Distance:" + distance + "KM");
    }*/


    /* sending Current LOcation Latitude and Longitude to server and
        Getting PickUp Location Latitude and Longitude from server through postData() method*/
    private void postData() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Pickup_Api.Base_Url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        pickup_api = retrofit.create(Pickup_Api.class);
        currentLocation_pojo = new CurrentLocation_pojo(latitude, longitude);

        Call<CurrentLocation_pojo> call = pickup_api.insertdata(currentLocation_pojo);
        call.enqueue(new Callback<CurrentLocation_pojo>() {
            @Override
            public void onResponse(Call<CurrentLocation_pojo> call, Response<CurrentLocation_pojo> response) {
                CurrentLocation_pojo data = response.body();
                Toast.makeText(MapsActivity.this, "suceess", Toast.LENGTH_SHORT).show();
                //latitude1 = latitude;
                //longitude1 = longitude;
                 pickup_latitude = 12.9569;
                 pickup_longitude = 77.7015;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!isFinishing()) {
                            new AlertDialog.Builder(MapsActivity.this)
                                    .setTitle("Your Pickup point")
                                    .setMessage("Please press ok")
                                    .setCancelable(false)
                                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            getPickupLocation(pickup_latitude, pickup_longitude);
                                            AddPolyline();
                                        }
                                    }).show();
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call<CurrentLocation_pojo> call, Throwable t) {
                Toast.makeText(MapsActivity.this, "Failure", Toast.LENGTH_SHORT).show();
            }
        });

    }


    /* adding Polylines between CurrentLOcation and PickUp_Location through this method*/
    private void AddPolyline() {
        String url = getDirectionsUrl(currentLocation_latlng, pickupPoint_latlng);
        DownloadTask downloadTask = new DownloadTask();
        /* Start downloading json data from Google Directions API*/
        downloadTask.execute(url);
        distance = CalculateByDistance(currentLocation_latlng, pickupPoint_latlng);
        // String dis = new Double(distance).toString();
        //DistanceBetween_CurrentLocation_to_PickupPoint.setText("Distance:" + distance + "KM");

    }


    /* Finding Distance between Current Location and PickUp_Point through this Method*/
    private double CalculateByDistance(LatLng latLng, LatLng latLng1) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = latLng.latitude;
        double lat2 = latLng1.latitude;
        double lon1 = latLng.longitude;
        double lon2 = latLng1.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = Double.parseDouble(String.format("%.1f", valueResult));
        double meter = valueResult % 1000;
        return km;

    }

    /* getting DirectionsUrl between currentLocation and PickUp_point through this method*/
    private String getDirectionsUrl(LatLng latLng, LatLng latLng1) {
        String str_origin = "origin=" + latLng.latitude + "," + latLng.longitude;
        // Destination of route
        String str_dest = "destination=" + latLng1.latitude + "," + latLng1.longitude;
        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ParserTask parserTask = new ParserTask();
            parserTask.execute(s);
        }

        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }
    }

    /* Using AsynckTask for Parsing Data*/
    private class ParserTask extends AsyncTask<Object, Object, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(Object... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject((String) jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList points = null;
            PolylineOptions polylineOptions = null;
            String distance = "";


            if (result.size() < 1) {
                Toast.makeText(getBaseContext(), "No Points", Toast.LENGTH_SHORT).show();
                return;
            }
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();
                polylineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap point = path.get(j);

                    if (j == 0) {    // Get distance from the list
                        distance = (String) point.get("distance");
                        continue;
                    } else if (j == 1) { // Get duration from the list
                        duration = (String) point.get("duration");
                        continue;
                    }


                    double lat = Double.parseDouble((String) point.get("lat"));
                    double lng = Double.parseDouble((String) point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }
                polylineOptions.addAll(points);
                polylineOptions.width(20);
                polylineOptions.color(Color.BLUE);
                polylineOptions.geodesic(true);
                //DistanceBetween_CurrentLocation_to_PickupPoint.setText(distance+""+duration);

            }
            if (polylineOptions != null) {
                googleMap.addPolyline(polylineOptions);
                googleMap.setTrafficEnabled(true);
            } else {
                Toast.makeText(MapsActivity.this, "Directions Not Found", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
