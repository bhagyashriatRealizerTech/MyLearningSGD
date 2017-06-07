package com.realizer.schoolgeine.driver;

import android.*;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.realizer.schoolgeine.driver.Commons.OnTaskCompleted;
import com.realizer.schoolgeine.driver.asynctask.LocationChangeAsyncTaskPost;
import com.realizer.schoolgeine.driver.backend.DatabaseQueries;
import com.realizer.schoolgeine.driver.model.TrackModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by Win on 12/9/2015.
 */
public class ServiceLocationChange extends IntentService implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener, OnTaskCompleted {
    Double currentLatitude;
    Double currentLongitude;
    private static final int TWO_MINUTES = 30000;
    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    String username;
    Intent gpsTrackerIntent;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    SharedPreferences sharedpreferences;
    DatabaseQueries qr;
    ArrayList<TrackModel> locList;
    int count;
    Location BetterLocation;
    SimpleDateFormat df;
    public static final String TAG = RegistrationActivity.class.getSimpleName();

    public ServiceLocationChange() {
        super("ServiceLocationChange");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        qr = new DatabaseQueries(getApplicationContext());
        Toast.makeText(ServiceLocationChange.this, "Service Created", Toast.LENGTH_LONG).show();
    }

    private class BackgroundThread extends Thread {
        @Override
        public void run() {
            super.run();

            mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                    .addConnectionCallbacks(ServiceLocationChange.this)
                    .addOnConnectionFailedListener(ServiceLocationChange.this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();


            locList = new ArrayList<>();
            count = 0;

            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new AutoSyncServerDataTrack(), 1000 * 10, 1000 * 30);
        }
    }

    class AutoSyncServerDataTrack extends TimerTask {
        @Override
        public void run() {
            if (locList.size() > 0) {
                Log.d("Async", "ok");
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String cellNo = pref.getString("DriverUUID", "");
                username = pref.getString("UserID", "");
                Collections.reverse(locList);
                new LocationChangeAsyncTaskPost(ServiceLocationChange.this, username, locList, cellNo).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Toast.makeText(ServiceLocationChange.this, "service start", Toast.LENGTH_SHORT).show();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Toast.makeText(ServiceLocationChange.this, "Task performed in service", Toast.LENGTH_SHORT).show();

        qr = new DatabaseQueries(getApplicationContext());
        df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        showNotification();

        qr.insertTrackingInfo(df.format(new Date()), "Start", "Start");

        BackgroundThread background = new BackgroundThread();
        background.start();


        return START_STICKY;
    }


    private void showNotification() {
        NotificationManager notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification;
        Notification.Builder builder = new Notification.Builder(ServiceLocationChange.this);

        Intent notificationIntent = new Intent(ServiceLocationChange.this, ServiceLocationChange.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent intent =
                PendingIntent.getActivity(ServiceLocationChange.this, 0, notificationIntent, 0);

        builder.setAutoCancel(true);
        builder.setContentTitle("Track");
        builder.setContentText("Tracking is on");
        builder.setSmallIcon(R.drawable.genie_logo_action_bar);
        builder.setContentIntent(intent);
        builder.setNumber(100);
        builder.setOngoing(false);  //API level 16
        builder.setDefaults(Notification.DEFAULT_SOUND);
        builder.setDefaults(Notification.DEFAULT_VIBRATE);
        builder.build();

        notification = builder.getNotification();
        notificationManager.notify(0, notification);

        startForeground(101, notification);

    }



    @Override
    public void onDestroy() {
        Log.d("Test Service", "Stop");
        qr.insertTrackingInfo(df.format(new Date()), "Stop", "Stop");
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
        super.onDestroy();
    }


    @Override
    public void onLowMemory() {
        qr.insertTrackingInfo(df.format(new Date()), "Low Memory", "Low Memory");
        super.onLowMemory();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

       /* AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, intent.getIntExtra("Hour",0));
        calendar.set(Calendar.MINUTE, intent.getIntExtra("Minute", 0));
        Intent myIntent = new Intent(ServiceLocationChange.this, AlarmReceiver.class);
        myIntent.putExtra("Type","Stop");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ServiceLocationChange.this, 0, myIntent, 0);
        alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);*/
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Toast.makeText(ServiceLocationChange.this, "On Task Removed", Toast.LENGTH_SHORT).show();

        qr.insertTrackingInfo(df.format(new Date()), "Stop", "Stop");

        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 100,
                restartServicePendingIntent);

        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL_IN_MILLISECONDS)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);


        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        Toast.makeText(ServiceLocationChange.this, "On Connected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        //Toast.makeText(TestServicecLocation.this, "On Location Changed", Toast.LENGTH_SHORT).show();
        if(BetterLocation == null)
            BetterLocation = location;
        Toast.makeText(ServiceLocationChange.this, "On Location Changed", Toast.LENGTH_SHORT).show();

        Location tempBetterLocation = getBetterLocation(BetterLocation, location);
        if(tempBetterLocation == location) {
            if(BetterLocation != null && BetterLocation != tempBetterLocation) {
                BetterLocation = tempBetterLocation;
                float accuracy = location.getAccuracy();
                TrackModel obj = new TrackModel();
                String locDate = df.format(new Date());

                obj.setLocationTime(locDate);
                obj.setLati(String.valueOf(location.getLatitude()));
                obj.setLangi(String.valueOf(location.getLongitude()));
                locList.add(count, obj);
                count = count + 1;
                qr.insertTrackingInfo(locDate, String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));

                Toast.makeText(ServiceLocationChange.this, "Accuracy =>" + String.valueOf(accuracy), Toast.LENGTH_SHORT).show();
            }

            }
    }

