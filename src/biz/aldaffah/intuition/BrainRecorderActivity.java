/*
 * 
 */
package biz.aldaffah.intuition;

//~--- non-JDK imports --------------------------------------------------------

import android.app.Activity;

import android.bluetooth.BluetoothAdapter;

import android.content.Context;
import android.content.Intent;

import android.database.sqlite.SQLiteDatabase;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.util.Log;

import android.view.View;

import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.artfulbits.aiCharts.Base.ChartArea;
import com.artfulbits.aiCharts.Base.ChartSeries;
import com.artfulbits.aiCharts.ChartView;
import com.artfulbits.aiCharts.Types.ChartTypes;

import com.neurosky.thinkgear.*;

//~--- JDK imports ------------------------------------------------------------

import java.util.ArrayList;
import java.util.List;

public class BrainRecorderActivity extends Activity {
    final boolean rawEnabled = false;
    boolean       graphing   = false;

    /** 
     * Handles messages from TGDevice. 
     */
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (!graphing) {
                switch (msg.what) {
                case TGDevice.MSG_STATE_CHANGE :
                    switch (msg.arg1) {
                    case TGDevice.STATE_IDLE :
                        break;

                    case TGDevice.STATE_CONNECTING :
                        myTextView = (TextView) findViewById(R.id.connectStatus);
                        myTextView.setText("Connecting ...");

                        break;

                    case TGDevice.STATE_CONNECTED :
                        myTextView = (TextView) findViewById(R.id.connectStatus);
                        myTextView.setText("Connected");
                        tgDevice.start();

                        break;

                    case TGDevice.STATE_NOT_FOUND :
                        myTextView = (TextView) findViewById(R.id.connectStatus);
                        myTextView.setText("Connection Not Found, reset and try again.");;

                        break;

                    case TGDevice.STATE_NOT_PAIRED :
                        myTextView = (TextView) findViewById(R.id.connectStatus);
                        myTextView.setText("There is not Mindset paired to this device.");

                        break;

                    case TGDevice.STATE_DISCONNECTED :
                        myTextView = (TextView) findViewById(R.id.connectStatus);
                        myTextView.setText("Disconnected");
                    }

                    break;

                case TGDevice.MSG_POOR_SIGNAL :
                    myTextView = (TextView) findViewById(R.id.signal);

                    if (msg.arg1 == 0) {
                        myTextView.setText("Great");
                    } else if (msg.arg1 > 50) {
                        myTextView.setText("Poor, please readjust headset.");
                    }

                    break;

                case TGDevice.MSG_ATTENTION :
                    mProgress = (ProgressBar) findViewById(R.id.attentionBar);
                    mProgress.setProgress(msg.arg1);

                    break;

                case TGDevice.MSG_MEDITATION :
                	mProgress = (ProgressBar) findViewById(R.id.meditationBar);
                    mProgress.setProgress(msg.arg1);
                    break;

                case TGDevice.MSG_BLINK :
                	myTextView = (TextView) findViewById(R.id.blink);
                    myTextView.setText("Blink " + msg.arg1);
                    break;

                case TGDevice.MSG_EEG_POWER :
                    fbands = (TGEegPower) msg.obj;
                    points.add(fbands);
                    myTextView = (TextView) findViewById(R.id.ch1);
                    myTextView.setText("Delta " + fbands.delta);
                    myTextView = (TextView) findViewById(R.id.ch2);
                    myTextView.setText("High Alpha " + fbands.highAlpha);
                    myTextView = (TextView) findViewById(R.id.ch3);
                    myTextView.setText("High Beta " + fbands.highBeta);
                    myTextView = (TextView) findViewById(R.id.ch4);
                    myTextView.setText("Low Alpha " + fbands.lowAlpha);
                    myTextView = (TextView) findViewById(R.id.ch5);
                    myTextView.setText("Low Beta " + fbands.lowBeta);
                    myTextView = (TextView) findViewById(R.id.ch6);
                    myTextView.setText("Low Gamma " + fbands.lowGamma);
                    myTextView = (TextView) findViewById(R.id.ch7);
                    myTextView.setText("Mid Gamma " + fbands.midGamma);
                    myTextView = (TextView) findViewById(R.id.ch8);
                    myTextView.setText("Theta " + fbands.theta);

                    break;

                case TGDevice.MSG_LOW_BATTERY :
                    Toast.makeText(getApplicationContext(), "Low battery!", Toast.LENGTH_SHORT).show();

                    break;

                default :
                    break;
                }
            }
        }
    };
    
    BluetoothAdapter bluetoothAdapter;
    TextView         myTextView;
    ChartView        chartView;
    TGRawMulti       rawData;
    TGDevice         tgDevice;
    TGEegPower       fbands;
    frequencyTable   db;

    /**
     * EEGPoint current;
     */
    String              thought;
    List<TGEegPower>    points;
    private ProgressBar mProgress;

    /** 
     * Called when the activity is first created. 
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recording);
        points = new ArrayList<TGEegPower>();

        /** Thinking don't exist now
        db = new frequencyTable(this);
         
        Bundle extras = getIntent().getExtras();

        thought = extras.getString("thought");

        current = new EEGPoint(thought);
        myTextView = (TextView) findViewById(R.id.thinking);
        myTextView.setText(thought);
		
		*/
		
        /** start up bluetooth */
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {

        	/** Alert user that bluetooth is not available */
            Toast.makeText(this, "Bluetooth not available", Toast.LENGTH_LONG).show();
            finish();

            return;
        } else {

        	/** create the TGDevice */
            tgDevice = new TGDevice(bluetoothAdapter, handler);
        }

        if ((tgDevice.getState() != TGDevice.STATE_CONNECTING) && (tgDevice.getState() != TGDevice.STATE_CONNECTED)) {
            tgDevice.connect(rawEnabled);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tgDevice.close();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void graph(View view) {
        graphing = true;
        setContentView(R.layout.graphing);

        /**
                
        chartView = new ChartView(this);
        ChartSeries series1 = new ChartSeries("s1", ChartTypes.Line);
        ChartSeries series2 = new ChartSeries("s2", ChartTypes.Line);
        ChartSeries series3 = new ChartSeries("s3", ChartTypes.Line);
        ChartSeries series4 = new ChartSeries("s4", ChartTypes.Line);
        ChartSeries series5 = new ChartSeries("s5", ChartTypes.Line);
        ChartSeries series6 = new ChartSeries("s6", ChartTypes.Line);
        ChartSeries series7 = new ChartSeries("s7", ChartTypes.Line);
        ChartSeries series8 = new ChartSeries("s8", ChartTypes.Line);
        
        */
        
        chartView = (ChartView) findViewById(R.id.chartView);

        ChartSeries series1 = chartView.getSeries().get("Delta");
        ChartSeries series2 = chartView.getSeries().get("High Alpha");
        ChartSeries series3 = chartView.getSeries().get("High Beta");
        ChartSeries series4 = chartView.getSeries().get("Low Alpha");
        ChartSeries series5 = chartView.getSeries().get("Low Beta");
        ChartSeries series6 = chartView.getSeries().get("Low Gamma");;
        ChartSeries series7 = chartView.getSeries().get("Mid Gamma");
        ChartSeries series8 = chartView.getSeries().get("Theta");

        /**
         
        ChartArea area1 = chartView.getAreas().get("area");        
		ChartArea area2 = new ChartArea("area2");
		area1.getDefaultYAxis().setTitle("Frequency");
		area1.setName("Frequency Bands");
  
 		*/
        for (int i = 0; i < points.size(); i++) {
            series1.getPoints().addXY(i, points.get(i).delta);
            series2.getPoints().addXY(i, points.get(i).highAlpha);
            series3.getPoints().addXY(i, points.get(i).highBeta);
            series4.getPoints().addXY(i, points.get(i).lowAlpha);
            series5.getPoints().addXY(i, points.get(i).highBeta);
            series6.getPoints().addXY(i, points.get(i).lowGamma);
            series7.getPoints().addXY(i, points.get(i).midGamma);
            series8.getPoints().addXY(i, points.get(i).theta);
        }    
        
        /**
         
        series1.setArea("area1");
        series2.setArea("area1");
        series3.setArea("area1");
        series4.setArea("area1");
        series5.setArea("area1");
        series6.setArea("area1");
        series7.setArea("area1");
        series8.setArea("area1");

        chartView.getAreas().add(area1);
        chartView.getAreas().add(area2);
        chartView.getAreas().add(area3);

        chartView.getSeries().add(series1);
        chartView.getSeries().add(series2);
        chartView.getSeries().add(series3);
        chartView.getSeries().add(series4);
        chartView.getSeries().add(series5);
        chartView.getSeries().add(series6);
        chartView.getSeries().add(series7);
        chartView.getSeries().add(series8);
         
         */
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
