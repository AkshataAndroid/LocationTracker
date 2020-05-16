package com.smartconnect.locationsockets.Model;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class LoginDetails
{
    private Data data;

    private String eventName;

    public Data getData ()
    {
        return data;
    }

    public void setData (Data data)
    {
        this.data = data;
    }

    public String getEventName ()
    {
        return eventName;
    }

    public void setEventName (String eventName)
    {
        this.eventName = eventName;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [data = "+data+", eventName = "+eventName+"]";
    }
    private static boolean debugger = true;

    public static void LogD(String T, String S) {
        if (debugger) {
            if (S != null) {
                Log.d(T, S);
            }
        }
    }

    public static void LogR(String T, String S) {
        if (S != null) {
            Log.d(T, S);
        }
    }

    public static void ToastD(Context T, String S) {
        if (debugger) {
            Toast.makeText(T, S, Toast.LENGTH_SHORT).show();
        }
    }

    public static void ToastR(Context T, String S) {
        Toast.makeText(T, S, Toast.LENGTH_SHORT).show();
    }
}