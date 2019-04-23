package com.example.ccnoorcom.adminproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.support.design.widget.Snackbar;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.example.ccnoorcom.adminproject.Helpers.isNetworkAvailable;
import static com.example.ccnoorcom.adminproject.Helpers.noInternetDialog;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng mCenterLatLong;

    String station_id, station_name;
    EditText txt_name, txt_number, txt_start, txt_end, stationname;
    Button btn, btn2, btn3;
    private ArrayList<Float> distances = new ArrayList<>();
    private List<Area> areaArrayList;
    int minIndex;
    LocationUpdate locationUpdate = new LocationUpdate();

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        SharedPreferences sharedPreferences = AppController.getAppContext().getSharedPreferences("verified", 0);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("flag", "true");
        editor.commit();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        stationname = findViewById(R.id.stationname);

        txt_end = findViewById(R.id.txt_end);

        txt_name = findViewById(R.id.txt_name);


        txt_start = findViewById(R.id.txt_start);

        btn = findViewById(R.id.btn);

        btn2 = findViewById(R.id.btn2);

        btn3 = findViewById(R.id.btn3);


        stationname.setText("");


        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn3.setVisibility(View.GONE);
                Toast.makeText(MapsActivity.this,"متوقف شد",Toast.LENGTH_LONG).show();
                locationUpdate.CancelTimer();

            }
        });


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn.getText().equals("شروع خط!")) {
                    Snackbar.make(v, "برای شروع دست خود را بر روی دکمه نگه دارید.", Snackbar.LENGTH_SHORT).show();

                } else
                    Snackbar.make(v, "برای پایان دست خود را بر روی دکمه نگه دارید.", Snackbar.LENGTH_SHORT).show();

            }
        });
        btn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (isNetworkAvailable(AppController.getAppContext())) {

                    if (btn.getText().equals("شروع خط!")) {
                        btn.setBackgroundColor(getResources().getColor(R.color.green_700));
                        try {


                            Create();
                            //  CreateNode();

                        } catch (Exception e) {
                        }
                        btn.setText("پایان خط!");
                        stationname.setVisibility(View.VISIBLE);
                        btn2.setVisibility(View.VISIBLE);
                        btn3.setVisibility(View.VISIBLE);
                    } else if (btn.getText().equals("پایان خط!")) {

                        try {
                            locationUpdate.CancelTimer(txt_end.getText().toString());

                            btn.setBackgroundColor(getResources().getColor(R.color.blue_700));
                            btn.setText("شروع خط!");

                            txt_end.setText("");
                            txt_name.setText("");
                            txt_start.setText("");

                            stationname.setVisibility(View.GONE);
                            btn2.setVisibility(View.GONE);
                            btn3.setVisibility(View.GONE);


                        } catch (Exception e) {
                        }
                    }
                } else noInternetDialog();
                return true;
            }
        });
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng latLong = new LatLng(35.684209, 51.388263);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLong).zoom(16).build();

        mMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

        googleMap.setMyLocationEnabled(true);
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(final CameraPosition cameraPosition) {


                Log.d("Camera postion change" + "", cameraPosition + "");
                mCenterLatLong = cameraPosition.target;

                // Add a marker in Sydney and move the camera
//        mMap.addMarker(new MarkerOptions().position(latLong).title("Marker in Sydney"));
                //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLong));
                if (isNetworkAvailable(AppController.getAppContext()))
                    getAreas();
                else noInternetDialog();

                if (mCenterLatLong != null)
                    btn2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            CreateStation(mCenterLatLong.latitude, mCenterLatLong.longitude, stationname.getText().toString());
                        }
                    });


            }

        });
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));

    }


    public void getAreas() {


        RequestParams params = new RequestParams();
        //    params.put("db", "states");
        //admin.idpz.ir/api/getstates

        Helpers.client.get("http://admin.idpz.ir/api/getstate", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {


                try {
                    Gson gson = new Gson();

                    Area[] areaModel = gson.fromJson(responseString, Area[].class);

                    areaArrayList = Arrays.asList(areaModel);

                    findArea();
//                    if (areaModel.length > 0)
//                        for (int i = 0; i < areaModel.length; i++) {
//                            //findArea(areaModel[i]);
//                            Log.d(TAG, "onSuccess: " + areaModel[i].getName());
//
//                        }

                } catch (Exception e) {

                }

            }
        });

    }

    public void findArea() {


        distances.clear();

        for (Area area : areaArrayList) {
            float myDistance = 0;
            Location mycity = new Location("");
            Location myLocation = new Location("");
            myLocation.setLatitude(mCenterLatLong.latitude);
            myLocation.setLongitude(mCenterLatLong.longitude);

            mycity.setLatitude(area.getAttributes().getLat());
            mycity.setLongitude(area.getAttributes().getLng());
            myDistance = Math.round(myLocation.distanceTo(mycity));
            distances.add(myDistance);

        }

        minIndex = distances.indexOf(Collections.min(distances));
        mMap.clear();

        Log.d("pariarea", "findArea: " + areaArrayList.get(minIndex).getId().toString());

    }

    public void Create() {

        RequestParams params = new RequestParams();

        params.put("state_id", minIndex);
        params.put("start", txt_start.getText().toString());
        params.put("end", txt_end.getText().toString());
        params.put("color", "4CAF50");
        params.put("name", txt_name.getText().toString());


        params.put("api_token", Helpers.getSharePrf("api_token"));

        Helpers.client.post(Helpers.baseUrl + "make_station", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                //noInternetDialog();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

                try {
                    if (responseString.contains("ok")) {

                        Gson gson = new Gson();

                        Line line = gson.fromJson(responseString, Line.class);
                        Helpers.setStation_id(line.getStationId().toString());
                        Helpers.setStation_name(line.getName());


                        CreateStation(mCenterLatLong.latitude, mCenterLatLong.longitude, txt_start.getText().toString());
                        //     locationUpdate.StartSchedule(MapsActivity.this, 10000, mCenterLatLong);

                    }
                } catch (Exception e) {
                }

            }
        });
    }


    public void CreateStation(double latitude, double longitude, String station_name) {

        RequestParams params = new RequestParams();

        try {
            params.put("station_id", Helpers.getStation_id());

            params.put("name", station_name);
            params.put("lat", latitude);
            params.put("lng", longitude);
            params.put("api_token", Helpers.getSharePrf("api_token"));

        } catch (Exception e) {
        }
        Helpers.client.post(Helpers.baseUrl + "make_nods", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                //noInternetDialog();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {


                    if (responseString.contains("ok")) {
                        stationname.setText("");

                    //    Toast.makeText(MapsActivity.this, "ایستگاه ثبت شد", Toast.LENGTH_SHORT).show();

                        locationUpdate.StartSchedule(MapsActivity.this, 10000, mCenterLatLong);

                        btn3.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                }
            }
        });
    }
}
