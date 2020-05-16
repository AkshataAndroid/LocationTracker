package com.smartconnect.locationsockets.Service;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;


import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.engineio.client.transports.WebSocket;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.smartconnect.locationservice.Services.LocationService;
import com.smartconnect.locationsockets.Activity.MainActivity;
import com.smartconnect.locationsockets.BuildConfig;
import com.smartconnect.locationsockets.Constants.APIConst;
import com.smartconnect.locationsockets.Constants.Const;
import com.smartconnect.locationsockets.Constants.UserPreferences;
import com.smartconnect.locationsockets.Device.DeviceInfo;
import com.smartconnect.locationsockets.Model.LoginDetails;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.smartconnect.locationsockets.Device.Device.DEVICE_TIME_ZONE;
import static com.smartconnect.locationsockets.Device.Device.DEVICE_VERSION;

public class SocketService extends Service {

    public static Socket stream = null;
    public Boolean isConnected = false;
    private static final SocketService m_instance = new SocketService();
    private long startTime = 0;
    private long endTime = 0;
    Long tsLong, diff, newtsLong;
    Context context;
    static  SocketService socketService;
    public static String longitude="abc", latitude="abc",accuracy, altitude, device_id, device_name,version_name;
    public static String apiUrl = null;
    public static String mLocationDetails = "android-mobile";
    public static SocketService instance() {
        return m_instance;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");

        context = getApplicationContext();
    }

    @Nullable

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        socketService =new SocketService();
        if (intent != null && intent.getAction().equals("START")) {
            Log.i("Location", "started Location service");
            String apiStatus = UserPreferences.getPreferences(getApplicationContext(), APIConst.API_URL);
            if (apiStatus == null || apiStatus=="") {
                apiUrl = LocationtrackingService.instance().api();
                UserPreferences.setPreferences(getApplicationContext(), APIConst.API_URL, apiUrl);
            } else {
                apiUrl = apiStatus;
            }
         //   attachLifecycleListeners();
            socketConnections();
        }

        return START_STICKY;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.i(TAG, "onStart");
    }


