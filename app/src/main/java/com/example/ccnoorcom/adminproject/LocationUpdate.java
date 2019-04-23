package com.example.ccnoorcom.adminproject;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;

import static com.example.ccnoorcom.adminproject.Helpers.isNetworkAvailable;
import static com.example.ccnoorcom.adminproject.Helpers.noInternetDialog;

public class LocationUpdate implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    Context mAct;
    public static GoogleApiClient mGoogleApiClient;
    LatLng latLng;

    public static Timer myTimer = new Timer();
    Location mLastLocation;

    String lastStation = "";

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(mAct, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mAct, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (isNetworkAvailable(AppController.getAppContext()))

            CreateNode();

        else noInternetDialog();

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void StartSchedule(final Context act, int period, LatLng latLng) {
        this.latLng = latLng;

        mAct = act;

        //


        myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
//                            getLastLocation();

                            buildGoogleApiClient();
                            if (mGoogleApiClient != null) {
                                mGoogleApiClient.connect();
                            } else {

                                Toast.makeText(mAct, "عدم دریافت موقعیت مکانی...", Toast.LENGTH_SHORT).show();

                            }


                        } catch (Exception ex) {
                            Log.e("t", ex.toString());
                        }
                    }
                });
                thread.start();
            }

        }, 0, period);

    }

    protected synchronized void buildGoogleApiClient() {
        try {
            mGoogleApiClient = new GoogleApiClient.Builder(mAct)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        } catch (Exception ex) {
            Log.d("buildGoogleApiClient", ex.getMessage());
        }


    }


    public void CancelTimer(String lastStation) {

        this.lastStation = lastStation;
        myTimer.cancel();

        Toast.makeText(mAct, "پایان", Toast.LENGTH_SHORT).show();
        CreateNode();
    }

    public void CancelTimer() {

        myTimer.cancel();

        Toast.makeText(mAct, "متوقف شد", Toast.LENGTH_SHORT).show();
    }
    public void CreateNode() {

        RequestParams params = new RequestParams();

        try {
            params.put("station_id", Helpers.getStation_id());
            if (lastStation != "")
                params.put("name",lastStation);
            params.put("lat", latLng.latitude);
            params.put("lng", latLng.longitude);
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

                        // Toast.makeText(mAct, "update", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                }
            }
        });
    }
}