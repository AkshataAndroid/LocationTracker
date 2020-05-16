package com.smartconnect.locationservice.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.snackbar.Snackbar;
import com.smartconnect.locationservice.BuildConfig;
import com.smartconnect.locationservice.Database.LocationDB;
import com.smartconnect.locationservice.R;
import com.smartconnect.locationservice.Services.LocationService;


import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    static final int READ_BLOCK_SIZE = 100;
    Context cob = this;
    Handler mHandler;
    Location location;
    private boolean mAlreadyStartedService = false;
    private TextView mMsgView, mMockLocationText, mNetworkType, mTimely, mBattery, mNetworkStrength;
    Handler delayhandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mMsgView = (TextView) findViewById(R.id.msgView);
        mMockLocationText = (TextView) findViewById(R.id.mocklocationText);
        mNetworkType = findViewById(R.id.networkType);
        mTimely = findViewById(R.id.timeDiffrence);
        mBattery = findViewById(R.id.battery);
        mNetworkStrength = findViewById(R.id.networkStrength);
        //timelyupdate
        SharedPreferences sharedPrefs = getSharedPreferences(
                "timely", MODE_PRIVATE);
        String timeData = sharedPrefs.getString("time", null);
        long timeInterval = Long.parseLong(timeData);
        long timeChange = timeInterval / 1000;


//        Bundle extras = getIntent().getExtras();
//        String value;
//        if (extras != null) {
//            value = extras.getString("startService");
//
//            startService(new Intent(this, LocationService.class));
//        }else{
//            stopService(new Intent(this, LocationService.class));
//        }

        //logfile
//do something
        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String latitude = intent.getStringExtra(LocationService.EXTRA_LATITUDE);
                        String longitude = intent.getStringExtra(LocationService.EXTRA_LONGITUDE);
                        String time = intent.getStringExtra(LocationService.TIME);
                        String speed = intent.getStringExtra(LocationService.SPEED);
                        String accuracy = intent.getStringExtra(LocationService.ACCURACY);
                        String altitude = intent.getStringExtra(LocationService.ALTITUDE);
                        String provider = intent.getStringExtra(LocationService.PROVIDER);
                        String date = intent.getStringExtra(LocationService.DATE);
                        String gps = intent.getStringExtra(LocationService.GPSPROVIDER);
                        String network = intent.getStringExtra(LocationService.NETWORKPROVIDER);
                        String passive = intent.getStringExtra(LocationService.PASSIVEPROVIDER);
                        ContentValues contentValues = new ContentValues();
                        // get  & set with contentvalues
                        contentValues.put(LocationDB.LAT, latitude);
                        contentValues.put(LocationDB.LNG, longitude);
                        contentValues.put(LocationDB.DATE, date);
                        contentValues.put(LocationDB.TIME, time);
                        contentValues.put(LocationDB.ALTITUDE, altitude);
                        contentValues.put(LocationDB.SPEED, speed);
                        contentValues.put(LocationDB.ACCURACY, accuracy);
                        contentValues.put(LocationDB.PROVIDER, provider);
                        contentValues.put(LocationDB.GPSPROVIDER, gps);
                        contentValues.put(LocationDB.NETWORKPROVIDER, network);
                        contentValues.put(LocationDB.PASSIVEPROVIDER, passive);
                        LocationDB lb = new LocationDB(cob);
                        long row = lb.insert(latitude, longitude, date, time, altitude, speed, accuracy, provider,gps,network,passive);
                        if (row > 0) {
                            Toast.makeText(cob, "Your Location Inserted Successfully....",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(cob, "Something Wrong...", Toast.LENGTH_SHORT).show();
                        }
                        if (latitude != null && longitude != null) {
                            mMsgView.setText(getString(R.string.msg_location_service_started) + "\n Latitude : " + latitude + "\n Longitude : " + longitude + "\n Time :" + date + "\n Speed :" + speed + "\n Accuracy : " + accuracy + "\n Altitude : " + altitude + "\n Provider : " + provider + "\n Date : " + time + "\n GPS Provider :"+gps+"\n NETWORK Provider  :" +network+ "\n PASSIVE Provider  :" +passive+"");
                            Log.i("Latitude :", latitude);
                            Log.i("Longitude :", longitude);
                            Log.i("Time :", time);
                            Log.i("Date : ", date);
                            Log.i("Speed :", speed);
                            Log.i("Accuracy :", accuracy);
                            Log.i("Altitude :", altitude);
                            Log.i("Provider :", provider);
                        }

                        //  timelyupdate
                        SharedPreferences sharedPrefs =getSharedPreferences(
                                "timely", MODE_PRIVATE);
                        String timeData = sharedPrefs.getString("time",null);
                        long timeInterval=Long.parseLong(timeData);
                        long timeChange= timeInterval/1000;
                        mTimely.setText("Your Location Will Update in  " + timeChange + "  Seconds");
                        if (isExternalStorageWritable()) {
                            File appDirectory = new File(Environment.getExternalStorageDirectory() + "/MyPersonalAppFolder");
                            File logDirectory = new File(appDirectory + "/log");
                            File logFile = new File(logDirectory, "logcat" + System.currentTimeMillis() + ".txt");
                            // create app folder
                            if (!appDirectory.exists()) {
                                appDirectory.mkdir();
                            }
                            // create log folder
                            if (!logDirectory.exists()) {
                                logDirectory.mkdir();
                            }
                            // clear the previous logcat and then write the new one to the file
                            try {
                                Process process = Runtime.getRuntime().exec("logcat -c");
                                process = Runtime.getRuntime().exec("logcat -f " + logFile);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        } else if (isExternalStorageReadable()) {
                            // only readable
                        } else {
                            // not accessible
                        }
                    }
                }
                , new IntentFilter(LocationService.ACTION_LOCATION_BROADCAST)
        );


