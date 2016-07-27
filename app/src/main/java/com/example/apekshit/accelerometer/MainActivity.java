package com.example.apekshit.accelerometer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener, AdapterView.OnItemSelectedListener {
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    TextView t1, t2, t3, resultx, resulty, resultz;
    // create a couple arrays of y-values to plot:
    ArrayList<Double> series1Numbers;
    ArrayList<Double> series2Numbers;
    ArrayList<Double> series3Numbers;
    int senstype;
    String[] axes = {"XYZ","X", "Y", "Z", "XY", "YZ", "ZX"};
    int timegap;
    int i=0;
    EditText time;
    double mode;
    int xcol,ycol,zcol;
    String stasto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        stasto="Pause";
        time = (EditText) findViewById(R.id.editText);
        series1Numbers = new ArrayList<>();
        series2Numbers = new ArrayList<>();
        series3Numbers = new ArrayList<>();
        senstype = Sensor.TYPE_ACCELEROMETER;
        timegap = 400;
        mode = 9.81;
        // Spinner element
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> spinnerad = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, axes);
        spinnerad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerad);
        xcol=Color.MAGENTA;
        ycol=Color.YELLOW;
        zcol=Color.TRANSPARENT;
        Toast.makeText(this,"Default configuration:\nTime Duration:400ms\nGravity:considered\nUnit:g",Toast.LENGTH_LONG).show();
        // Spinner click listener
        spinner.setOnItemSelectedListener(this);
        setUp(senstype);
    }

    public void setUp(int type) {
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(type);
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        t1 = (TextView) findViewById(R.id.textView);
        t2 = (TextView) findViewById(R.id.textView2);
        t3 = (TextView) findViewById(R.id.textView3);
        resultx = (TextView) findViewById(R.id.textView4);
        resulty = (TextView) findViewById(R.id.textView5);
        resultz = (TextView) findViewById(R.id.textView6);
        Button btnChart = (Button) findViewById(R.id.btn_chart);

        // Defining click event listener for the button btn_chart
        View.OnClickListener clickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Draw the Income vs Expense Chart
                openChart();
            }
        };

        // Setting event click listener for the button btn_chart of the MainActivity layout
        btnChart.setOnClickListener(clickListener);

    }

    @Override
    protected void onPause() {
        super.onPause();
        senSensorManager.unregisterListener(this);
        Toast.makeText(this,"Calculations Paused",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
           // Toast.makeText(this, "Resume Calculations by tapping the Start button", Toast.LENGTH_LONG).show();
    }

    double x;
    double y;
    double z;
    long prevtime = 0;

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        long currenttime = System.currentTimeMillis();

        if ((currenttime - prevtime) > timegap) {

            double xbr = event.values[0];
            double ybr = event.values[1];
            double zbr = event.values[2];

            xbr = xbr / mode;
            ybr = ybr / mode;
            zbr = zbr / mode;

            x = (Math.round(xbr * 100.0) / 100.0);
            y = (Math.round(ybr * 100.0) / 100.0);
            z = (Math.round(zbr * 100.0) / 100.0);
            if (mode == 1.0) {
                t1.setText("x:" + x);
                t2.setText("y:" + y);
                t3.setText("z:" + z);
            } else {
                t1.setText("x:" + x + "g");
                t2.setText("y:" + y + "g");
                t3.setText("z:" + z + "g");
            }
            prevtime = currenttime;
            series1Numbers.add(x);
            series2Numbers.add(y);
            series3Numbers.add(z);

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void change(View v) {
        clear(v);
        senSensorManager.unregisterListener(this);
        if (senstype == Sensor.TYPE_ACCELEROMETER) {
            senstype = Sensor.TYPE_LINEAR_ACCELERATION;
            Toast.makeText(this, "Ignoring Gravity(Requires Gyroscope/Magnetometer))", Toast.LENGTH_SHORT).show();
            stasto="Pause";
            setUp(senstype);
        } else {
            senstype = Sensor.TYPE_ACCELEROMETER;
            Toast.makeText(this, "Considering Gravity", Toast.LENGTH_SHORT).show();
            setUp(senstype);
        }

    }

    public void timeclick(View v) {
        try {
            clear(v);
            timegap = Integer.parseInt(time.getText().toString());
            if (timegap < 100) {
                Toast.makeText(this, "The value has to be greater than or equal to 100", Toast.LENGTH_SHORT).show();
            }
            senSensorManager.unregisterListener(this);
            stasto="Pause";
            setUp(senstype);
            Toast.makeText(this, "Time set to " + timegap + "milliseconds", Toast.LENGTH_SHORT).show();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Not a valid integer", Toast.LENGTH_SHORT).show();
        }
    }

    public void unit(View v) {
        clear(v);
        if (mode == 9.81) {
            mode = 1.0;
            Toast.makeText(this, "Values in m/sec^2", Toast.LENGTH_SHORT).show();
        } else {
            mode = 9.81;
            Toast.makeText(this, "Values in terms of g", Toast.LENGTH_SHORT).show();
        }
        senSensorManager.unregisterListener(this);
        stasto="Pause";
        setUp(senstype);

    }

    public void clear(View v) {
        series1Numbers.clear();
        series2Numbers.clear();
        series3Numbers.clear();
        resultx.setText("X\n");
        resulty.setText("Y\n");
        resultz.setText("Z\n");
    }
public void ststo(View v){
    if(stasto=="Start"){
        stasto="Pause";
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        Toast.makeText(this,"Calculations started",Toast.LENGTH_SHORT).show();
    }
    else{
        if(stasto=="Pause"){
            stasto="Start";
            senSensorManager.unregisterListener(this);
            Toast.makeText(this,"Calculations paused",Toast.LENGTH_SHORT).show();

        }
    }
}
    public void show(View v) {
        resultx.setText("X\n");
        resulty.setText("Y\n");
        resultz.setText("Z\n");
        for (int i = 0; i < series1Numbers.size(); i++) {
            if(mode==1.0) {
                resultx.append(series1Numbers.get(i) + "\n");
                resulty.append(series2Numbers.get(i) + "\n");
                resultz.append(series3Numbers.get(i) + "\n");
            }
            else{
                resultx.append(series1Numbers.get(i) + "g\n");
                resulty.append(series2Numbers.get(i) + "g\n");
                resultz.append(series3Numbers.get(i) + "g\n");
            }
        }

        // Getting reference to the button btn_chart

    }

    private void openChart() {

        // Creating an  XYSeries for Income
        XYSeries xacc = new XYSeries("X");
        // Creating an  XYSeries for Expense
        XYSeries yacc = new XYSeries("Y");
        XYSeries zacc = new XYSeries("Z");
        // Adding data to Income and Expense Series
        for (int i = 0; i < series1Numbers.size(); i++) {
            xacc.add(i + 1, series1Numbers.get(i));
            yacc.add(i + 1, series2Numbers.get(i));
            zacc.add(i + 1, series3Numbers.get(i));
        }

        // Creating a dataset to hold each series
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        // Adding Income Series to the dataset
        dataset.addSeries(xacc);
        // Adding Expense Series to dataset
        dataset.addSeries(yacc);
        dataset.addSeries(zacc);

        // Creating XYSeriesRenderer to customize incomeSeries
         XYSeriesRenderer xRenderer = new XYSeriesRenderer();
        xRenderer.setColor(xcol);
        xRenderer.setPointStyle(PointStyle.CIRCLE);
        xRenderer.setFillPoints(true);
        xRenderer.setLineWidth(5);
        xRenderer.setDisplayChartValues(true);
        xRenderer.setChartValuesTextSize(20);

        // Creating XYSeriesRenderer to customize expenseSeries
        XYSeriesRenderer YRenderer = new XYSeriesRenderer();
        YRenderer.setColor(ycol);
        YRenderer.setPointStyle(PointStyle.CIRCLE);
        YRenderer.setFillPoints(true);
        YRenderer.setLineWidth(5);
        YRenderer.setDisplayChartValues(true);
        YRenderer.setChartValuesTextSize(20);

        XYSeriesRenderer ZRenderer = new XYSeriesRenderer();
        ZRenderer.setColor(zcol);
        ZRenderer.setPointStyle(PointStyle.CIRCLE);
        ZRenderer.setFillPoints(true);
        ZRenderer.setLineWidth(5);
        ZRenderer.setDisplayChartValues(true);
        ZRenderer.setChartValuesTextSize(20);


        // Creating a XYMultipleSeriesRenderer to customize the whole chart
        XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
        multiRenderer.setXLabels(0);
        multiRenderer.setChartTitle("Accelerometer");
        multiRenderer.setXTitle("Time(seconds)");
        multiRenderer.setYTitle("Acceleration");
        multiRenderer.setZoomButtonsVisible(true);
        multiRenderer.setLabelsTextSize(35);
        multiRenderer.setLabelsColor(Color.WHITE);
        multiRenderer.setApplyBackgroundColor(true);
        multiRenderer.setBackgroundColor(Color.DKGRAY);
        multiRenderer.setLegendTextSize(50);
        multiRenderer.setChartTitleTextSize(50);
        multiRenderer.setShowGrid(true);
        multiRenderer.setShowGridX(true);
        multiRenderer.setShowGridY(true);
        for (double i = 0; i < series1Numbers.size(); i += 1000.0 / timegap) {
            multiRenderer.addXTextLabel(i, ((i * timegap) / 1000.0) + "");
        }

        // Adding incomeRenderer and expenseRenderer to multipleRenderer
        // Note: The order of adding dataseries to dataset and renderers to multipleRenderer
        // should be same
        multiRenderer.addSeriesRenderer(xRenderer);
        multiRenderer.addSeriesRenderer(YRenderer);
        multiRenderer.addSeriesRenderer(ZRenderer);


        // Creating an intent to plot line chart using dataset and multipleRenderer
        Intent intent = ChartFactory.getLineChartIntent(getBaseContext(), dataset, multiRenderer);

        // Start Activity
        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case (1):
                xcol = Color.MAGENTA;
                ycol = Color.TRANSPARENT;
                zcol = Color.TRANSPARENT;
                break;

            case (2):
                Log.e("case1", position+"");
                ycol = Color.YELLOW;
                xcol = Color.TRANSPARENT;
                zcol = Color.TRANSPARENT;
                break;


            case (3):

                zcol = Color.CYAN;
                xcol = Color.TRANSPARENT;
                ycol = Color.TRANSPARENT;
                break;

            case (4):

                ycol = Color.YELLOW;
                xcol = Color.MAGENTA;
                zcol = Color.TRANSPARENT;
                break;

            case (5):

                ycol = Color.YELLOW;
                xcol = Color.TRANSPARENT;
                zcol = Color.CYAN;
                break;

            case (6):

                ycol = Color.TRANSPARENT;
                xcol = Color.MAGENTA;
                zcol = Color.CYAN;
                break;

            case (0):

                ycol = Color.YELLOW;
                xcol = Color.MAGENTA;
                zcol = Color.CYAN;
                break;
        }
        senSensorManager.unregisterListener(this);
        setUp(senstype);
        }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
