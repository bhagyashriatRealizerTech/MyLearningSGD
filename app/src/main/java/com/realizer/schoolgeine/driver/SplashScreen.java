package com.realizer.schoolgeine.driver;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.google.android.gms.nearby.messages.internal.RegisterStatusCallbackRequest;

/**
 * Created by Bhagyashri on 4/5/2016.
 */
public class SplashScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash_screen);

        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(3000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    boolean isLogin = pref.getBoolean("Login", false);
                    Intent intent;

                    if(isLogin)
                     intent = new Intent(SplashScreen.this,MainActivity.class);
                    else
                     intent = new Intent(SplashScreen.this,RegistrationActivity.class);
                    startActivity(intent);
                }
            }
        };
        timerThread.start();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }
}
