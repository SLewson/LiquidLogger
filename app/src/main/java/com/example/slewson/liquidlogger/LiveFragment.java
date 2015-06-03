package com.example.slewson.liquidlogger;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.slewson.liquidlogger.model.ParseManager;
import com.example.slewson.liquidlogger.model.RecipeObject;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import lecho.lib.hellocharts.formatter.SimpleAxisValueFormatter;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Created by Marie on 5/21/2015.
 */
public class LiveFragment extends Fragment implements LiquidLogAPI.LiquidLogApiCallback{
    private LiquidLogAPI liquidLogAPI;
    private RecipeObject recipe = null;

    private TextView recipeName_textview = null;
    private TextView goalpH_textview = null;
    private TextView goalTemp_textview = null;
    private TextView pH_textview = null;
    private TextView temp_textview = null;
    private TextView time_textview = null;
    private Button start_button = null;
    private Button current_button = null;
    private ProgressBar progress_bar_view = null;

    private LineChartView lineChartView = null;
    private List<PointValue> pHvalues = null;
    private List<PointValue> tempValues = null;
    private float scale = 14.0f / 220.0f;
    private float offset = (0.0f * scale) / 2;

    private Timer timer = null;
    private boolean notified = false;

    private boolean inProgress = false;
    private boolean phComplete = false;
    private boolean tempComplete = false;

    private float fakeTimer = 0;
    private double fakepH = 7.0;
    private double fakeTemp = 32.0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get the view from live_layout.xml
        View view = inflater.inflate(R.layout.live_layout, container, false);
        liquidLogAPI = new LiquidLogAPI(this);
        // TODO: Use real recipes
        recipe = new RecipeObject("DEFAULT", 3.0, 50.0, "", "");

        startCoffeeRefreshTimer();

        recipeName_textview = (TextView) view.findViewById(R.id.current_recipe_text);
        goalpH_textview = (TextView) view.findViewById(R.id.goal_ph_text);
        goalTemp_textview = (TextView) view.findViewById(R.id.goal_temp_text);
        pH_textview = (TextView) view.findViewById(R.id.pH_text);
        temp_textview = (TextView) view.findViewById(R.id.temp_text);
        time_textview = (TextView) view.findViewById(R.id.time_text);
        start_button = (Button) view.findViewById(R.id.start_button);
        current_button = (Button) view.findViewById(R.id.current_button);
        progress_bar_view = (ProgressBar) view.findViewById(R.id.progress_bar);
        initViews();

        lineChartView = (LineChartView) view.findViewById(R.id.line_chart);
        pHvalues = new ArrayList<>();
        tempValues = new ArrayList<>();

