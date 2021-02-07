package com.example.android.arkanoid;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final int splashTimeOut = 2000;

        Thread splashThread = new Thread(){
            int wait = 0;
            @Override
            public void run() {
                try {
                    super.run();
                    while(wait < splashTimeOut){
                        sleep(100);
                        wait += 100;
                    }
                } catch (Exception e) {
                }finally{
                    startActivity(new Intent(SplashActivity.this,LoginActivity.class));
                    finish();
                }
            }
        };
        splashThread.start();

    }
}

