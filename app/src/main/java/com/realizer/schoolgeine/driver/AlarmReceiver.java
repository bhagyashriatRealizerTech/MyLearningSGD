package com.realizer.schoolgeine.driver;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.realizer.schoolgeine.driver.Commons.Singlton;

public class AlarmReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        /*//this will update the UI with message

        //this will sound the alarm tone
        //this will sound the alarm once, if you wish to
        //raise alarm in loop continuously then use MediaPlayer and setLooping(true)
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        Ringtone ringtone = RingtoneManager.getRingtone(context, alarmUri);
        ringtone.play();

        //this will send a notification message
        ComponentName comp = new ComponentName(context.getPackageName(),
                AlarmService.class.getName());
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);*/
        if(intent.getStringExtra("Type").equalsIgnoreCase("Start")) {
            Intent serviceIntent = new Intent(context, ServiceLocationChange.class);
            serviceIntent.putExtra("Hour",intent.getIntExtra("Hour",0));
            serviceIntent.putExtra("Minute",intent.getIntExtra("Minute",0));
            Singlton.setAutoserviceIntent(serviceIntent);
            startWakefulService(context,serviceIntent);
        }
        if(intent.getStringExtra("Type").equalsIgnoreCase("Stop")) {

            if(Singlton.getAutoserviceIntent() != null)
                context.stopService(Singlton.getAutoserviceIntent());
        }


    }
}
