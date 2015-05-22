package com.example.slewson.liquidlogger;

import android.app.NotificationManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.ChartData;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Created by Marie on 5/21/2015.
 */
public class LiveFragment extends Fragment implements LiquidLogAPI.LiquidLogApiCallback{
    private LiquidLogAPI liquidLogAPI;

    private TextView pH_textview = null;
    private TextView temp_textview = null;

    private LineChartView lineChartView = null;

    private Timer timer = null;
    private boolean notified = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get the view from live_layout.xml
        View view = inflater.inflate(R.layout.live_layout, container, false);
        liquidLogAPI = new LiquidLogAPI(this);
        startCoffeeRefreshTimer();
        //liquidLogAPI.getCoffeeStatus();

        pH_textview = (TextView) view.findViewById(R.id.ph_value);
        temp_textview = (TextView) view.findViewById(R.id.temp_value);

        lineChartView = (LineChartView) view.findViewById(R.id.line_chart);

        initChart();
        return view;
    }

    private void initChart() {
        List<PointValue> values = new ArrayList<>();
        values.add(new PointValue(0.0f, 2.0f));
        values.add(new PointValue(1.5f, 1.5f));
        values.add(new PointValue(2.7f, 3.7f));
        values.add(new PointValue(3.2f, 4.2f));

        //In most cased you can call data model methods in builder-pattern-like manner.
        Line line = new Line(values).setColor(Color.BLUE).setCubic(true);
        line.setStrokeWidth(3);
        List<Line> lines = new ArrayList<>();
        lines.add(line);

        LineChartData data = new LineChartData();
        data.setLines(lines);

        Axis x = Axis.generateAxisFromRange(-4f, 4f, 0.25f);
        x.setName("Time");

        Axis y = new Axis();
        y.setName("pH");
        y.setHasSeparationLine(true);
        y.setHasLines(true);

        data.setAxisXBottom(x);
        data.setAxisYLeft(y);

        lineChartView.setLineChartData(data);
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
                new NotificationCompat.Builder(getActivity())
                        .setContentTitle("Liquid Logger")
                        .setContentText(message)
                        .setSmallIcon(R.drawable.coffee);

        // Sets an ID for the notification
        int mNotificationId = 001;
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr = (NotificationManager) getActivity().getSystemService(getActivity().NOTIFICATION_SERVICE);
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