        generateData();
        return view;
    }

    private void initViews() {
        recipeName_textview.setText(recipe.getName());
        goalpH_textview.setText(recipe.getpH().toString());
        goalTemp_textview.setText(recipe.getTemp().toString());

        start_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (inProgress) {
                    stopLogger();
                } else {
                    startLogger();
                }
            }
        });

        current_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                selectCurrentRecipe();
            }
        });
    }

    private void stopLogger() {
        inProgress = false;
    }

    private void startLogger() {
        reset();
        inProgress = true;
    }

    private void updateProgress() {
        progress_bar_view.setProgress((int) (((7.0 - fakepH) / (7.0 - recipe.getpH())) * 100.0));

        // TODO: adjust jenk calculations
        if (Math.abs(fakepH - recipe.getpH()) <= 0.5 && !phComplete) {
            displayNotification("Goal pH reached!");
            phComplete = true;
            progress_bar_view.setProgress(100);
        }
//        if (Math.abs(fakeTemp - recipe.getTemp()) <= 1.0 && !tempComplete) {
//            displayNotification("Goal temperature reached!");
//            tempComplete = true;
//        }

        if (phComplete) {
            stopLogger();
            displayNotification("Coffee Complete!");
        }
    }

    private void generateData() {
        List<Line> lines = new ArrayList<>();
        Line pHLine = new Line(pHvalues)
                .setColor(Color.BLUE)
                .setCubic(false)
                .setStrokeWidth(3);
        lines.add(pHLine);

//        ArrayList<PointValue> goalpHValues = new ArrayList<>();
//        for (PointValue pv : pHvalues) {
//            goalpHValues.add(new PointValue(pv.getX(), recipe.getpH().floatValue()));
//        }
//        Line goalpHLine = new Line(goalpHValues)
//                .setColor(Color.BLUE)
//                .setStrokeWidth(1)
//                .setHasPoints(false);
//        lines.add(goalpHLine);

        Line tempLine = new Line(tempValues)
                .setColor(Color.RED)
                .setCubic(false)
                .setStrokeWidth(3);
        lines.add(tempLine);

//        ArrayList<PointValue> goalTempValues = new ArrayList<>();
//        for (PointValue pv : tempValues) {
//            goalTempValues.add(new PointValue(pv.getX(), recipe.getTemp().floatValue() * scale - offset));
//        }
//        Line goalTempLine = new Line(goalTempValues)
//                .setColor(Color.RED)
//                .setStrokeWidth(1)
//                .setHasPoints(false);
//        lines.add(goalTempLine);

        LineChartData data = new LineChartData(lines);

        Axis axisX = new Axis().setName("Time");
        Axis axisY = new Axis().setName("pH")
                .setTextColor(ChartUtils.COLOR_BLUE)
                .setHasLines(true);
        Axis axisZ = new Axis().setName("Temperature")
                .setTextColor(ChartUtils.COLOR_RED)
                .setFormatter(new TemperatureValueFormatter(scale, offset, 0));

        data.setAxisXBottom(axisX);
        data.setAxisYLeft(axisY);
        data.setAxisYRight(axisZ);

        lineChartView.setLineChartData(data);
        resetViewport();
    }

    private void reset() {
        fakeTimer = 0f;
        fakepH = 7.0f;
        fakeTemp = 32.0f;

        phComplete = false;
        tempComplete = false;
        progress_bar_view.setProgress(0);

        pHvalues.clear();
        tempValues.clear();
        generateData();
    }

    private void resetViewport() {
        final Viewport v = new Viewport(lineChartView.getMaximumViewport());
        v.bottom = 0;
        v.top = 14.0f;
        v.left = 0;
        v.right = pHvalues.size();
        lineChartView.setMaximumViewport(v);
        lineChartView.setCurrentViewport(v);
    }

    public void addPhValue(PointValue pv) {
        pHvalues.add(pv);
        generateData();
    }

    public void addTempValue(PointValue pv) {
        pv.set(pv.getX(), pv.getY() * scale - offset);
        tempValues.add(pv);
        generateData();
    }

    private void startCoffeeRefreshTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Log.d("MainActivity", "Getting coffee status");
                liquidLogAPI.getCoffeeStatus();
                if (inProgress) {
                    fakepH -= 0.5;
                    fakeTemp += 2.0;
                    addPhValue(new PointValue(fakeTimer, (float) fakepH));
                    addTempValue(new PointValue(fakeTimer, (float) fakeTemp));
                    updateProgress();
                    fakeTimer += 1f;
                }
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
        Log.d("MainActivity", "success for some reason");
        if (coffeeStatus.getpH() >= 4.8 && coffeeStatus.getpH() <= 5.2 && !notified) {
            displayNotification("Your coffee is ready!");
            notified = true;
        }
        else if (coffeeStatus.getpH() < 4.5) {
            Log.d("MainActivity", "Notify");
            displayNotification("Sorry, your coffee is ruined");
        }
    }

    public void selectCurrentRecipe() {
        final List<ParseObject> recipes = ParseManager.getAllRecipes();
        final ArrayList<String> items = new ArrayList<>();
        for (ParseObject po : recipes) {
            items.add(po.getString("name"));
        }

        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        adb.setSingleChoiceItems(items.toArray(new String[items.size()]), 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface d, int n) {
                ParseObject selectedRecipe = recipes.get(n);
                setCurrentRecipe(new RecipeObject(selectedRecipe.getString("name"),
                        selectedRecipe.getDouble("pH"),
                        selectedRecipe.getDouble("temp"),
                        selectedRecipe.getString("notes"),
                        selectedRecipe.getObjectId()));
                d.dismiss();
            }
        });
        adb.setNegativeButton("Cancel", null);
        adb.setTitle("Select a Recipe");
        adb.show();
    }

    public void setCurrentRecipe(RecipeObject selected) {
        recipe = selected;
        recipeName_textview.setText(recipe.getName());
        goalpH_textview.setText(recipe.getpH().toString());
        goalTemp_textview.setText(recipe.getTemp().toString());
        reset();
    }

    private static class TemperatureValueFormatter extends SimpleAxisValueFormatter {

        private float scale;
        private float sub;
        private int decimalDigits;

        public TemperatureValueFormatter(float scale, float sub, int decimalDigits) {
            this.scale = scale;
            this.sub = sub;
            this.decimalDigits = decimalDigits;
        }

        @Override
        public int formatValueForAutoGeneratedAxis(char[] formattedValue, float value, int autoDecimalDigits) {
            float scaledValue = (value + sub) / scale;
            return super.formatValueForAutoGeneratedAxis(formattedValue, scaledValue, this.decimalDigits);
        }
    }
}
