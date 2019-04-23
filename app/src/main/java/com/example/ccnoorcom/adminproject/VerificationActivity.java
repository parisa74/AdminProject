package com.example.ccnoorcom.adminproject;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.goodiebag.pinview.Pinview;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;
import swarajsaaj.smscodereader.interfaces.OTPListener;
import swarajsaaj.smscodereader.receivers.OtpReader;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.example.ccnoorcom.adminproject.Helpers.isNetworkAvailable;
import static com.example.ccnoorcom.adminproject.Helpers.noInternetDialog;

public class VerificationActivity extends AppCompatActivity implements OTPListener {
    Pinview pin;
    Timer timer;
    long timercount;
    TextView timer_txt, resend, correct, mobile_txt;
    int check;
    String mobile;
    Button btn_confrim;

    String code;

    ProgressDialog pd;
    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {
            android.Manifest.permission.READ_CONTACTS,
            android.Manifest.permission.WRITE_CONTACTS,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_SMS,
            android.Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION


    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);


        if (!hasPermissions(VerificationActivity.this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(VerificationActivity.this, PERMISSIONS, PERMISSION_ALL);
        }
        pd = new ProgressDialog(VerificationActivity.this);
        pd.setMessage("لطفا تامل نمایید...");
        pd.setCancelable(false);
        correct = findViewById(R.id.correct);

        mobile_txt = findViewById(R.id.mobile);


        btn_confrim = findViewById(R.id.btn_confrim);

        Intent intent = getIntent();
        mobile = intent.getStringExtra("mobile");
        mobile_txt.setText(mobile);


        gettime();

        resend = findViewById(R.id.resend);

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.show();

                gettime();

                if (isNetworkAvailable(AppController.getAppContext()))
                    resend_code(mobile);

                else noInternetDialog();

            }
        });
        timer_txt = findViewById(R.id.timer);
        OtpReader.bind(this, "+98300077");

        pin = (Pinview) findViewById(R.id.code);

        pin.setPinViewEventListener(new Pinview.PinViewEventListener() {
            @Override
            public void onDataEntered(Pinview pinview, boolean fromUser) {
                //Make api calls here or what not
                if (isNetworkAvailable(AppController.getAppContext())) {

                    try {
                        pd.show();
                    } catch (Exception e) {
                    }
                    code = pin.getValue();
                    verify(code);
//                Log.e("vrifiy",code);
                } else noInternetDialog();
            }
        });

        btn_confrim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.show();
                if (isNetworkAvailable(AppController.getAppContext()))

                verify(code);

                else noInternetDialog();
            }
        });

        correct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i1 = new Intent(getApplicationContext(), LogInActivity.class);
                i1.setAction(Intent.ACTION_MAIN);
                i1.addCategory(Intent.CATEGORY_HOME);
                i1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                overridePendingTransition(0, 0);

                startActivity(i1);
                finish();
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));

    }

    @Override
    public void otpReceived(String messageText) {
        code = parseCode(messageText);
        pin.setValue(code);

    }

    private String parseCode(String message) {
        Pattern p = Pattern.compile("\\b\\d{4}\\b");
        Matcher m = p.matcher(message);
        String code = "";
        while (m.find()) {
            code = m.group(0);
        }
        return code;
    }

    void gettime() {
        timercount = 60;
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        timercount -= 1;
                        timer_txt.setText(gettimer(timercount));
                        resend.setEnabled(false);
                        resend.setTextColor(Color.GRAY);
                        if (timercount == 0) {
                            timer.cancel();
                            timer_txt.setText("00:00");
                            resend.setTextColor(Color.BLACK);
                            resend.setEnabled(true);

                            check = 1;
                        }
                    }
                });
            }
        }, 0, 1000);
    }

    public String gettimer(long timercount) {
        long secound = (timercount);
        long mint = secound / 60;
        secound %= 60;
        return String.format(Locale.ENGLISH, "%02d", mint) + " : " + String.format(Locale.ENGLISH, "%02d", secound);
    }


    public void verify(String code) {

        RequestParams params = new RequestParams();

        params.put("mobile", mobile);
        params.put("code", code);

        params.put("api_token","zU79Rd6LOR3bWGSibmFVhnX1gsb4GfLBysWx88dxLcR5VVrPg8jjMDK8RfmqB9kR");

        Helpers.client.post(Helpers.baseUrl + "masteruser_enter", params, new TextHttpResponseHandler() {
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
                        JsonParser jsonParser = new JsonParser();
                        JsonObject objectFromString = jsonParser.parse(responseString).getAsJsonObject();
                        Helpers.setTaxi_id(objectFromString.get("user_id").toString());

                        Helpers.addToSharePrf("api_token",objectFromString.get("api_token").getAsString());
                        Intent i1 = new Intent(getApplicationContext(), MapsActivity.class);
                        i1.setAction(Intent.ACTION_MAIN);
                        i1.addCategory(Intent.CATEGORY_HOME);
                        i1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        i1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        startActivity(i1);
                        overridePendingTransition(0, 0);

                        finish();
                    } else {
                        Toast.makeText(VerificationActivity.this, "کد وارد شده اشتباه است", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                }
            }
        });
    }


    public void resend_code(final String mobile) {
        pd.show();
        RequestParams params = new RequestParams();

        params.put("mobile", mobile);

        Helpers.client.post(Helpers.baseUrl + "masteruser_verify", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                pd.dismiss();
                //    noInternetDialog();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                pd.dismiss();
                try {
                    if (responseString.contains("ok")) {


                    }
                } catch (Exception e) {
                }
            }

        });
    }

//    public void noInternetDialog() {
//        Typeface face = Typeface.createFromAsset(AppController.getAppContext().getAssets(),
//                "fonts/IRANSans(FaNum).ttf");
//        MoAlertDialog dialog = new MoAlertDialog(VerificationActivity.this);
//
//        dialog.showSuccessDialog("عدم اتصال به اینترنت ", "لطفا اتصال خود را با اینترنت بررسی نمایید.");
//
//        dialog.setTypeface(face);
//
//        dialog.setDilogIcon(R.drawable.ic_no_internet);
//        dialog.setDialogButtonText("باشه!");
//    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
