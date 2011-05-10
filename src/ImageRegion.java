import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: 3/28/11
 * Time: 2:09 PM
 * To change this template use File | Settings | File Templates.
 *
 * Given a 2D array of pixels numbered (0,0) for the upper left corner, this
 * class describes a subset of pixels enclosed by a rectangle with sides
 * parallel / perpendicular to the edges of the 2d array.
 *
 * This class is primarily used to indicate zoom boxes that the user has
 * selected with a mouse which are then translated to ComplexRegion objects
 * for zooming into the next picture to be displayed.
 *
 * Requirement 1.1.12 Zooming
 * This class allows a subsection of the image coordinates of the full picture
 * (as described in subclass ImageSize) to be selected. Necessary to select
 * an area for zooming.
 *
 * Subclassed by ImageSize.
 */
public class ImageRegion implements Serializable {

    private final Pixel upperLeft;
    private final Pixel lowerRight;

    /**
     * The input coordinates are given relative to the upper left corner (0,0)
     * of a 2d pixel array. The coordinates should be opposite corners (they
     * should not reside on the same line segment).
     *
     * @param point1
     * @param point2
     */
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
        return upperLeft.getX();
    }

    public int getYMin(){
        return upperLeft.getY();
    }

    public int getXMax(){
        return lowerRight.getX();
    }

    public int getYMax(){
        return lowerRight.getY();
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
