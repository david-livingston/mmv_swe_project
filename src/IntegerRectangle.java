import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: 3/28/11
 * Time: 2:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class IntegerRectangle {

    private final Point upperLeft;
    private final Point lowerRight;

    public IntegerRectangle(Point upperLeft, Point lowerRight) {
        this.upperLeft = upperLeft;
        this.lowerRight = lowerRight;
    }

    public int getXMin(){
        return (int) upperLeft.getX();
    }

    public int getYMin(){
        return (int) upperLeft.getY();
    }

    public int getXMax(){
        return (int) lowerRight.getX();
    }

    public int getYMax(){
        return (int) lowerRight.getY();
    }

    @Override
    public String toString() {
        return "IntegerRectangle{" +
                "upperLeft=" + upperLeft +
                ", lowerRight=" + lowerRight +
                '}';
    }
}
