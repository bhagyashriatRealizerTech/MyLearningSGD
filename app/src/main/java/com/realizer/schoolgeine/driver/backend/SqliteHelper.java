package com.realizer.schoolgeine.driver.backend;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * Created by Win on 12/21/2015.
 */
public class SqliteHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "SchoolgenieDriver";
    private static final int DATABASE_VERSION = 5;
    static Context mycontext;
    private static SqliteHelper mInstance = null;
    private static final String DRIVERINFO ="CREATE TABLE DriverInfo(SrNo INTEGER PRIMARY KEY   AUTOINCREMENT,DriverUUID TEXT,FName TEXT ,SchoolCode TEXT,MobileNo TEXT,Address TEXT,VehicleNo TEXT,StartTime TEXT,EndTime TEXT,AltMobNo TEXT,UserId TEXT)";

    private static final String TRACKINFO ="CREATE TABLE TrackInfo(TrackId INTEGER PRIMARY KEY   AUTOINCREMENT, BroadCastTime TEXT,Latitude TEXT,Longitude TEXT,HasBroadCase TEXT)";



    private SqliteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mycontext = context;
    }

    public static SqliteHelper getInstance(Context ctx) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (mInstance == null) {
            mInstance = new SqliteHelper(ctx.getApplicationContext());
        }
        mycontext = ctx;
        return mInstance;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(DRIVERINFO);
        db.execSQL(TRACKINFO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

        db.execSQL("DROP TABLE if exists DriverInfo");
        db.execSQL("DROP TABLE if exists TrackInfo");
        onCreate(db);
    }
}
