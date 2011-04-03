import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: 3/28/11
 * Time: 2:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class ImageRegion {

    private final Pixel upperLeft;
    private final Pixel lowerRight;

    public ImageRegion(Pixel upperLeft, Pixel lowerRight) {
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

    public int getWidth(){
        return getXMax() - getXMin();
    }

    public int getHeight(){
        return getYMax() - getYMin();
    }

    @Override
    public String toString() {
        return "ImageRegion{" +
                "upperLeft=" + upperLeft +
                ", lowerRight=" + lowerRight +
                '}';
    }
}
