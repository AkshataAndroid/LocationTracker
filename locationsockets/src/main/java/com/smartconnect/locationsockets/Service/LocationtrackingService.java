package com.smartconnect.locationsockets.Service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import java.util.HashMap;
import java.util.Map;

@SuppressLint("NewApi")
public final class LocationtrackingService implements ViewTreeObserver.OnWindowFocusChangeListener  {
    public static String pkgName = null;
    Context context;
    private Map<String, Object> m_customData;
    private static final LocationtrackingService m_instance = new LocationtrackingService();
    public static Boolean serviceStatus = false;
    public static boolean m_started = false;
    public static Activity m_currentActivity;


    public static LocationtrackingService instance() {
        return m_instance;
    }

    private void reinitialize() {
        Log.d("TAG", "message Yo yo yo");


        // this.m_device.runRegistrationLoop();
    }
    public Map<String, Object> customData() {
        return this.m_customData;
    }
    public LocationtrackingService start(Application application) {
      //  this.m_customData = customData;
        if (this.m_started) {
            return this;
        }
        this.m_started = true;

        context = application.getApplicationContext();
        //context.startService(new Intent(context, CobrowseService.class));
        Intent startServerIntent = new Intent(context, SocketService.class);
        startServerIntent.setAction("START");
        context.startService(startServerIntent);
        reinitialize();
        return this;
    }
    public LocationtrackingService start(Activity currentActivity, boolean b) {
       // this.m_customData = customData;
        this.m_started = b;
        //this.m_currentActivity = null;

        start(currentActivity.getApplication());
        setActivity(currentActivity);
        return this;
    }
    void setActivity(Activity activity) {
        if (this.m_currentActivity == activity) {
            return;
        }
        this.m_currentActivity = activity;
//        this.m_controlInjection.setActivity(activity);
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

    }

    public String api() {
        if (pkgName == null) {
            pkgName = context.getPackageName();
        }
        ApplicationInfo app = null;
        try {
            app = context.getPackageManager().getApplicationInfo(pkgName, PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Bundle bundle = app.metaData;

        String mData = bundle.getString("locationurl");

        return mData;
    }
    public void Expermemnt(Activity mainActivity) {
        //   Toast.makeText(mainActivity, "Hello there", Toast.LENGTH_SHORT).show();
    }

}
