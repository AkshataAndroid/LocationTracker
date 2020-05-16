package com.smartconnect.locationservice.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public  class LocationDB extends SQLiteOpenHelper{


    private static String DBNAME = "locationmarkersqlite.db";
    private static int VERSION = 7;
    public static final String ROW_ID = "_id";
    public static final String LAT = "lat";
    public static final String LNG = "lng";
    public static final String TIME = "time";
    public static final String DATE = "date";
    public static final String ALTITUDE = "altitude";
    public static final String ACCURACY = "accuracy";
    public static final String SPEED = "speed";
    public static final String PROVIDER = "provider";
    public static final String GPSPROVIDER = "gpsprovider";
    public static final String NETWORKPROVIDER = "networkprovider";
    public static final String PASSIVEPROVIDER = "passiveprovider";
    private static final String DATABASE_TABLE = "locations";

    private SQLiteDatabase mDB;

    public LocationDB(Context context) {
        super(context, DBNAME, null, VERSION);
        this.mDB = getWritableDatabase();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table " + DATABASE_TABLE + " ( " +
                ROW_ID + " integer primary key autoincrement , " +
                LNG + " double , " +
                LAT + " double , "+
                TIME + " time , " +
                DATE + " date , " +
                ACCURACY + " double , " +
                SPEED + " double , " +
                ALTITUDE + " double , " +
                GPSPROVIDER + " boolean , " +
                NETWORKPROVIDER + " boolean , " +
                PASSIVEPROVIDER + " boolean , " +
                PROVIDER + " text " +
                " ) ";

        db.execSQL(sql);
    }

    public long insert(  String lat,String lng,String time,String date,String altitude,String accuracy,String speed,String provider,String gps,String network,String passive){

        ContentValues contentValue = new ContentValues();
        contentValue.put(LAT, lat);
        contentValue.put(LNG, lng);
        contentValue.put(TIME, time);
        contentValue.put(DATE, date);
        contentValue.put(ACCURACY, accuracy);
        contentValue.put(ALTITUDE, altitude);
        contentValue.put(SPEED, speed);
        contentValue.put(PROVIDER, provider);
        contentValue.put(GPSPROVIDER, gps);
        contentValue.put(NETWORKPROVIDER, network);
        contentValue.put(PASSIVEPROVIDER, passive);
        long row= mDB.insert(DATABASE_TABLE, null, contentValue);
        return row;

    }



    public int del(){
        int cnt = mDB.delete(DATABASE_TABLE, null , null);
        return cnt;
    }

    public Cursor getAllLocations(){
        return mDB.query(DATABASE_TABLE, new String[] { ROW_ID, LAT , LNG} , null, null, null, null, null);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}