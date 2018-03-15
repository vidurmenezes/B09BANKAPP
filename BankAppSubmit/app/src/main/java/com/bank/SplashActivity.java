package com.bank;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by vidur on 2017-07-27.
 */
public class SplashActivity extends AppCompatActivity {
  private Handler handler;
  private Runnable delayRunnable;

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    new Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        // This method will be executed once the timer is over
        // Start your app main activity
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
      }
    }, 3000);
  }
}
