package uwaterloo.ca.lab4_204_15a;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.PrintWriter;
import java.util.Timer;


public class MainActivity extends AppCompatActivity {

    public static final double FALL_SLOPE = -0.2;
    public static final double TROUGH = -0.3;
    public static final double PEAK = 0.3;
    public static final double RISE_SLOPE = 0.2;
    public static final double STABLE_RANGE = 0.5;

    public static final int GAMEBOARD_DIMENSION = 1080;
    public static final int GAMEBOARD_BOUDARY = 810;

    public File file = null;
    public PrintWriter myWriter = null;
    public static int counter = 0;

    public static double[] accelRecord = {0, 0, 0};

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final double[][] accel_IOreading = new double[100][3];

        final String FILE_NAME = "Test 1.csv";
        final String FOLDER_NAME = "Kevin Lab 1";

        //reference to XML identifications set
        TextView accel_label = (TextView) findViewById(R.id.accel_label);

        //historical high textviews

        TextView accelRecordTV = (TextView) findViewById(R.id.accelRecordData);
        TextView sigTV = (TextView) findViewById(R.id.sigTV);



        RelativeLayout l = (RelativeLayout) findViewById(R.id.layout1);

        l.getLayoutParams().width = GAMEBOARD_DIMENSION;    //  setting up gameboard dimensions
        l.getLayoutParams().height = GAMEBOARD_DIMENSION;   //  only use relative layout on the game board (ie the square cannot move outside of layout)

        int height = l.getLayoutParams().height;
        Log.d("debug info", Integer.toString(height));
        l.setBackgroundResource(R.drawable.gameboard);


        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        Sensor accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);


        Timer myGameLoop = new Timer();
        GameLoopTask myGameLoopTask = new GameLoopTask(this, getApplicationContext(), l);
        myGameLoop.schedule(myGameLoopTask, 50, 12);

        //handler object
        AccelSensorHandler accelHandler = new AccelSensorHandler(accel_label, accel_IOreading, accelRecordTV, sigTV,myGameLoopTask);
        sensorManager.registerListener(accelHandler, accelSensor, SensorManager.SENSOR_DELAY_GAME);



    }

}


