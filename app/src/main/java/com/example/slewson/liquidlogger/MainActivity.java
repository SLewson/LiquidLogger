package com.example.slewson.liquidlogger;

import android.app.NotificationManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends ActionBarActivity implements LiquidLogAPI.LiquidLogApiCallback {

    private LiquidLogAPI liquidLogAPI;

    private TextView pH_textview = null;
    private TextView temp_textview = null;

    private Timer timer = null;
    private boolean notified = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        liquidLogAPI = new LiquidLogAPI(this);
        startCoffeeRefreshTimer();
        //liquidLogAPI.getCoffeeStatus();

        pH_textview = (TextView) findViewById(R.id.ph_value);
        temp_textview = (TextView) findViewById(R.id.temp_value);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startCoffeeRefreshTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Log.d("MainActivity", "Getting coffee status");
                liquidLogAPI.getCoffeeStatus();
            }
        }, 0, 1500);
    }

    private void displayCoffeeStatus(LiquidLogAPI.CoffeeStatus coffeeStatus) {
        String status = "Temp: " + coffeeStatus.getTemp() + ", pH: " + coffeeStatus.getpH();
        pH_textview.setText("" + coffeeStatus.getpH());
        temp_textview.setText("" + coffeeStatus.getTemp());

        Log.d("MainActivity", status);
    }

    private void displayNotification(String message) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setContentTitle("Liquid Logger")
                        .setContentText(message)
                        .setSmallIcon(R.drawable.coffee);

        // Sets an ID for the notification
        int mNotificationId = 001;
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }

    @Override
    public void onLiquidLogApiError(String error) {
        Log.d("MainActivity", "API Error: " + error);
    }

    @Override
    public void onLiquidLogApiStatusResponse(LiquidLogAPI.CoffeeStatus coffeeStatus) {
        displayCoffeeStatus(coffeeStatus);

        if (coffeeStatus.getpH() >= 4.8 && coffeeStatus.getpH() <= 5.2 && !notified) {
            displayNotification("Your coffee is ready!");
            notified = true;
        }
        else if (coffeeStatus.getpH() < 4.5) {
            Log.d("MainActivity", "Notify");
            displayNotification("Sorry, your coffee is ruined");
        }
    }
}