//    private void attachLifecycleListeners() {
//        getApplication().registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
//            public void onActivityCreated(Activity activity, Bundle bundle) {
//                Log.i("Conference", "activity created " + activity);
//            }
//
//            public void onActivityStarted(Activity activity) {
//                Log.i("Conference", "activity started " + activity);
//            }
//
//            public void onActivityResumed(Activity activity) {
//                Log.i("Conference", "activity resumed " + activity);
//                //     SocketService.this.setActivity(activity);
//            }
//
//            public void onActivityPaused(Activity activity) {
//                Log.i("Conference", "activity paused " + activity);
//                //       SocketService.this.clearActivity(activity);
//            }
//
//            public void onActivityStopped(Activity activity) {
//                Log.i("Conference", "activity stopped " + activity);
//                //         SocketService.this.clearActivity(activity);
//            }
//
//            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
//                Log.i("Conference", "activity save " + activity);
//            }
//
//            public void onActivityDestroyed(Activity activity) {
//                Log.i("Conference", "activity destroy " + activity);
//                //           SocketService.this.clearActivity(activity);
//            }
//        });
//    }

    public void socketConnections() {
        try {
            IO.Options opts = new IO.Options();
            opts.forceNew = false;
            opts.reconnection = true;
            opts.reconnectionDelay = 1000;
            opts.reconnectionDelayMax = 5000;
            opts.reconnectionAttempts = 99999;
            opts.transports = new String[]{WebSocket.NAME};
            stream = IO.socket(apiUrl, opts);

            Log.d("Socket started", stream.toString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        stream.on("mobilePong", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                newtsLong = System.currentTimeMillis();
                LoginDetails.LogD("tslong", String.valueOf(tsLong));
                LoginDetails.LogD("newtslong", String.valueOf(newtsLong));
                if (tsLong != null) {
                    diff = newtsLong - tsLong;
                } else {
                    diff = Long.valueOf(0);
                }
                if (diff > 1000) {
                    Log.d("Latency", " " + Math.floor(diff / 1000) + "sec");
                    if (diff > 2000) {
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {

                            @Override
                            public void run() {
                                // LogDetail.ToastR(getApplicationContext(), "Poor Connection");
                            }
                        });

                    }
                } else {
                    // LogDetail.LogR("Latency", " " + diff + "ms");
                }
            }
        });

        stream.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("Socket Connected", stream.toString());
                        if (!isConnected) {
                            if (null != mLocationDetails) {
//                                Map<String, Object> data = LocationtrackingService.instance().customData();
//
//                                if (data.containsKey("latitude")) {
//                                    latitude = data.get("latitude").toString();
//                                }
//                                if (data.containsKey("longitude")) {
//                                    longitude = data.get("longitude").toString();
//                                }
//                                if (data.containsKey("time")) {
//                                    longitude = data.get("time").toString();
//                                }
//                                if (data.containsKey("accuracy")) {
//                                    accuracy = data.get("accuracy").toString();
//                                }
//                                if (data.containsKey("altitude")) {
//                                    altitude = data.get("altitude").toString();
//                                }

                          //      LoginDetails.LogD("data", String.valueOf(data));

                                JSONObject appInfo = new JSONObject();
                                LoginDetails.LogD("latitude",  " : " + longitude);

                                try {
                                    //appInfo.put("platform", "android");
                                    appInfo.put("verticalAccuracy", "0");
                                    appInfo.put("horizontalAccuracy", "0");
                                    appInfo.put("appBuild", BuildConfig.VERSION_CODE);

                                    appInfo.put("timeZone", DeviceInfo.getDeviceInfo(getApplicationContext(), DEVICE_TIME_ZONE));
//                                    appInfo.put("latitude", String.valueOf(LocationService.location.getLatitude()));
//                                    appInfo.put("longitude", LocationService.location.getLongitude());
//
//                                    appInfo.put("time",LocationService.location.getTime());
//                                    appInfo.put("accuracy",LocationService.location.getAccuracy());
//                                    appInfo.put("altitude",LocationService.location.getAltitude());
//                                    appInfo.put("speed",LocationService.location.getSpeed());
//                                    appInfo.put("time",LocationService.location.getProvider());
                                    appInfo.put("appVersion", BuildConfig.VERSION_NAME);
                                    appInfo.put("appName", BuildConfig.APPLICATION_ID);
                                    appInfo.put("rtt", "");
                                    appInfo.put("imei", device_id);



                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                LoginDetails.LogD("appDetail", String.valueOf(appInfo));

                               // stream.emit("addMobile", mLocationDetails,"",  appInfo, "android");
                                stream.emit("",mLocationDetails,appInfo);
                            }
                            sendToServer();
                            isConnected = true;
                        }
                    }
                });
            }
        });



        stream.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d("Socket disconnected", stream.toString());

                isConnected = false;
            }
        });

        stream.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d("Socket not able connect", stream.toString());


            }
        });

        stream.on(Socket.EVENT_CONNECT_TIMEOUT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d("Socket connect timeout", stream.toString());


            }
        });

        stream.on("response", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject maindata = (JSONObject) args[0];
                try {
                    if (maindata.getString("eventName").equals("pongAlive")) {
                        endTime = System.currentTimeMillis();
                        long timeDiff = startTime - endTime;

                        Log.d("timeDifference ", String.valueOf(timeDiff));
                        if (timeDiff > 500) {
                            Toast.makeText(getApplication().getApplicationContext(), timeDiff + "ms", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        JsonProcessing js = new JsonProcessing();
                        js.processJsonResponse(getApplicationContext(), maindata);
                        Log.d("jsonresp", maindata.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
               /* JSONObject maindata = (JSONObject) args[0];
                JsonProcessing js = new JsonProcessing();
                js.processJsonResponse(getApplicationContext(), maindata);
                Log.d("jsonresp", maindata.toString());*/

            }
        });


        stream.connect();
        Intent myIntent = new Intent(this, MainActivity.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(myIntent );
    }

    public void sendToServer() {
        //  activeScreen = LoginActivity.class.getSimpleName();

//        Timer t = new Timer();
//        //Set the schedule function and rate
//        t.scheduleAtFixedRate(new TimerTask() {
//                                  @Override
//                                  public void run() {
//                                      //Called each time when 3000 milliseconds (3 second) (the period parameter)
//                                      try {
//                                          startTime = System.currentTimeMillis();
//                                          JSONObject newObject = new JSONObject();
//                                          newObject.put("udid", DeviceInfo.getDeviceId(context));
//                                          JSONObject finalObject = new JSONObject();
//                                          finalObject.put("eventName", "pingAlive");
//                                          finalObject.put("data", newObject);
//                                          Log.d("FinalOBJ", finalObject.toString());
//                                          stream.emit("request", finalObject);
//                                      } catch (JSONException e) {
//                                          e.printStackTrace();
//                                      }
//                                  }
//                              },
//                //Set how long before to start calling the TimerTask (in milliseconds)
//                0,
//                //Set the amount of time between each execution (in milliseconds)
//                30000);
 try {

            JSONObject finalObject = new JSONObject();
            finalObject.put("eventName", "pingAlive");

            Log.d("FinalOBJ", finalObject.toString());
            stream.emit("latitude", finalObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setActivity(Activity activity) {
        LocationtrackingService.instance().setActivity(activity);
    }


}
