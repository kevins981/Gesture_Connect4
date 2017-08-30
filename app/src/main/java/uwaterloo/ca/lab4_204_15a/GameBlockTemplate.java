package uwaterloo.ca.lab4_204_15a;

import android.content.Context;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by Kevin on 2017/7/12.
 */

public abstract class GameBlockTemplate extends android.support.v7.widget.AppCompatImageView {
    public GameBlockTemplate(Context myContext) {   // constructor
        super(myContext);
    }

    abstract void doubleBlockNum();
    abstract void move();

    abstract float getCoorX ();

    abstract float getCoorY ();

    abstract float getTarCoorX ();

    abstract float getTarCoorY ();

    abstract void setBlockDirection(GameLoopTask.Directions myBlockDirection);
    abstract void setDestination(float x, float y);


}
