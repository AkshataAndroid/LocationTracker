package com.smartconnect.locationtracker.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.smartconnect.locationservice.Activity.MainActivity;
import com.smartconnect.locationservice.Database.LocationDB;
import com.smartconnect.locationservice.Services.LocationService;
import com.smartconnect.locationsockets.Device.DeviceInfo;
import com.smartconnect.locationsockets.Service.JsonProcessing;
import com.smartconnect.locationsockets.Service.LocationtrackingService;
import com.smartconnect.locationsockets.Service.SocketService;
import com.smartconnect.locationtracker.R;

import org.json.JSONObject;

import java.util.HashMap;

import static com.smartconnect.locationservice.Services.LocationService.latitude;
import static com.smartconnect.locationservice.Services.LocationService.location;
import static com.smartconnect.locationservice.Services.LocationService.longitude;

public class First_Activity extends Activity {
    Context context;
    JSONObject jsonObject;
    final SocketService mSocketservice = new SocketService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_);

        LocationtrackingService.instance().Expermemnt(this);
        LocationService.instance().Expermemnt(this);

         LocationService.isGPSEnabled=true;

        // SocketLibraries
//



        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(SocketService.instance().isConnected == true){
//                    Toast.makeText(First_Activity.this,"Socket  connected",Toast.LENGTH_SHORT).show();
//                }else{
//                    Toast.makeText(First_Activity.this,"Socket not able connect",Toast.LENGTH_SHORT).show();
//                }
//                mSocketservice.stream.connect();
//                SocketService.instance().socketConnections();
//
//                HashMap<String, Object> customData = new HashMap<>();
//                if (LocationService.location != null) {
//                    //double lat=LocationService.location.getLatitude();
//                    String latitude = String.valueOf(LocationService.location.getLatitude());
//                    customData.put("latitude",latitude);
//                    customData.put("longitude",  String.valueOf(LocationService.location.getLongitude()));
//                    customData.put("time", String.valueOf(LocationService.location.getTime()));
//                    customData.put("accuracy", String.valueOf(LocationService.location.getAccuracy()));
//                    customData.put("altitude", String.valueOf(LocationService.location.getAltitude()));
//                    customData.put("speed", String.valueOf(LocationService.location.getSpeed()));
//                    customData.put("provider", String.valueOf(LocationService.location.getProvider()));
//                }
//
//                mSocketservice.stream.emit("Sending to server",customData);
//                LocationtrackingService.instance().start(First_Activity.this, customData, false);                } catch (Exception e) {

                try {
                    LocationtrackingService.instance().start(First_Activity.this, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }



              //  LocationtrackingService.instance().serviceStatus = true;


            }
        });
//handleIntent(getIntent());
    }

//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        handleIntent(intent);
//    }

//    private void handleIntent(Intent intent) {
//
//        HashMap<String, Object> customData = new HashMap<>();
//
//        if (LocationService.location != null) {
//        //double lat=LocationService.location.getLatitude();
//         String latitude = String.valueOf(LocationService.location.getLatitude());
//            customData.put("latitude",latitude);
//            customData.put("longitude",  String.valueOf(LocationService.location.getLongitude()));
//        customData.put("time", String.valueOf(LocationService.location.getTime()));
//        customData.put("accuracy", String.valueOf(LocationService.location.getAccuracy()));
//        customData.put("altitude", String.valueOf(LocationService.location.getAltitude()));
//        customData.put("speed", String.valueOf(LocationService.location.getSpeed()));
//        customData.put("provider", String.valueOf(LocationService.location.getProvider()));
//        }
//
//      //  customData.put("longitude", LocationService.location.getLongitude());
////        customData.put("time",LocationService.location.getTime());
////        customData.put("accuracy",LocationService.location.getAccuracy());
////        customData.put("altitude",LocationService.location.getAltitude());
////        customData.put("speed",LocationService.location.getSpeed());
////        customData.put("provider",LocationService.location.getProvider());
//
//        LocationtrackingService.instance().start(this, customData, false);
//        LocationtrackingService.instance().serviceStatus = true;
//
//    }
}




