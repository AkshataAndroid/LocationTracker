package com.smartconnect.locationservice.Activity;

import android.app.Activity;
import android.app.AppOpsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;


import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.smartconnect.locationservice.BuildConfig;
import com.smartconnect.locationservice.R;
import com.smartconnect.locationservice.Services.LocationService;


public class SettingsActivity extends AppCompatActivity {
    GoogleApiClient mLocationClient;
    LocationRequest mLocationRequest = new LocationRequest();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }



    public static  class SettingsFragment extends PreferenceFragmentCompat  {

        private Activity mActivity;
        SwitchPreferenceCompat mMockLocation,mUpdateSwitch,mBatterOptimisation,mBackgroundUpdate;
        EditTextPreference mTimelyLocationUpdate;
        GoogleApiClient mGoogleApiClient;
        Context mContext;
        int status;
        String enabled="Mocklocation enabled";
        String disabled="Mocklocation disabled";
        SharedPreferences sharedPref ;

        @Override
        public void onCreatePreferences(final Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
             mUpdateSwitch = (SwitchPreferenceCompat) findPreference(this.getResources().getString(R.string.update));
            mMockLocation= (SwitchPreferenceCompat) findPreference(this.getResources().getString(R.string.mock));
            mBackgroundUpdate=findPreference(this.getResources().getString(R.string.Background_update));
            mTimelyLocationUpdate=findPreference(this.getResources().getString(R.string.timely));

//            remainder = mTimelyLocationUpdate - mins * 60;
//            int secs = remainder;
            mTimelyLocationUpdate.setOnBindEditTextListener(new EditTextPreference.OnBindEditTextListener() {
                @Override
                public void onBindEditText(@NonNull EditText editText) {
                    editText.setInputType(InputType.TYPE_DATETIME_VARIATION_TIME);
                    SharedPreferences.Editor editor = getContext().getSharedPreferences("timely", MODE_PRIVATE).edit();
                    // editor.putString("time", "Update location in every  "+mTimelyLocationUpdate.getText()+"  Seconds");
                    editor.putString("time", mTimelyLocationUpdate.getText());
                    editor.apply();
                }
            });

//            SharedPreferences getWeightAndAgeStore = getContext().getSharedPreferences("timely", Context.MODE_PRIVATE);
//           // mTimelyLocationUpdate.setText(getWeightAndAgeStore.getString("WEIGHT", "0"));

            mBatterOptimisation=findPreference(this.getResources().getString(R.string.Battery));


//update location
            mUpdateSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (!(mUpdateSwitch.isChecked())) {

                        Intent intent = new Intent(getContext(), LocationService.class);
                        Bundle data1 = new Bundle();
                        data1.putString("startService","clicked");
                       getContext(). startService(intent);
//                        LocationService locationService=new LocationService();
//                        locationService.

                    }else {
                        Intent intent = new Intent(getContext(), LocationService.class);
                        intent.putExtra("stopService","notclicked");
                        getContext().stopService(intent);
//                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                        startActivity(intent);
//                        LocationService locationService=new LocationService();
//                        locationService.onConnectionSuspended(Context.MODE_PRIVATE);
                    }

                    return true;
                }
            });


            //mock location
            mMockLocation.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (!checkMock()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage("In order to use this app you must disable mock location do you want to disable it now?").setTitle("Mock location is enabled");
                        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
                                startActivity(i);

                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();

                        // Toast.makeText(getContext(), enabled, Toast.LENGTH_SHORT).show();
                        SharedPreferences myPrefs = getContext().getSharedPreferences(
                                "myPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = myPrefs.edit();
                        editor.putString("ALERT","Mocklocation enabled");
                        editor.apply();



                    }else{


                        SharedPreferences myPrefs =getContext().getSharedPreferences(
                                "myPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = myPrefs.edit();
                        editor.putString("ALERT","Mocklocation disabled");
                        editor.apply();

                       // String userData = myPrefs.getString("ALERT","");

                    }
                    return true;
                }


            });
            //TimelyLocationUpadte



            //BatteryOptimisation
            mBatterOptimisation.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    PowerConnectionReceiver   receiver = new PowerConnectionReceiver();
                    IntentFilter ifilter = new IntentFilter();
                    ifilter.addAction(Intent.ACTION_POWER_CONNECTED);
                    ifilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
                    ifilter.addAction(Intent.ACTION_BATTERY_LOW);
                    ifilter.addAction(Intent.ACTION_BATTERY_OKAY);
                    getContext(). registerReceiver(receiver, ifilter);

                    return true;
                }
            });

        }

        private boolean checkMock() {
            super.onStart();

            boolean isMockLocation = true;

            try
            {
                //if marshmallow
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    AppOpsManager opsManager = (AppOpsManager) getContext().getSystemService(Context.APP_OPS_SERVICE);
                    isMockLocation = (opsManager.checkOp(AppOpsManager.OPSTR_MOCK_LOCATION, android.os.Process.myUid(), BuildConfig.APPLICATION_ID)== AppOpsManager.MODE_DEFAULT);
                    Toast.makeText(getContext(), "MockLocation allowded", Toast.LENGTH_SHORT).show();

                }
                else
                {
                    // in marshmallow this will always return true
                    isMockLocation = !Settings.Secure.getString(getContext().getContentResolver(), "mock_location").equals("0");
                }
            }
            catch (Exception e)
            {
                return isMockLocation;
            }

            return isMockLocation;

        }



    }
    public static class PowerConnectionReceiver extends BroadcastReceiver {

        public PowerConnectionReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            int level   = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale   = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            double batteryPct = (double) level / (double) scale;
            int percent = (int) (batteryPct * 100D);
            String percentage=String.valueOf(percent);
            SharedPreferences myPrefs = context.getSharedPreferences(
                    "battery", MODE_PRIVATE);
            SharedPreferences.Editor editor = myPrefs.edit();
            editor.putString("ALERT", percentage);
            editor.apply();


            if (intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Battery percentage " +percentage+"%").setTitle("Battery is Connected");
                builder.setCancelable(true);
                AlertDialog dialog = builder.create();
                dialog.show();
                Toast.makeText(context, "The device is charging", Toast.LENGTH_SHORT).show();
            } else  if (intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED)){

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Battery percentage " +percentage+"%").setTitle("Battery is Disconnected");
                builder.setCancelable(true);
                AlertDialog dialog = builder.create();
                dialog.show();
                Toast.makeText(context, "The device is not charging", Toast.LENGTH_SHORT).show();
            }else if(intent.getAction().equals(Intent.ACTION_BATTERY_LOW)){

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Battery percentage " +percentage+"%").setTitle("Battery is low");
                builder.setCancelable(true);
                AlertDialog dialog = builder.create();
                dialog.show();

            }else if(intent.getAction().equals(Intent.ACTION_BATTERY_OKAY)){

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Battery percentage " +percentage+"%").setTitle("Battery is okay");
                builder.setCancelable(true);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }


    }
    @Override
    protected void onStart() {
        super.onStart();
//        Intent intent = new Intent(this, LocationService.class);
//        startService(intent);


    }

    @Override
    protected void onStop() {
        super.onStop();
//        Intent intent = new Intent(this, LocationService.class);
//        stopService(intent);


    }


    private void UpdateLocation(FragmentActivity activity) {


    }
}
