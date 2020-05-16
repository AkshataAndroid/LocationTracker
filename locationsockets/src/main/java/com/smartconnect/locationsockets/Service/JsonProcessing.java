package com.smartconnect.locationsockets.Service;

import android.content.Context;
import android.util.Log;


import com.smartconnect.locationsockets.Activity.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import static com.smartconnect.locationsockets.Constants.APIConst.EVENT_LISTING_TABLETS;
import static com.smartconnect.locationsockets.Constants.APIConst.EVENT_LOGIN_FAILED;
import static com.smartconnect.locationsockets.Constants.APIConst.EVENT_LOGIN_SUCCESS;
import static com.smartconnect.locationsockets.Constants.APIConst.EVENT_TABLETS_ALREADY_EXIST;
import static com.smartconnect.locationsockets.Constants.APIConst.EVENT_TABLETS_REGISTERED;
import static com.smartconnect.locationsockets.Constants.APIConst.EVENT_TABLETS_REGISTRATION_UNSUCCESSFUL;


public class JsonProcessing {
    private Context context;

    public void processJsonResponse(Context context, JSONObject maindata) {
        this.context = context;
        try {
            String eventName = maindata.getString("eventName");
            Log.d("eventName", eventName);
            Log.d("maindata", maindata.toString());
            navigateToScreen(eventName, maindata);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void navigateToScreen(String eventName, JSONObject receivedData) throws JSONException {
        Log.d("Screen", "Unhandled event...............!" + eventName);
        switch (eventName) {
            case EVENT_LOGIN_SUCCESS:
            case EVENT_LOGIN_FAILED:
                Log.d("LOgin Activity", "Login");
                if (BaseActivity.activeScreen.equalsIgnoreCase("LoginActivity")) {

                }
                break;
            case EVENT_LISTING_TABLETS:
                Log.d("Main Activity", "Tablet Listing");
                if (BaseActivity.activeScreen.equalsIgnoreCase("MainActivity")) {

                }
                break;
            case EVENT_TABLETS_REGISTERED:
            case EVENT_TABLETS_ALREADY_EXIST:
            case EVENT_TABLETS_REGISTRATION_UNSUCCESSFUL:
                Log.d("Data Activity", "Tablet Addition");
                if (BaseActivity.activeScreen.equalsIgnoreCase("DataActivity")) {

                }
                break;
            default:
                Log.i("Screen", "Unhanpdled event json p...............!" + eventName);
                break;
        }
    }
}