//startActivity(getIntent());
    }


    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }


    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
//    private final Runnable m_Runnable = new Runnable()
//    {
//        public void run()
//
//        {
//            SharedPreferences sharedPrefs =getSharedPreferences(
//                    "timely", MODE_PRIVATE);
//            String timeData = sharedPrefs.getString("time",null);
//            long timeInterval=Long.parseLong(timeData);
//            long timeChange= timeInterval/1000;
//
//            Toast.makeText(MainActivity.this,"in runnable",Toast.LENGTH_SHORT).show();
//
//            MainActivity.this.mHandler.postDelayed(m_Runnable, timeChange);
//        }
//
//    };//runnable


    @Override
    protected void onPause() {
        super.onPause();
//        mHandler.removeCallbacks(m_Runnable);
//        finish();

    }


    @Override
    public void onResume() {
        super.onResume();
        //mocklocation
        SharedPreferences myPrefs =this.getSharedPreferences(
                "myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = myPrefs.edit();
        String userData = myPrefs.getString("ALERT","");
        mMockLocationText.setText(userData);


        //timelyupdate
//        SharedPreferences sharedPrefs =getSharedPreferences(
//                "timely", MODE_PRIVATE);
//        String timeData = sharedPrefs.getString("time",null);
//        long timeInterval=Long.parseLong(timeData);
//        long timeChange= timeInterval/1000;




        //   mTimely.setText("your Location will update in  "+timeChange+"  Seconds");
//        Intent dailyQuotes = new Intent(getApplicationContext(), LocationService.class);
//        AlarmManager am = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
//        PendingIntent pi = PendingIntent.getService(getApplicationContext(), 0, dailyQuotes, 0);
//        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, timeChange, 30000, pi);

        //Batterypercentage
        SharedPreferences batPref =this.getSharedPreferences(
                "battery", MODE_PRIVATE);

        String percentage = batPref.getString("ALERT",null);
        mBattery.setText("Battery "+percentage+" %");
        startStep1();

    }


    /**
     * Step 1: Check Google Play services
     */
    private void startStep1() {

        //Check whether this user has installed Google play service which is being used by Location updates.
        if (isGooglePlayServicesAvailable()) {
            //Passing null to indicate that it is executing for the first time.
            startStep2(null);
        } else {
            Toast.makeText(getApplicationContext(), R.string.no_google_playservice_available, Toast.LENGTH_LONG).show();
        }
    }
    /**
     * Step 2: Check & Prompt Internet connection
     */
    private Boolean startStep2(DialogInterface dialog) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                .isConnectedOrConnecting();
//For WiFi Check
        boolean isWifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .isConnectedOrConnecting();

        TelephonyManager telephonyManager= (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);

        int networkType=telephonyManager.getNetworkType();
        System.out.println(mobile + " net " + isWifi);

        if (mobile == true)
        {
            if(networkType==TelephonyManager.NETWORK_TYPE_LTE){

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    mNetworkStrength.setText("4G is Connected  and Signel Strength "+telephonyManager.getSignalStrength().getEvdoDbm());
                }
                // mNetworkStrength.setText("4G is Connected");
            }else if( networkType==TelephonyManager.NETWORK_TYPE_LTE){
                mNetworkStrength.setText("3G is Connected");
            }else if(networkType==TelephonyManager.NETWORK_TYPE_GPRS){
                mNetworkStrength.setText("2G is Connected");
            }
            mNetworkType.setText("Mobile Network is connected");
            Toast.makeText(getApplicationContext(),"Mobile Network is connectd",Toast.LENGTH_LONG).show();
        }
        else if(isWifi==true)
        {
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(getApplicationContext().WIFI_SERVICE);
            List<ScanResult> scanResult = wifiManager.getScanResults();
            for (int i = 0; i < scanResult.size(); i++) {
                mNetworkStrength.setText("Wifi Strength "+scanResult.get(i).level+"");
                Log.d("scanResult", "Speed of wifi"+scanResult.get(i).level);//The db level of signal
            }

            mNetworkType.setText("Wifi is connected");
            Toast.makeText(getApplicationContext(),"WiFi  is connected",Toast.LENGTH_LONG).show();
        }
        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
            promptInternetConnect();
            return false;
        }


        if (dialog != null) {
            dialog.dismiss();
        }
        //Yes there is active internet connection. Next check Location is granted by user or not.
        if (checkPermissions()) { //Yes permissions are granted by the user. Go to the next step.
            startStep3();
        } else {  //No user has not granted the permissions yet. Request now.
            requestPermissions();
        }
        return true;
    }
    /**
     * Show A Dialog with button to refresh the internet state.
     */
    private void promptInternetConnect() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.title_alert_no_internet);
        builder.setMessage(R.string.msg_alert_no_internet);
        String positiveText = getString(R.string.btn_label_refresh);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //Block the Application Execution until user grants the permissions
                        if (startStep2(dialog)) {

                            //Now make sure about location permission.
                            if (checkPermissions()) {
                                //Step 2: Start the Location Monitor Service
                                //Everything is there to start the service.
                                startStep3();
                            } else if (!checkPermissions()) {
                                requestPermissions();
                            }
                        }
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Step 3: Start the Location Monitor Service
     */
    private void startStep3() {
        if (!mAlreadyStartedService && mMsgView != null) {
            mMsgView.setText(R.string.msg_location_service_started);
            Intent intent;
            //Start location sharing service to app server.........
            Bundle extras = getIntent().getExtras();
            if (Settings.Secure.getString(getContentResolver(),
                    Settings.Secure.ALLOW_MOCK_LOCATION).equals("0")){
//                intent = new Intent(this, LocationService.class);
//                stopService(intent);
            }
            else{
//                intent = new Intent(this, LocationService.class);
//                startService(intent);
            }
            mAlreadyStartedService = false;
        }
    }

    /**
     * Return the availability of GooglePlayServices
     */
    public boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(this, status, 2404).show();
            }
            return false;
        }
        return true;
    }



    private boolean checkPermissions() {
        int permissionState1 = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        int permissionState2 = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        return permissionState1 == PackageManager.PERMISSION_GRANTED && permissionState2 == PackageManager.PERMISSION_GRANTED;

    }


    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        boolean shouldProvideRationale2 =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION);
        if (shouldProvideRationale || shouldProvideRationale2) {
            //  Log.i(TAG, "Displaying permission rationale to provide additional context.");
            showSnackbar(R.string.permission_rationale,
                    android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }
    @SuppressLint("WrongConstant")
    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        //   Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //  Log.i(TAG, "Permission granted, updates requested, starting location updates");
                startStep3();
            } else {
                showSnackbar(R.string.permission_denied_explanation,
                        R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }
    }


    @Override
    public void onDestroy() {
        stopService(new Intent(this, LocationService.class));
        mAlreadyStartedService = false;
        super.onDestroy();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent i= new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(i);


        }
        return super.onOptionsItemSelected(item);
    }


}