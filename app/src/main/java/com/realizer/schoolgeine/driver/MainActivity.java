package com.realizer.schoolgeine.driver;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.realizer.schoolgeine.driver.Commons.Singlton;
import com.realizer.schoolgeine.driver.Commons.Utils;
import com.realizer.schoolgeine.driver.backend.DatabaseQueries;
import com.realizer.schoolgeine.driver.model.DriverInfo;
import com.realizer.schoolgeine.driver.showmaps.TrackShowMap;

import org.w3c.dom.Text;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Bhagyashri on 9/27/2016.
 */
public class MainActivity extends AppCompatActivity {

    TextView profile,map,list;
    AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    DatabaseQueries qr;
    ImageButton start_stop;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        initiateView();
        //setTrackingTime();

    }

    public void setTrackingTime()
    {
        ArrayList<DriverInfo> driverInfo = qr.getDriverInfo();
        String startTime[] = driverInfo.get(driverInfo.size()-1).getStartTime().split(":");
        String endTime[] = driverInfo.get(driverInfo.size()-1).getEndTime().split(":");

        int startH = Integer.valueOf(startTime[0]);
        int startM = Integer.valueOf(startTime[1]);

        int endH = Integer.valueOf(endTime[0]);
        int endM = Integer.valueOf(endTime[1]);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, startH);
        calendar.set(Calendar.MINUTE, startM);
        Intent myIntent = new Intent(MainActivity.this, AlarmReceiver.class);
        myIntent.putExtra("Type", "Start");
        myIntent.putExtra("Hour", endH);
        myIntent.putExtra("Minute", endM);
        pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, myIntent, 0);
        alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);

       /* calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, endH);
        calendar.set(Calendar.MINUTE, endM);
        myIntent = new Intent(MainActivity.this, AlarmReceiver.class);
        myIntent.putExtra("Type","Stop");
        pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, myIntent, 0);
        alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);*/

    }
    public void initiateView()
    {
        profile = (TextView) findViewById(R.id.txtProfile);
        map = (TextView) findViewById(R.id.txtMap);
        list = (TextView)findViewById(R.id.txtList);
        start_stop = (ImageButton) findViewById(R.id.iv_button_start_stop);
        Typeface face= Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/font.ttf");
        profile.setTypeface(face);
        map.setTypeface(face);
        list.setTypeface(face);

        qr = new DatabaseQueries(MainActivity.this);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        getSupportActionBar().setTitle(Utils.actionBarTitle("Dashboard", MainActivity.this));

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TrackShowMap.class);
                startActivity(intent);
            }
        });


        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ShowListActivity.class);
                startActivity(intent);
            }
        });

        start_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                SharedPreferences.Editor edit = sharedpreferences.edit();
                if(!sharedpreferences.getBoolean("IsStart",false))
                {
                    start_stop.setImageResource(R.drawable.stop_button);
                    edit.putBoolean("IsStart", true);
                    edit.commit();
                    Intent intent = new Intent(MainActivity.this,ServiceLocationChange.class);
                    Singlton.setAutoserviceIntent(intent);
                    startService(intent);
                }
                else
                {
                    start_stop.setImageResource(R.drawable.start_button);
                    edit.putBoolean("IsStart",false);
                    edit.commit();
                    if(Singlton.getAutoserviceIntent() != null)
                    stopService(Singlton.getAutoserviceIntent());
                }
            }
        });
    }
}
