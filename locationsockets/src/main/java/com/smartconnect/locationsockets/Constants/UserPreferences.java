package com.smartconnect.locationsockets.Constants;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.smartconnect.locationsockets.Activity.BaseActivity;


public class UserPreferences {

    public static final String PREF_NAME_MAIN = "ConferenceAdminSharedP";

    public static void setPreferences(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME_MAIN, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putString(key, value);
        prefsEditor.apply();
    }

    public static String getPreferences(Context context, String key) {
        String LP = "";
        SharedPreferences appSharedPrefs = context.getSharedPreferences(PREF_NAME_MAIN, Context.MODE_PRIVATE);
        if (appSharedPrefs != null) {
            return appSharedPrefs.getString(key, LP);
        } else {
            Intent intent = new Intent(context, BaseActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
            return LP;
        }
    }
}
