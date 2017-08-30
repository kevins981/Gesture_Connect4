package uwaterloo.ca.lab4_204_15a;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.Random;
import java.util.TimerTask;

import static uwaterloo.ca.lab4_204_15a.GameBlock.moving;
import static uwaterloo.ca.lab4_204_15a.GameBlock.winningText;

/**
 * Created by Kevin on 2017/6/21.
 */

public class GameLoopTask extends TimerTask {

    private Activity myActivity;
    private Context myContext;
    private RelativeLayout myLayout;
    private Directions myDirections;

    public final float OFFSET_X = -60f;   // offset constants after GameBlock sclaed
    public final float OFFSET_Y = -60f;
    public final float LEFT_BOUND = -60f;
    public final float RIGHT_BOUND = 750f;
    public final float UP_BOUND = -60f;
    public final float DOWN_BOUND = 750f;
    public final float SLOT_ISO = 270f;

    Random myRandom = new Random();


    int n, m;
    GameBlock myGameBlock;



    private LinkedList <GameBlock> myBlockList = new LinkedList ();


    enum Directions {Left, Right, Up, Down, No_Movement}


    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public void createBlock(){
        Log.d("boolean array test", "create Block!!!");
        boolean isOccupied2D [][]  = new boolean[4][4];  // 2D array for random block generation (initialized to false)


        for (GameBlock i: myBlockList
                ) {
            isOccupied2D[(int)((i.getTarCoorX()- OFFSET_X)/SLOT_ISO )][(int)((i.getTarCoorY()- OFFSET_Y) /SLOT_ISO )] = true;   // coordinate to array index decoding
        }

        int emptySlotCount = 0;
        for (int i = 0; i <4; i++){
            for (int j = 0; j <4; j++){
               //Log.d("boolean array test", i+","+j+" :"+isOccupied2D[i][j]);
                if (isOccupied2D[i][j] == false){
                    emptySlotCount++;
                }
            }
        }
        Log.d("emptyslot array.", Integer.toString(emptySlotCount));

        if (emptySlotCount == 0 && noMerge() == true) {
            /////////////////////*************Game Losing Condition******************//////////////////
            winningText.setText("YOU LOSE");
            winningText.setX(100);
            winningText.setY(50);
            winningText.bringToFront();
            winningText.setTextSize(100);
            winningText.setTextColor(Color.BLACK);
            myLayout.addView(winningText);

            GameBlock.ended = true;
            noMerge();
        }else if (emptySlotCount != 0) {

            ///////////*******************Random Block Generation *******************/////////////////
            n = myRandom.nextInt(emptySlotCount) + 1;

            /// while implementation
            int i = 0;
            int j = 0;
            do {

                if (isOccupied2D[i][j] == false) {
                    n--;    // traverse to find the empty spot
                }
                if (n != 0) {
                    if (j == 3) {
                        j = 0;
                        i++;
                    } else {
                        j++;
                    }
                }
            } while (n != 0 && i < 4);
            Log.d("emptyslot array test: ", Integer.toString(i) + ", " + Integer.toString(j));
            myBlockList.add(new GameBlock(myContext, SLOT_ISO * i, SLOT_ISO * j, myLayout));

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public GameLoopTask(Activity myActivity, Context myContext, RelativeLayout myLayout){
        this.myActivity = myActivity;
        this.myContext = myContext;
        this.myLayout = myLayout;
        this.createBlock();
        Directions myDirections = Directions.No_Movement;
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public void setDirection(Directions myDirections){   // acceleration handler can set the direction based on the gesture
        this.myDirections = myDirections;

        for (GameBlock i : myBlockList) {
            i.setBlockDirection(myDirections);

            if (myDirections == Directions.Left){  // set initial target position (boundary)
                i.setDestination(LEFT_BOUND, i.getCoorY());
            }
            else if (myDirections == Directions.Right){
                i.setDestination( RIGHT_BOUND, i.getCoorY());
            }
            else if (myDirections == Directions.Up){
                i.setDestination(i.getCoorX(), UP_BOUND);
            }
            else if (myDirections == Directions.Down){
                i.setDestination(i.getCoorX(), DOWN_BOUND);
            }



        }

        int blockCount;
        int slotCount;


        //******************* Collision detection *******************//
//        for (GameBlock i : myBlockList) {
            blockCount = 0;
            slotCount = 0;
//            int currentBlockIndex = (int)((i.getCoorX()-OFFSET_X)/SLOT_ISO);
            if (myDirections == Directions.Left) {
                //                Log.d("merging array test: ", "new block");
//                for (int j = 0; j < 4; j++) {
//                    Log.d("merging array test: ", Integer.toString(analysisArray[j]));
//                }


//                for (int j = 0; j < 4; j++) {
//                    if (i.getTarCoorX()+j*SLOT_ISO == i.getCoorX()){   // if traversed to the current position of the block, exit for loop
//                        break;
//                    }
//                    if (isOccupied(i.getTarCoorX()+j*SLOT_ISO, i.getTarCoorY())){   // start from tartget slot and check going right
//                       // Log.d("debug info: ", "occupied!! " + i.getTarCoorX()+j*SLOT_ISO+" "+ i.getTarCoorY());
//                        blockCount++;
//                    }
//                    slotCount++;
//                }
//                //Log.d("debug info: ", "position " + i.getTarCoorX()+", "+i.getTarCoorY());
//                i.setDestination(i.getCoorX()-(slotCount-blockCount)*SLOT_ISO, i.getCoorY());
//                //Log.d("debug info: ", "counts " + slotCount+", "+blockCount);
//                }
                for (GameBlock i : myBlockList) {
                    blockCount = 0;
                    slotCount = 0;
                    int currentBlockIndex = (int) ((i.getCoorX() - OFFSET_X) / SLOT_ISO);
                    int analysisArray[] = new int[4];

                    for (int j = 0; j < 4; j++) {
                        if (returnBlockRef(OFFSET_X + j * SLOT_ISO, i.getCoorY()) != null) {
                            analysisArray[j] = returnBlockRef(OFFSET_X + j * SLOT_ISO, i.getCoorY()).getBlockNum();
                        } else {
                            analysisArray[j] = 0;
                        }
                    }

                    /////////////////////*****************Merging and collsion algorithm*****************/////////////////////
                    i.setDestination(targetCalcAlgorithm(analysisArray, currentBlockIndex) * SLOT_ISO + OFFSET_X, i.getCoorY());

                    for (int j = 0; j < 4; j++) {
                        analysisArray[j] = 0;    // reset array
                    }

                }



            }


            else if (myDirections == Directions.Right) {
//                for (GameBlock i : myBlockList) {
//                    blockCount = 0;
//                    slotCount = 0;
//                    for (int j = 0; j < 4; j++) {   // checking slots from right to left
//                        if (i.getTarCoorX() - j * SLOT_ISO == i.getCoorX()) {   // if traversed to the current position of the block, exit for loop
//                            break;
//                        }
//                        if (isOccupied(i.getTarCoorX() - j * SLOT_ISO, i.getTarCoorY())) {   // start from tartget slot and check going right
//                            // Log.d("debug info: ", "occupied!! " + i.getTarCoorX()+j*SLOT_ISO+" "+ i.getTarCoorY());
//                            blockCount++;
//                        }
//                        slotCount++;
//                    }
//                    //Log.d("debug info: ", "position " + i.getTarCoorX()+", "+i.getTarCoorY());
//                    i.setDestination(i.getCoorX() + (slotCount - blockCount) * SLOT_ISO, i.getCoorY());
//                    //Log.d("debug info: ", "counts " + slotCount+", "+blockCount);
//                }7

                for (GameBlock i : myBlockList) {
                    blockCount = 0;
                    slotCount = 0;
                    int currentBlockIndex = (int) ((i.getCoorX() - OFFSET_X) / SLOT_ISO);
                    currentBlockIndex = reverseCoordinate(currentBlockIndex);  // reverse (reflect) the coordinate of block for goinf right
                    int analysisArray[] = new int[4];

                    for (int j = 0; j < 4; j++) {
                        if (returnBlockRef(OFFSET_X + j * SLOT_ISO, i.getCoorY()) != null) {
                            analysisArray[j] = returnBlockRef(OFFSET_X + j * SLOT_ISO, i.getCoorY()).getBlockNum();
                        } else {
                            analysisArray[j] = 0;
                        }
                    }

                    int myReverseArray[] = new int[4];

                    for (int j = 0; j < 4; j++){
                        myReverseArray[j] = analysisArray[analysisArray.length-j-1];   // reversing
                    }
                    Log.d("mergingaR I am: x:", Double.toString(i.getCoorX()));
                    Log.d("mergingaR I am: y:", Double.toString(i.getCoorY()));
                    Log.d("mergingaR my number: y:", Double.toString(i.getBlockNum()));
                    Log.d("mergingaR curreIndex: ", Integer.toString(currentBlockIndex));
                    Log.d("mergingaR array0: ", Integer.toString(analysisArray[0]));
                    Log.d("mergingaR array1: ", Integer.toString(analysisArray[1]));
                    Log.d("mergingaR array2: ", Integer.toString(analysisArray[2]));
                    Log.d("mergingaR array3: ", Integer.toString(analysisArray[3]));

                    int result = targetCalcAlgorithm(myReverseArray, currentBlockIndex);
                    result = reverseCoordinate(result);
                    Log.d("mergingaR destinatnX: ", Double.toString(result * SLOT_ISO + OFFSET_X));
                    Log.d("mergingaR destinaonY: ", Double.toString(i.getCoorY()));
                    i.setDestination(result* SLOT_ISO + OFFSET_X, i.getCoorY());

                    for (int j = 0; j < 4; j++) {
                        analysisArray[j] = 0;    // reset array
                    }

                }



            }

            else if (myDirections == Directions.Up) {

//                for (GameBlock i : myBlockList) {
//                    blockCount = 0;
//                    slotCount = 0;
//                    for (int j = 0; j < 4; j++) {
//                        if (i.getTarCoorY() + j * SLOT_ISO == i.getCoorY()) {   // if traversed to the current position of the block, exit for loop
//                            break;
//                        }
//                        if (isOccupied(i.getTarCoorX(), i.getTarCoorY() + j * SLOT_ISO)) {   // start from tartget slot and check going right
//                            //Log.d("debug info: ", "occupied!! " + i.getTarCoorX()+j*SLOT_ISO+" "+ i.getTarCoorY());
//                            blockCount++;
//                        }
//                        slotCount++;
//                    }
//                    //Log.d("debug info: ", "position " + i.getTarCoorX()+", "+i.getTarCoorY());
//                    i.setDestination(i.getCoorX(), i.getCoorY() - (slotCount - blockCount) * SLOT_ISO);
//                    //Log.d("debug info: ", "counts " + slotCount+", "+blockCount);
//                }
                for (GameBlock i : myBlockList) {
                    blockCount = 0;
                    slotCount = 0;
                    int currentBlockIndex = (int) ((i.getCoorY() - OFFSET_Y) / SLOT_ISO);
                    int analysisArray[] = new int[4];

                    for (int j = 0; j < 4; j++) {
                        if (returnBlockRef(i.getCoorX(), OFFSET_Y + j * SLOT_ISO) != null) {
                            analysisArray[j] = returnBlockRef(i.getCoorX(), OFFSET_Y + j * SLOT_ISO).getBlockNum();
                        } else {
                            analysisArray[j] = 0;
                        }
                    }

                    i.setDestination(i.getCoorX(), targetCalcAlgorithm(analysisArray, currentBlockIndex) * SLOT_ISO + OFFSET_Y);

                    for (int j = 0; j < 4; j++) {
                        analysisArray[j] = 0;    // reset array
                    }

                }

            }

            else if (myDirections == Directions.Down) {
//                for (GameBlock i : myBlockList) {
//                    blockCount = 0;
//                    slotCount = 0;
//                    for (int j = 0; j < 4; j++) {
//                        if (i.getTarCoorY() - j * SLOT_ISO == i.getCoorY()) {   // if traversed to the current position of the block, exit for loop
//                            break;
//                        }
//                        if (isOccupied(i.getTarCoorX(), i.getTarCoorY() - j * SLOT_ISO)) {   // start from tartget slot and check going right
//                            //Log.d("debug info: ", "occupied!! " + i.getTarCoorX()+j*SLOT_ISO+" "+ i.getTarCoorY());
//                            blockCount++;
//                        }
//                        slotCount++;
//                    }
//                    //.d("debug info: ", "position " + i.getTarCoorX()+", "+i.getTarCoorY());
//                    i.setDestination(i.getCoorX(), i.getCoorY() + (slotCount - blockCount) * SLOT_ISO);
//                    //Log.d("debug info: ", "counts " + slotCount+", "+blockCount);
//                }


                for (GameBlock i : myBlockList) {
                    blockCount = 0;
                    slotCount = 0;
                    int currentBlockIndex = (int) ((i.getCoorY() - OFFSET_Y) / SLOT_ISO);
                    currentBlockIndex = reverseCoordinate(currentBlockIndex);  // reverse (reflect) the coordinate of block for goinf right
                    int analysisArray[] = new int[4];

                    for (int j = 0; j < 4; j++) {
                        if (returnBlockRef(i.getCoorX(), OFFSET_Y + j * SLOT_ISO) != null) {
                            analysisArray[j] = returnBlockRef(i.getCoorX(), OFFSET_Y + j * SLOT_ISO).getBlockNum();
                        } else {
                            analysisArray[j] = 0;
                        }
                    }

                    int myReverseArray[] = new int[4];

                    for (int j = 0; j < 4; j++){
                        myReverseArray[j] = analysisArray[analysisArray.length-j-1];   // reversing
                    }


                    int result = targetCalcAlgorithm(myReverseArray, currentBlockIndex);
                    result = reverseCoordinate(result);

                    i.setDestination(i.getCoorX(), result* SLOT_ISO + OFFSET_Y );

                    for (int j = 0; j < 4; j++) {
                        analysisArray[j] = 0;    // reset array
                    }

                }

            }


//after the target destinations of all blocks are set, mark the blocks that are going to merge
        for (GameBlock i : myBlockList) {
            //Log.d("merging array test: ", Double.toString(i.getTarCoorX()));
            if (targetIsOccupied(i.getTarCoorX(), i.getTarCoorY(), i) != null) {  // if the target slot has a block
                ///////////////////////////////////////////////////////extra check ////////////////////
                //////////////////////////////////////check only rows?
                if (targetIsOccupied(i.getTarCoorX(), i.getTarCoorY(), i).getChecked() == false && i.getBlockNum() == targetIsOccupied(i.getTarCoorX(), i.getTarCoorY(), i).getBlockNum()) {   // if we have not check this block yet
                    i.setToBeRemoved(true);
                    //Log.d("merging array test: ", "block to be removed!");
                    targetIsOccupied(i.getTarCoorX(), i.getTarCoorY(), i).setChecked(true);
                    targetIsOccupied(i.getTarCoorX(), i.getTarCoorY(), i).doubleBlockNum();   // double the block number of this block that is going to merge
                }
            }
        }

        //this.createBlock();


        //Log.d("direction set debug1: ", this.myDirections.toString());
        this.myDirections = Directions.No_Movement;   // reset direction
        //Log.d("direction set debug2: ", this.myDirections.toString());

    }

    public void removeToBeRemovedBlocks(){
        // remove all toBeRemoved blocks from the linked list
        LinkedList <GameBlock> toRemove = new LinkedList ();

        for (GameBlock i : myBlockList) {   // reset check boolean; remove toBeRemoved
            i.setChecked(false);
            if (i.getToBeRemoved() == true){
                myLayout.removeView(i.getNumTV());
                myLayout.removeView(i);
                toRemove.add(i);
                //myBlockList.remove(i);

            }
        }
        myBlockList.removeAll(toRemove);
    }
    //check if this location (x,y) is occupied
    public boolean isOccupied(float x, float y){
        for (GameBlock i : myBlockList) {
            if(i.getCoorX() == x && i.getCoorY() == y) {
                return true;
            }
        }
        return false;
    }

    public GameBlock returnBlockRef (float x, float y){  // returns the object reference at a particular coordinate
        for (GameBlock i : myBlockList) {
            if(i.getCoorX() == x && i.getCoorY() == y) {
                return i;
            }
        }
        return null;
    }


    //check if another block has the same target position; if yes, returns the reference of that block
    public GameBlock targetIsOccupied(float x, float y, GameBlock i){
       // Log.d("merging array: looking:", Double.toString(x)+", "+Double.toString(y));
        for (GameBlock j : myBlockList) {

//            Log.d("merging array: parsing:", Double.toString(i.getTarCoorX())+", "+Double.toString(x));
//            Log.d("merging array: pasing2:", Double.toString(i.getTarCoorY())+", "+Double.toString(y));
            if(j.getTarCoorX() == x && j.getTarCoorY() == y && i!=j ) {
                Log.d("merging array: parsing:", "return1!!!");
                return j;
            }
        }
        return null;
    }


    /////////////////**********************Merging and Collision algorithm***************************/////////////
    public int targetCalcAlgorithm (int[] myArray, int blockPos){
        int slotCount = 0;
        int blockCount = 0;
        int currentNumApp = 0;
        int currentNum = 0;


        int newTargetPos = -1;

        for (int i = 0; i < 4; i++){
            if (myArray[i]!=0){
                blockCount++;
                if (currentNumApp == 0) {
                    currentNum = myArray[i];
                    currentNumApp++;
                }

                else if (myArray[i] != currentNum){
                    currentNum = myArray[i];
                    currentNumApp = 1;
                }

                else {
                    if (myArray[i] == currentNum){
                        currentNumApp++;
                    }
                }

            }

            if (currentNumApp == 2){
                blockCount--;
                currentNumApp = 0;
                currentNum = 0;
            }
            slotCount++;

//            System.out.println(i+"th loop: ");
//            System.out.println("slotCount: "+slotCount);
//            System.out.println("blockCount: "+blockCount);
//            System.out.println("currentNumApp: "+currentNumApp);
//            System.out.println("currentNum: "+currentNum);

            if (i == blockPos){
                blockCount--;
                slotCount--;
                newTargetPos = slotCount - blockCount;
                return blockPos-newTargetPos;
            }



        }

        return -1;

    }


    public int reverseCoordinate(int i) {
        switch (i) {
            case 0:
                i = 3;
                break;
            case 1:
                i = 2;
                break;
            case 2:
                i = 1;
                break;
            case 3:
                i = 0;
                break;
            default:
                i = -1;
        }
        return i;
    }

    public boolean noMerge(){   // check if there are anymore possible merges on the board
        int analysis2DArray[][] = new int[4][4];

        for (GameBlock i: myBlockList
                ) {
            analysis2DArray[(int)((i.getCoorY()-OFFSET_Y)/SLOT_ISO)][(int)((i.getCoorX()-OFFSET_X)/SLOT_ISO)] = i.getBlockNum();
        }

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                if (analysis2DArray[i][j] == analysis2DArray[i][j+1]){
                    return false;
                }
            }
        }

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                if (analysis2DArray[j][i] == analysis2DArray[j+1][i]){
                    return false;
                }
            }
        }

        return true;
    }


    /* boundaries:
        top left: 0,0
        top right: 810, 0
        bottom left: 0, 810
        bottom right: 810, 810
    */

    /*block coordinates:
        -60 210 480 750
     -60
     210
     480
     750
     */

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public void run(){
        //Log.d("boolean array moving?", Boolean.toString(moving));
        //this.myGameBlock.setBlockDirection(Directions.No_Movement);

        myActivity.runOnUiThread(
                new Runnable() {
                    public void run(){
                        for (GameBlock i : myBlockList) {
                            i.move();
                        }
                        for (GameBlock i : myBlockList) {
                            i.setBlockNum(i.getBlockNum());
                        }

                    }
                }
        );

    }
}
