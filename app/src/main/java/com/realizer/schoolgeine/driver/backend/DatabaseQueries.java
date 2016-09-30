package com.realizer.schoolgeine.driver.backend;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.realizer.schoolgeine.driver.model.DriverInfo;
import com.realizer.schoolgeine.driver.model.TrackModel;

import java.util.ArrayList;

/**
 * Created by Bhagyashri on 9/22/2016.
 */
public class DatabaseQueries {

    SQLiteDatabase db;
    Context context;
    String scode;

    public DatabaseQueries(Context context) {

        this.context = context;
        SQLiteOpenHelper myHelper = SqliteHelper.getInstance(context);
        this.db = myHelper.getWritableDatabase();

    }

    //Insert Student Information
    public long insertTrackingInfo(String locationTime,String lati,String langi)
    {
        deleteTable();
        ContentValues conV = new ContentValues();
        conV.put("BroadCastTime", locationTime);
        conV.put("Latitude", lati);
        conV.put("Longitude", langi);
        conV.put("HasBroadCase", "false");
        long newRowInserted = db.insert("TrackInfo", null, conV);

        return newRowInserted;
    }

    public long updateTimeTableSyncFlag(String locationTime,String lati,String langi) {
        ContentValues conV = new ContentValues();
        conV.put("BroadCastTime", locationTime);
        conV.put("Latitude", lati);
        conV.put("Longitude", langi);
        conV.put("HasBroadCase", "true");
        long newRowUpdate = db.update("TrackInfo", conV, "BroadCastTime= '" + locationTime + "'", null);


        return newRowUpdate;
    }

    public ArrayList<TrackModel> getAllLocData() {
        Cursor c = db.rawQuery("SELECT * FROM TrackInfo", null);
        ArrayList<TrackModel> result = new ArrayList<>();

        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {
                    TrackModel obj = new TrackModel();
                    obj.setLocationTime(c.getString(c.getColumnIndex("BroadCastTime")));
                    obj.setLati(c.getString(c.getColumnIndex("Latitude")));
                    obj.setLangi(c.getString(c.getColumnIndex("Longitude")));
                    obj.setHasBroadcast(c.getString(c.getColumnIndex("HasBroadCase")));
                    result.add(obj);
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return result;
    }


    // ======== Drver Information ======= //

    //Insert Student Information
    public long insertDriverInfo(DriverInfo obj)
    {
        //deleteTable();
        ContentValues conV = new ContentValues();
        conV.put("DriverUUID", obj.getDriverUUID());
        conV.put("FName", obj.getFName());
        conV.put("SchoolCode", obj.getSchoolode());
        conV.put("MobileNo", obj.getMobileNo());
        conV.put("Address", obj.getAddress());
        conV.put("VehicleNo", obj.getVehicleNo());
        conV.put("StartTime", obj.getStartTime());
        conV.put("EndTime", obj.getEndTime());
        conV.put("AltMobNo", obj.getAlternateNo());
        conV.put("UserId", obj.getUserID());
        long newRowInserted = db.insert("DriverInfo", null, conV);

        return newRowInserted;
    }

    public long updateDriverInfo(DriverInfo obj) {
        ContentValues conV = new ContentValues();
        conV.put("DriverUUID", obj.getDriverUUID());
        conV.put("FName", obj.getFName());
        conV.put("SchoolCode", obj.getSchoolode());
        conV.put("MobileNo", obj.getMobileNo());
        conV.put("Address", obj.getAddress());
        conV.put("VehicleNo", obj.getVehicleNo());
        conV.put("StartTime", obj.getStartTime());
        conV.put("EndTime", obj.getEndTime());
        conV.put("AltMobNo", obj.getAlternateNo());
        conV.put("UserId", obj.getUserID());
        long newRowUpdate = db.update("DriverInfo", conV, "DriverUUID= '" + obj.getDriverUUID() + "'", null);


        return newRowUpdate;
    }

    public ArrayList<DriverInfo> getDriverInfo() {
        Cursor c = db.rawQuery("SELECT * FROM DriverInfo ORDER BY SrNo DESC", null);
        ArrayList<DriverInfo> result = new ArrayList<>();

        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {
                    DriverInfo obj = new DriverInfo();
                    obj.setSrNo(c.getInt(c.getColumnIndex("SrNo")));
                    obj.setDriverUUID(c.getString(c.getColumnIndex("DriverUUID")));
                    obj.setFName(c.getString(c.getColumnIndex("FName")));
                    obj.setSchoolode(c.getString(c.getColumnIndex("SchoolCode")));
                    obj.setMobileNo(c.getString(c.getColumnIndex("MobileNo")));
                    obj.setAddress(c.getString(c.getColumnIndex("Address")));
                    obj.setVehicleNo(c.getString(c.getColumnIndex("VehicleNo")));
                    obj.setStartTime(c.getString(c.getColumnIndex("StartTime")));
                    obj.setEndTime(c.getString(c.getColumnIndex("EndTime")));
                    obj.setAlternateNo(c.getString(c.getColumnIndex("AltMobNo")));
                    obj.setUserID(c.getString(c.getColumnIndex("UserId")));
                    result.add(obj);
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return result;
    }

    public void deleteTable()
    {
        db.delete("TrackInfo","TrackId NOT IN (SELECT TrackId FROM TrackInfo ORDER BY TrackId DESC LIMIT 19)",null);

    }
}

