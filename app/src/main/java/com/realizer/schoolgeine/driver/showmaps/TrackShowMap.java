package com.realizer.schoolgeine.driver.showmaps;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.realizer.schoolgeine.driver.Commons.OnTaskCompleted;
import com.realizer.schoolgeine.driver.Commons.Utils;
import com.realizer.schoolgeine.driver.R;
import com.realizer.schoolgeine.driver.backend.DatabaseQueries;
import com.realizer.schoolgeine.driver.model.TrackModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;

/**
 * Created by shree on 10/29/2015.
 */
public class TrackShowMap extends AppCompatActivity implements OnTaskCompleted {
    private GoogleMap mMap;
    String Latitude;
    String Longitude;
    Marker mMarker;
    private PolylineOptions mPolylineOptions;
    String Username,Password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_activity);
        getSupportActionBar().setTitle(Utils.actionBarTitle("Map",TrackShowMap.this));
        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Username = sharedpreferences.getString("UserID","");
        Password = sharedpreferences.getString("DriverUUID","");
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new AutoSyncServerDataTrack(), 1000, 1000*120);
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        ArrayList<TrackModel> tempList = new DatabaseQueries(TrackShowMap.this).getAllLocData();
        String tempLat = null;
        String tempLang=null;
        for(int i=0;i<tempList.size();i++)
        {
            if(!tempList.get(i).getLati().equalsIgnoreCase("Start") && !tempList.get(i).getLati().equalsIgnoreCase("Stop"))
            {
                tempLat = tempList.get(i).getLati();
                tempLang = tempList.get(i).getLangi();
                break;
            }
        }
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                if(tempLang != null && tempLat != null)
                setUpMap(tempLat, tempLang);
            }
        }
    }

    private void setUpMap(String lati, String longi) {

       /* if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }*/
        mMap.setMyLocationEnabled(true);
       // mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
         mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID );
        // mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN );
        // mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        String currentLatitude =lati;
        String currentLongitude =longi;

        LatLng latLng = new LatLng(Double.parseDouble(currentLatitude), Double.parseDouble(currentLongitude));

        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title(Username + " is here!")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
        mMarker = mMap.addMarker(options);
        mMarker.setPosition(latLng);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18.0f));
       // mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mPolylineOptions = new PolylineOptions();
        mPolylineOptions.color(Color.RED).width(7);

    }

    @Override
    public void onTaskCompleted(String s,ArrayList<TrackModel>trackobj) {

        String dailyDriverList = s;
        if(s.equals(","))
        {
            Toast.makeText(getApplicationContext(), "Server Not Responding ", Toast.LENGTH_SHORT).show();
        }
        else {
            try {
                JSONArray locList = new JSONArray(s);
                for(int i=locList.length()-1;i>=0;i--) {
                    JSONObject obj = locList.getJSONObject(i);
                    LatLng latLng = new LatLng(Double.parseDouble(obj.getString("latitude")), Double.parseDouble(obj.getString("longitude")));
                    animateMarker(mMarker, latLng, false);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    public void animateMarker(final Marker marker, final LatLng toPosition,
                              final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;

               // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng),16.0f));
                marker.setPosition(new LatLng(lat, lng));
                mMap.addPolyline(mPolylineOptions.add(new LatLng(lat, lng)));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }
    class AutoSyncServerDataTrack extends TimerTask
    {
        @Override
        public void run() {

            TrackAsync();
        }
    }
    public void TrackAsync()
    {
         new TrackingAsyncTaskAuto(TrackShowMap.this,Username,Password,TrackShowMap.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }
}
