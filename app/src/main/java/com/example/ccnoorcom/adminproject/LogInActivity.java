package com.example.ccnoorcom.adminproject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;


import java.io.File;

import cz.msebera.android.httpclient.Header;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.example.ccnoorcom.adminproject.Helpers.isNetworkAvailable;
import static com.example.ccnoorcom.adminproject.Helpers.noInternetDialog;

public class LogInActivity extends AppCompatActivity {


    EditText mobile_input;
    Button btn_confrim;
    ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        File Item2 = new File("/data/data/" + getPackageName() + "/shared_prefs/verified.xml");
        if (Item2.exists()) {

            Intent i1 = new Intent(getApplicationContext(), MapsActivity.class);
            i1.setAction(Intent.ACTION_MAIN);
            i1.addCategory(Intent.CATEGORY_HOME);
            i1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            i1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);


            startActivity(i1);
            overridePendingTransition(0, 0);

            finish();

        }

        pd = new ProgressDialog(LogInActivity.this);
        pd.setMessage("لطفا تامل نمایید...");

        pd.setCancelable(false);
        mobile_input = findViewById(R.id.mobile_input);
        mobile_input.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (isNetworkAvailable(AppController.getAppContext())) {

                    if (actionId == EditorInfo.IME_ACTION_GO) {
                    pd.show();
                    if (mobile_input.length() == 11) {
                        //Helpers.setMobile(mobile_input.getText().toString());

                        login(mobile_input.getText().toString());
                    } else {
                        Toast.makeText(LogInActivity.this, "شماره موبایل باید حتما 11 رقم باشد.", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                    return true;
                }
                }
                else noInternetDialog();
                return false;

            }


        });
        btn_confrim = findViewById(R.id.btn_confrim);

        btn_confrim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.show();
                if (isNetworkAvailable(AppController.getAppContext())) {

                    if (mobile_input.length() == 11) {
                        //   Helpers.setMobile(mobile_input.getText().toString());

                        login(mobile_input.getText().toString());
                    } else {
                        Toast.makeText(LogInActivity.this, "شماره موبایل باید حتما 11 رقم باشد.", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }else noInternetDialog();
            }
        });


    }

    public void login(final String mobile) {

        RequestParams params = new RequestParams();

        params.put("mobile", mobile);

        params.put("api_token","zU79Rd6LOR3bWGSibmFVhnX1gsb4GfLBysWx88dxLcR5VVrPg8jjMDK8RfmqB9kR");


        Helpers.client.post(Helpers.baseUrl + "masteruser_verify", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                pd.dismiss();
                //noInternetDialog();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                pd.dismiss();
                try {
                    if (responseString.contains("ok")) {

                        Intent i1 = new Intent(getApplicationContext(), VerificationActivity.class);
                        i1.setAction(Intent.ACTION_MAIN);
                        i1.addCategory(Intent.CATEGORY_HOME);
                        i1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        i1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        i1.putExtra("mobile", mobile_input.getText().toString());

                        startActivity(i1);
                        overridePendingTransition(0, 0);

                        finish();

                    }
                } catch (Exception e) {
                }
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));

    }


//    public void noInternetDialog() {
//        Typeface face = Typeface.createFromAsset(AppController.getAppContext().getAssets(),
//                "IRANSans(FaNum).ttf");
//        MoAlertDialog dialog = new MoAlertDialog(LoginActivity.this);
//
//        dialog.showSuccessDialog("عدم اتصال به اینترنت ", "لطفا اتصال خود را با اینترنت بررسی نمایید.");
//
//        dialog.setTypeface(face);
//
//        dialog.setDilogIcon(R.drawable.ic_no_internet);
//        dialog.setDialogButtonText("باشه!");
//    }


}
