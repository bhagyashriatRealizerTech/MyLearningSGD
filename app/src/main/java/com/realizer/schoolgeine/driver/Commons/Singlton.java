package com.realizer.schoolgeine.driver.Commons;

import android.content.Intent;

/**
 * Created by Bhagyashri on 9/22/2016.
 */
public class Singlton {

    private static Singlton _instance;
    public static Intent autoserviceIntent = null;
    public static String dbName="";
    private Singlton()
    {

    }

    public static Singlton getInstance()
    {
        if (_instance == null)
        {
            _instance = new Singlton();
        }
        return _instance;
    }

    public static Intent getAutoserviceIntent() {
        return autoserviceIntent;
    }

    public static void setAutoserviceIntent(Intent autoserviceIntent) {
        Singlton.autoserviceIntent = autoserviceIntent;
    }

    public static String getDbName() {
        return dbName;
    }

    public static void setDbName(String dbName) {
        Singlton.dbName = dbName;
    }
}
