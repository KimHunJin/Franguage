package com.franguage.admin.test01.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.franguage.admin.test01.MainActivity;
import com.franguage.admin.test01.R;
import com.franguage.admin.test01.Utils.Utils;

/**
 * Created by Admin on 2015-08-19.
 */
public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        Thread mTimer = new Thread() {
            @Override
            public void run() {

                String is_auto = Utils.getAppPreferences(SplashActivity.this, "is_auto");

                try {
                    sleep(3000);
                    if(is_auto.equals("1")) {
                        Intent it = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(it);
                        finish();
                    } else {
                        Intent mIntro = new Intent(SplashActivity.this, IntroActivity.class);
                        startActivity(mIntro);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        mTimer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}