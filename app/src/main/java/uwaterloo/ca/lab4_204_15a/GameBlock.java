package uwaterloo.ca.lab4_204_15a;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.sax.RootElement;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Random;

/**
 * Created by Kevin on 2017/6/24.
 */

public class GameBlock extends GameBlockTemplate {

    public final float OFFSET_X = -60f;   // offset constants after GameBlock sclaed
    public final float OFFSET_Y = -60f;
    public final float LEFT_BOUND = -60f;
    public final float RIGHT_BOUND = 750f;
    public final float UP_BOUND = -60f;
    public final float DOWN_BOUND = 750f;
    public final float SLOT_ISO = 270f;

    public final float TEXT_OFFSET = 100f;

    private final float IMAGE_SCALE = 0.65f;
    private float myCoordX;
    private float myCoordY;

    private float myTargetCoordX;
    private float myTargetCoordY;

    private float VELOCITY = 2;
    private final float ACCEL = 2;

    public static boolean moving = false;   // to identify if any blocks are moving. The block will not recognize gestures when block moving

    public RelativeLayout myLayout;
    private TextView numTV;
    private int blockNum;

    private boolean toBeRemoved = false;
    private boolean checked = false;   // part of the merge algorithm flag

    public static boolean ended = false;

    Random myRandom = new Random();

    static TextView winningText;

    private GameLoopTask.Directions myBlockDirection = GameLoopTask.Directions.No_Movement;

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public GameBlock(Context myContext, float coordX, float coordY, RelativeLayout myLayout){   // constructor
        super(myContext);
        myCoordX = coordX + OFFSET_X;
        myCoordY = coordY + OFFSET_Y;
        this.setImageResource(R.drawable.gameblock);
        this.setScaleX(IMAGE_SCALE);
        this.setScaleY(IMAGE_SCALE);
        this.setX(myCoordX);
        this.setY(myCoordY);

        this.myLayout = myLayout;
        myLayout.addView(this);

        int n = myRandom.nextInt(2) + 1;
        blockNum = 2*n;

        numTV = new TextView(myContext);
        numTV.setText(Integer.toString(blockNum));
        numTV.setX(myCoordX+TEXT_OFFSET);
        numTV.setY(myCoordY+TEXT_OFFSET);
        numTV.bringToFront();
        numTV.setTextSize(50);
        numTV.setTextColor(Color.RED);
        myLayout.addView(numTV);

        winningText = new TextView(myContext);

    }

    // coordinate getter methods
    public float getCoorX (){
        return this.myCoordX;
        //Log.d("coordinate testing: ", this.myCoordX+", "+this.myCoordY);
    }

    public float getCoorY (){
        return this.myCoordY;
        //Log.d("coordinate testing: ", this.myCoordX+", "+this.myCoordY);
    }

    public float getTarCoorX (){
        return this.myTargetCoordX;
        //Log.d("coordinate testing: ", this.myCoordX+", "+this.myCoordY);
    }

    public float getTarCoorY (){
        return this.myTargetCoordY;
        //Log.d("coordinate testing: ", this.myCoordX+", "+this.myCoordY);
    }

    public void setBlockDirection(GameLoopTask.Directions myBlockDirection){
        this.myBlockDirection = myBlockDirection;
    }

    public void setDestination(float x, float y){
        myTargetCoordX = x;
        myTargetCoordY = y;
    }

    public int getBlockNum (){
        return this.blockNum;
    }

    public void setBlockNum (int i){
        this.numTV.setText(Integer.toString(i));
    }

    public void setToBeRemoved(boolean newBool){
        this.toBeRemoved = newBool;
    }

    public boolean getToBeRemoved (){
        return toBeRemoved;
    }

    public void setChecked(boolean newBool){
        this.checked = newBool;
    }

    public boolean getChecked (){
        return this.checked;
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public void doubleBlockNum(){
        this.blockNum = blockNum*2;

        //////////////////******************* Winning Condition**********************///////////////
        if(blockNum == 64){
            winningText.setText("YOU WIN");
            winningText.setX(100);
            winningText.setY(50);
            winningText.bringToFront();
            winningText.setTextSize(100);
            winningText.setTextColor(Color.BLACK);
            myLayout.addView(winningText);

            ended = true;
        }
        this.numTV.setText(Integer.toString(blockNum));
        numTV.setTextSize(50);
        numTV.setTextColor(Color.RED);
    }


    public TextView getNumTV(){
        return numTV;
    }


    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public void move(){



        if (myBlockDirection == GameLoopTask.Directions.Left){
            if (myCoordX <= myTargetCoordX){
                this.setBlockDirection(GameLoopTask.Directions.No_Movement);
                moving = false;
                myCoordX = myTargetCoordX;
                this.setX(myCoordX);
                numTV.setX(myCoordX+TEXT_OFFSET);
                VELOCITY = 2; // reset initial velocity

            }
            else {
                moving = true;
                myCoordX = myCoordX - (VELOCITY);
                numTV.setX(myCoordX+TEXT_OFFSET);
                this.setX(myCoordX);
                VELOCITY += ACCEL;

            }
        }
        else if (myBlockDirection == GameLoopTask.Directions.Right){
            if (myCoordX >= myTargetCoordX){
                moving = false;
                VELOCITY = 2; // reset initial velocity
                myCoordX = myTargetCoordX;
                this.setX(myCoordX);
                numTV.setX(myCoordX+TEXT_OFFSET);
                this.setBlockDirection(GameLoopTask.Directions.No_Movement);

            }
            else {
                myCoordX = myCoordX + (VELOCITY);
                this.setX(myCoordX);
                numTV.setX(myCoordX+TEXT_OFFSET);
                VELOCITY += ACCEL;
                moving = true;

            }

        }
        else if (myBlockDirection == GameLoopTask.Directions.Up){
            if (myCoordY < myTargetCoordY){
                moving = false;
                VELOCITY = 2; // reset initial velocity
                myCoordY = myTargetCoordY;
                this.setY(myCoordY);
                numTV.setY(myCoordY+TEXT_OFFSET);
                this.setBlockDirection(GameLoopTask.Directions.No_Movement);

            }
            else {
                myCoordY = myCoordY - (VELOCITY);
                this.setY(myCoordY);
                numTV.setY(myCoordY+TEXT_OFFSET);
                VELOCITY += ACCEL;
                moving = true;

            }
        }
        else if (myBlockDirection == GameLoopTask.Directions.Down ){
            if (myCoordY > myTargetCoordY){
                moving = false;
                VELOCITY = 2; // reset initial velocity
                myCoordY = myTargetCoordY;
                this.setY(myCoordY);
                numTV.setY(myCoordY+TEXT_OFFSET);
                //Log.d("debug info: ", "disappear " + this.myCoordX+", "+this.myCoordY);

                this.setBlockDirection(GameLoopTask.Directions.No_Movement);

            }
            else {
                moving = true;
                myCoordY = myCoordY + (VELOCITY);
                VELOCITY += ACCEL;
                this.setY(myCoordY);
                numTV.setY(myCoordY+TEXT_OFFSET);

            }
        }


    }

}
