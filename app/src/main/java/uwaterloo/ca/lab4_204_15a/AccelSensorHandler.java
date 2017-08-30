package uwaterloo.ca.lab4_204_15a;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.TextView;

//import ca.uwaterloo.sensortoy.LineGraphView;

import static uwaterloo.ca.lab4_204_15a.GameBlock.ended;
import static uwaterloo.ca.lab4_204_15a.GameBlock.moving;
import static uwaterloo.ca.lab4_204_15a.MainActivity.accelRecord;
import static uwaterloo.ca.lab4_204_15a.MainActivity.counter;

/**
 * Created by Kevin on 2017/6/4.
 */

public class AccelSensorHandler implements SensorEventListener {

    TextView myLocalTV;
    float[] accel_reads;
    String accel_textout;
//    LineGraphView graph;
    double round = 0;
    double[][] accel_IOreading;
    TextView accelRecordTV;
    TextView SignatureTV;

    FSM myFSM_y;
    FSM myFSM_x;

    final float C = 40;  // low pass filter constant


    FSM.Signatures mySig_y;
    FSM.Signatures mySig_x;

    GameLoopTask myGameTask;


    public AccelSensorHandler(TextView tv, double[][] accel_IOreading, TextView accelRecordTV, TextView sigTV, GameLoopTask myGameTask) {
        this.myLocalTV = tv;
//        this.graph = graph;
        this.accel_IOreading = accel_IOreading;
        this.accelRecordTV = accelRecordTV;
        SignatureTV = sigTV;

        // state machine declarations: 1 for each axis
        myFSM_y = new FSM(SignatureTV, MainActivity.FALL_SLOPE, MainActivity.TROUGH, MainActivity.RISE_SLOPE, MainActivity.PEAK, MainActivity.STABLE_RANGE);
        myFSM_x = new FSM(SignatureTV, MainActivity.FALL_SLOPE, MainActivity.TROUGH, MainActivity.RISE_SLOPE, MainActivity.PEAK, MainActivity.STABLE_RANGE);

        //GameLoopTask reference for setDirection()
        this.myGameTask = myGameTask;

    }


    //
    public void onAccuracyChanged(Sensor s, int i) {
    }

    //
    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public void onSensorChanged(SensorEvent se) {


        accel_reads = new float[3];

        if (se.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {

            //rounding values from the accelerometer in order to output the values
            for (int i = 0; i < 3; i++) {
                round = Math.round(se.values[i] * 100.0) / 100.0;
                accel_reads[i] = (float) round;
                //we are recording the most recent 100 readings
                //when all 100 are filled, the oldest reading is removed from the stack
                if (counter > 99) {
                    counter = 0;
                }

                if (counter == 0) {
                    // edge case where the array index pointer is reset to 0
                    accel_IOreading[counter][i] = accel_IOreading[99][i] + (accel_reads[i] - accel_IOreading[99][i]) / C;  // low pass filter
                }
                else{
                    //************ low pass filter implementation ****************//
                    // newReading = oldReading + (newReading - oldReading) / constant
                    accel_IOreading[counter][i] = accel_IOreading[counter-1][i] + (accel_reads[i] - accel_IOreading[counter-1][i]) / C;
                }
            }

            accel_textout = String.format("(%.2f,%.2f,%.2f)", accel_IOreading[counter][0], accel_IOreading[counter][1], accel_IOreading[counter][2]);
            myLocalTV.setText(accel_textout);

            float [] temp = new float[3];

            for (int i = 0; i < 3; i++) {
                temp[i] = (float) accel_IOreading[counter][i];
            }

//            graph.addPoint(temp);


            //compare to historical high
            for (int i = 0; i < 3; i++) {
                if (Math.abs(accel_reads[i]) > accelRecord[i]) {
                    accelRecord[i] = Math.abs(accel_reads[i]);
                    accel_textout = String.format("(%.2f,%.2f,%.2f)", accelRecord[0], accelRecord[1], accelRecord[2]);
                    accelRecordTV.setText(accel_textout);
                }
            }

            counter++;


            //***************** FSM Calling *********************//

            // every time a new reading is received
            mySig_y = myFSM_y.newData(accel_IOreading[counter-1][1]);   // passing latest y value
            mySig_x = myFSM_x.newData(accel_IOreading[counter-1][0]);   // passing latest x value

            //Log.d("moving? :", Boolean.toString(moving));

            if (!ended) {
                // logic function
                // inputs: y axis signature, x axis signature
                // output: appropriate textview output
                if (mySig_y == FSM.Signatures.TypeA && mySig_x == FSM.Signatures.TypeX) {
                    SignatureTV.setText("UP");
                    if (!moving) {   // set block in motion only when the block is not moving
                        myGameTask.setDirection(GameLoopTask.Directions.Up);
                        myGameTask.removeToBeRemovedBlocks();
                        myGameTask.createBlock();
                    }

                } else if (mySig_y == FSM.Signatures.TypeB && mySig_x == FSM.Signatures.TypeX) {
                    SignatureTV.setText("DOWN");
                    if (!moving) {
                        myGameTask.setDirection(GameLoopTask.Directions.Down);
                        myGameTask.removeToBeRemovedBlocks();
                        myGameTask.createBlock();
                    }

                } else if (mySig_y == FSM.Signatures.TypeX && mySig_x == FSM.Signatures.TypeA) {
                    SignatureTV.setText("RIGHT");
                    if (!moving) {
                        myGameTask.setDirection(GameLoopTask.Directions.Right);
                        myGameTask.removeToBeRemovedBlocks();
                        myGameTask.createBlock();
                    }

                } else if (mySig_y == FSM.Signatures.TypeX && mySig_x == FSM.Signatures.TypeB) {
                    SignatureTV.setText("LEFT");
                    if (!moving) {
                        myGameTask.setDirection(GameLoopTask.Directions.Left);
                        myGameTask.removeToBeRemovedBlocks();
                        myGameTask.createBlock();
                    }

                }

            }


        }







    }


}
