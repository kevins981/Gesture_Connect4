package uwaterloo.ca.lab4_204_15a;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Kevin on 2017/7/9.
 */
public class MergeTestTest {
    MergeTest myTest;
    int position;
    int myArray[] = {4, 0, 4, 0};

    @Before
    public void setUp(){
        position = 2;
        myTest = new MergeTest();

    }

    @Test
    public void Test1(){
        assertEquals("testi 1:",0, myTest.Test1(myArray, position), 0.001);

    }

}