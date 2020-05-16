package com.smartconnect.locationsockets.Handler;

import org.json.JSONObject;

public interface EventHandler {

    public  void onEventReceived(final String eventName, final JSONObject receivedData);

    public void onEventReceived(final String eventName, final String receivedData);
}
