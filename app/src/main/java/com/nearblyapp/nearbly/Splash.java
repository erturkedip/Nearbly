package com.nearblyapp.nearbly;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Toast;


public class Splash extends Activity {

    SharedPreferences prefs;
    private static final String PREFS = "prefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);

        final Thread timer = new Thread(){
          public void run(){
              try {
                  sleep(3000);
              }catch (InterruptedException e){

              }finally {
                  if(prefs.getBoolean("isFirstTime", true)) {
                      startActivity(new Intent(Splash.this, WelcomeScreen.class));
                      overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                      finish();
                  } else {
                      startActivity(new Intent(Splash.this, MainActivity.class));
                      overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                      finish();
                  }
              }
          }
        };
        timer.start();
    }


}
