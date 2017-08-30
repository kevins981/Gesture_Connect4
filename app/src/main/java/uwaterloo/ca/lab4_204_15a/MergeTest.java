package uwaterloo.ca.lab4_204_15a;

import android.util.Log;

/**
 * Created by Kevin on 2017/7/9.
 */

public class MergeTest {

    int slotCount = 0;
    int blockCount = 0;
    int currentNumApp = 0;
    int currentNum = 0;

    int myArray[] = new int [4];
    int blockPos;

    int newTargetPos = -1;

    public int Test1 (int[] myArray, int blockPos){

        this.myArray = myArray;
        this.blockPos = blockPos;

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


    public static void main (String args[]){
        MergeTest myTest =new MergeTest();
        int position =1;
        int myArray[] = {2, 4, 8, 2};
        int myReverseArray[] = new int[4];

        for (int i = 0; i < 4; i++){
            myReverseArray[i] = myArray[myArray.length-i-1];
        }

        switch (position){
            case 0:
                position = 3;
                break;
            case 1:
                position =2;
                break;
            case 2:
                position = 1;
                break;
            case 3:
                position = 0;
                break;
            default:
                position = -1;
        }

        int result =myTest.Test1(myReverseArray, position);
        //System.out.println(result);
        switch (result){
            case 0:
                result = 3;
                break;
            case 1:
                result =2;
                break;
            case 2:
                result = 1;
                break;
            case 3:
                result = 0;
                break;
            default:
                result = -1;
        }

        System.out.println(result);
    }

}
