package com.newday.chaminc;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.todolist.mynewday.R;

import gr.net.maroulis.library.EasySplashScreen;

public class Splash extends Activity {

    private final int SPLASH_DISPLAY_LENGTH = 1000;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        EasySplashScreen config = new EasySplashScreen(Splash.this)
                .withTargetActivity(MainActivity.class)
                .withSplashTimeOut(600);
//                .withBackgroundColor(getColor(R.color.colorPrimaryDark))
//                .withLogo(R.mipmap.ic_launcher_round);
//        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_in);

        View easySplash = config.create();
        setContentView(easySplash);
    }
}