    @Override
    public void onClick(View v) {

    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult((android.app.Activity) getApplicationContext(), CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onTaskCompleted(String s,ArrayList<TrackModel> obj) {

        if (s.toString().equalsIgnoreCase("true"))
        {
            locList = new ArrayList<>();
            count = 0;
            Toast.makeText(ServiceLocationChange.this, "Broadcast Successfully done!!!", Toast.LENGTH_SHORT).show();
            for(int i=0;i<obj.size();i++)
              qr.updateTimeTableSyncFlag(obj.get(i).getLocationTime(), obj.get(i).getLati(), obj.get(i).getLangi());
        }
        else {
            Toast.makeText(ServiceLocationChange.this, "Server not responding please wait...", Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
        }

    }


    public void initializeTimerTask(Double lat,Double lang) {

          if(lat != null && lang != null) {

         String locDate = df.format(new Date());
         qr.insertTrackingInfo(locDate,lat.toString(),lang.toString());

         Toast.makeText(getApplicationContext(), "Position got as Lat=> " + lat.toString() + " Lan=> " + lang, Toast.LENGTH_SHORT).show();

   }
     else
        Toast.makeText(getApplicationContext(), "Position got as Null", Toast.LENGTH_SHORT).show();

    }

    protected Location getBetterLocation(Location newLocation, Location currentBestLocation) {
       /* if (currentBestLocation == null) {
            // A new location is always better than no location
            return newLocation;
        }

        double distance = currentBestLocation.distanceTo(newLocation);

        if(distance>=16)
        {
            return  newLocation;
        }
        else
        {
            return currentBestLocation;
        }*/

        if (currentBestLocation == null) {
            // A new location is always better than no location
            return newLocation;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = newLocation.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved.
        if (isSignificantlyNewer) {
            return newLocation;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return currentBestLocation;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (newLocation.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;



        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return newLocation;
        } else if (isNewer && !isLessAccurate) {
            return newLocation;
        } else if (isNewer && !isSignificantlyLessAccurate ) {
            return newLocation;
        }
        return currentBestLocation;
    }

}
