package uwaterloo.ca.lab4_204_15a;

import android.widget.TextView;

/**
 * Created by Kevin on 2017/6/7.
 */

public class FSM {
    final double FALL_SLOPE;   // instance constants
    final double TROUGH;
    final double RISE_SLOPE;
    final double PEAK;
    final double STABLE_RANGE;

    final int STABLE_COUNT = 20;   // after reaching "Stable", wait this # of readings and check if the readings fall back within range STABLE_RANGE


    boolean upSig = true;   // used to determine if the waveform is +ve or -ve (TypeA or TypeB)
                            // in "Stable" state, the machine has to know which "path" did the state come from
                            // based on which "path" the state came from, "Stable" will set the Signature to either "TypeA" or "TypeB"

    int count = STABLE_COUNT;

    enum States {Wait, Down_Falling, Down_Rising, Up_Rising, Up_Falling, Stable, Determined}   // Down_Falling: falling portion of -ve x or y waveforms
    enum Signatures {TypeA, TypeB, TypeX}  // TypeA: +ve x or y   TypeB : -ve x or y   TypeX: undetermined

    private States myStates;   // instance enums
    private Signatures mySignature;
    private Signatures sigTemp;

    TextView sigTV;
    double previousRead = 0;
    double slope = 0;

    //constructor

    public  FSM(TextView sigTV, double FALL_SLOPE, double TROUGH, double RISE_SLOPE, double PEAK, double STABLE_RANGE){
        myStates = States.Wait;   // initialize state machine with default settings
        mySignature = Signatures.TypeX;
        this.sigTV = sigTV;
        this.FALL_SLOPE = FALL_SLOPE;  // constants declared in main class
        this.TROUGH = TROUGH;
        this.RISE_SLOPE = RISE_SLOPE;
        this.PEAK = PEAK;
        this.STABLE_RANGE = STABLE_RANGE;
    }

    public void resetFSM(){    // reset/default state
        myStates = States.Wait;
        mySignature = Signatures.TypeX;
        count = STABLE_COUNT;
    }

    public Signatures newData (double newReading){


        slope = newReading - previousRead;  // updates slope using the latest two readings


        // ************** FSM Implementation ***************//
        switch (myStates){

            case Wait:
                if (slope <= FALL_SLOPE){
                    myStates = States.Down_Falling;   // state transition
                    upSig = false;   // the waveform is potentially -ve x or y
                }

                else if (slope >= RISE_SLOPE) {
                    myStates = States.Up_Rising;
                    upSig = true;   // the waveform is potentially +ve x or y
                }
                break;

            case Down_Falling:
                //Log.d("Kevin debug info", "down falling");
                if (slope >= 0){    // hit trouph
                    if (newReading <= TROUGH){    // check if the read at trough is lower than TROUGH
                        myStates = States.Down_Rising;
                    }
                    else {    // if not, undetermined waveform
                        myStates = States.Determined;
                        mySignature = Signatures.TypeX;
                    }

                }
                break;


            case Down_Rising:
                //Log.d("Kevin debug info", "down rising");

                if (slope <= 0){    // hit peak
                    if (newReading >= PEAK){
                        myStates = States.Stable;
                    }
                    else {
                        myStates = States.Determined;
                        mySignature = Signatures.TypeX;
                    }

                }

                break;

            case Up_Rising:
                //Log.d("Kevin debug info", "up rising");
                if (slope <= 0){    // hit peak

                    if (newReading >= PEAK){
                        myStates = States.Up_Falling;
                    }
                    else {
                        myStates = States.Determined;
                        mySignature = Signatures.TypeX;
                    }

                }
                break;

            case Up_Falling:
                //Log.d("Kevin debug info", "up falling");
                if (slope >= 0){    // hit trouph
                    //Log.d("Kevin debug info", "up falling trouph is: "+newReading);

                    if (newReading <= TROUGH){
                        myStates = States.Stable;
                    }
                    else {
                        myStates = States.Determined;
                        mySignature = Signatures.TypeX;
                    }

                }

                break;

            case Stable:
                //Log.d("Kevin debug info", "stablizing");
                count--;    // wait for STABLE_COUNT readings

                if (count == 0){
                    if (newReading <= STABLE_RANGE && previousRead <= STABLE_RANGE){  // if the data after STABLE_COUNT readings are lower than STABLE_RANGE
                        myStates = States.Determined;   // valid gesture

                        if (upSig){
                            mySignature = Signatures.TypeA;
                        }
                        else {
                            mySignature = Signatures.TypeB;
                        }

                    }
                    else{
                        myStates = States.Determined;
                        mySignature = Signatures.TypeX;
                    }
                }


                break;


            case Determined:
                //sigTV.setText(mySignature.toString());
                //Log.d("Kevin debug info", "determined"+mySignature);
                sigTemp = mySignature;  // use the sigTemp to temporarily store mySignature so the signature is not lost upon reset
                resetFSM();   // reset Signature to Type_X
                return sigTemp;  // return the signature


            default:
                resetFSM();
                return Signatures.TypeX;
        }

        previousRead = newReading; // update reading
        return Signatures.TypeX;  // default return
    }
}
