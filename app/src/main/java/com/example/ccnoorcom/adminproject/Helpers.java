package com.example.ccnoorcom.adminproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import com.loopj.android.http.AsyncHttpClient;

import omidi.mehrdad.moalertdialog.MoAlertDialog;

public class Helpers {

    public static AsyncHttpClient client = new AsyncHttpClient();

    public static String baseUrl ="http://admin.idpz.ir/api/";  //todo"http://admin.idpz.ir/api/";


    public static String taxi_id;

    public static String station_id;

    public static String station_name;


    public static String getStation_id() {
        return station_id;
    }

    public static void setStation_id(String station_id) {
        Helpers.station_id = station_id;
    }

    public static String getStation_name() {
        return station_name;
    }

    public static void setStation_name(String station_name) {
        Helpers.station_name = station_name;
    }

    public static String getTaxi_id() {

        SharedPreferences sharedPreferences = AppController.getAppContext().getSharedPreferences("USER_ID", 0);
        return sharedPreferences.getString("user_id", "");
    }

    public static void setTaxi_id(String taxi_id) {

        SharedPreferences sharedPreferences = AppController.getAppContext().getSharedPreferences("USER_ID", 0);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("user_id", taxi_id);
        editor.commit();
        Helpers.taxi_id = taxi_id;
    }

    public static String getMobile() {
        SharedPreferences sharedPreferences = AppController.getAppContext().getSharedPreferences("mobile", 0);
        return sharedPreferences.getString("m", "");    }

    public static void setMobile(String mobile) {
        SharedPreferences sharedPreferences = AppController.getAppContext().getSharedPreferences("mobile", 0);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("m", mobile);
        editor.commit();
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void noInternetDialog() {
        Typeface face = Typeface.createFromAsset(AppController.getAppContext().getAssets(),
                "fonts/IRANSans(FaNum).ttf");
        MoAlertDialog dialog = new MoAlertDialog(AppController.getAppContext());

        dialog.showSuccessDialog("عدم اتصال به اینترنت ", "لطفا اتصال خود را با اینترنت بررسی نمایید.");

        dialog.setTypeface(face);

        dialog.setDilogIcon(R.drawable.ic_no_internet);
        dialog.setDialogButtonText("باشه!");
    }

    public static void addToSharePrf(String key, String value) {
        PreferenceManager.getDefaultSharedPreferences(AppController.getAppContext()).edit()
                .putString(key, value).apply();
    }


    public static String getSharePrf(String key) {
        return PreferenceManager.getDefaultSharedPreferences(AppController.getAppContext()).getString(key, "");
    }
}
