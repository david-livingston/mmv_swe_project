import java.awt.*;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: 3/28/11
 * Time: 2:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class ImageRegion implements Serializable {

    private final Pixel upperLeft;
    private final Pixel lowerRight;

    public ImageRegion(final Pixel point1, final Pixel point2) {
        final int xmin, xmax, ymin, ymax;

        if(point1.getX() < point2.getX()) {
            xmin = point1.getX();
            xmax = point2.getX();
        } else {
            xmin = point2.getX();
            xmax = point1.getX();
        }

        if(point1.getY() < point2.getY()) {
            ymin = point1.getY();
            ymax = point2.getY();
        } else {
            ymin = point2.getY();
            ymax = point1.getY();
        }

        upperLeft = new Pixel(xmin, ymin);
        lowerRight = new Pixel(xmax, ymax);
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

    public Pixel getUpperLeftCorner() {
        return upperLeft;
    }

    public Pixel getLowerRightCorner() {
        return lowerRight;
    }

    @Override
    public String toString() {
        return "ImageRegion{" +
                "upperLeft=" + upperLeft +
                ", lowerRight=" + lowerRight +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImageRegion that = (ImageRegion) o;

        if (lowerRight != null ? !lowerRight.equals(that.lowerRight) : that.lowerRight != null) return false;
        if (upperLeft != null ? !upperLeft.equals(that.upperLeft) : that.upperLeft != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = upperLeft != null ? upperLeft.hashCode() : 0;
        result = 31 * result + (lowerRight != null ? lowerRight.hashCode() : 0);
        return result;
    }
}
