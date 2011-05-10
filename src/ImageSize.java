import java.awt.*;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: 4/3/11
 * Time: 11:51 AM
 * To change this template use File | Settings | File Templates.
 *
 * Encapsulates information about the size of a 2d array of pixels, with the
 * upper left corner being considered (0,0).
 *
 * This is imply a constrained extension of ImageRegion (with upper left
 * corner fixed at (0,0)). Class is immutable like ImageRegion.
 *
 * Requirement 1.0.0 The program should display a basic fractal image.
 * This class specifies how many pixels will be used in calculating the set.
 * Because the set is infinite in size, it is necessary to specify how frequently
 * it is sampled (on both axises) to generate an image which approximates the set.
 *
 * Requirement 1.1.0 GUI
 * Various aspects of the GUI use an ImageSize object to describe their size (this
 * program uses ImageSize in many places as a replacement for the Dimension class).
 *
 * Requirement 1.1.3 Thumbnail window
 * Requirement 1.1.7 PNG image saving
 * Requirement 1.1.8 Save state file (ImageSize used in serialization format)
 *
 * Requirement 1.1.12 Zooming
 * This class provides methods for ensuring the zoomed into region will have the
 * same aspect ratio as the original image.
 *
 * Requirement 1.2.0 GUI enhancements
 * Class supports this objective in a number of ways. For example, it is used to
 * ensure the aspect ratio of the image displayed to the user remains correct as
 * the render window is resized.
 *
 * Requirement 1.2.2 Decouple Displayed Image Size from Logical Image Size
 * This class provides functionality for calculating ImageSize objects of different
 * size but with the same aspect ratio. This supports decoupling while maintaining
 * similarity between the images.
 *
 * Requirement 1.2.2 HD rendering
 * This class has static instances describing various resolutions.
 */
public class ImageSize extends ImageRegion implements Serializable {

    public final static ImageSize GREAT_MONITOR = new ImageSize(1600, 2560);
    public final static ImageSize REAL_HD = new ImageSize(1080, 1920);
    public final static ImageSize FAKE_HD = new ImageSize(720, 1280);
    public final static ImageSize EXAMPLE_NTSC_4_to_3_SD = new ImageSize(480, 640);
    public final static ImageSize DVD = new ImageSize(480, 720);
    public final static ImageSize EXAMPLE_NTSC_16_to_9_SD = new ImageSize(480, 872);

    /**
     * Notice although this class is similar to the java standard class
     * Dimension, the constructor params are not in the same order.
     *
     * @param height
     * @param width
     */
    public ImageSize(final int height, final int width){
        super(
            new Pixel(0, 0),
            new Pixel(width, height)
        );
    }

    /**
     * Takes an ImageRegion and returns an adjusted version with dimensions adjusted so it
     * matches the aspect ratio of this object. The param anchor should be one of the for
     * corners of the param input, and the returned adjusted ImageRegion will still have
     * that point as a corner.
     *
     * Useful in adjusting zoom boxes and image resizes so aspect ratio is maintained.
     *
     * @param anchor
     * @param input
     * @return
     */
    public ImageRegion adjustImageRegionAspectRatio(final Pixel anchor, final ImageRegion input){
        assert anchor.getX() == input.getXMax() || anchor.getX() == input.getXMin();
        assert anchor.getY() == input.getYMax() || anchor.getY() == input.getYMin();

        int newHeight = (int) (heightToWidth() * input.getWidth());

        final int otherX = anchor.getX() == input.getXMax() ? input.getXMin() : input.getXMax();
        final int otherY = anchor.getY() == input.getYMax() ? anchor.getY() - newHeight : anchor.getY() + newHeight;

        return new ImageRegion(anchor, new Pixel(otherX, otherY));
    }

    /**
     * Useful because many Swing methods take Dimension objects as input and
     * the Dimension class has a different constructor order than this class.
     *
     * @return this object (at least the height & width) represented as a
     *  Dimension object
     */
    public Dimension asDimension(){
        return new Dimension(getWidth(), getHeight());
    }

    /**
     * @param dimension height and width attributes in a Dimension object
     * @return the equivalent ImageSize
     */
    public static ImageSize fromDimension(Dimension dimension){
        return new ImageSize((int)dimension.getHeight(), (int)dimension.getWidth());
    }

    /**
     * @return an aspect ratio for this object (width divided by height)
     */
    public double widthToHeight(){
        return ((double)getWidth())/getHeight();
    }

    /**
     * @return the aspect ratio of this object (height to width)
     */
    public double heightToWidth(){
        return ((double)getHeight())/getWidth();
    }

    /**
     * @return The number of pixels contained by an image with this object's
     *  dimensions.
     */
    public int pixelCount(){
        return getHeight() * getWidth();
    }

    /**
     * Compares this object to the input ImageSize and returns whether this
     *  object is larger.
     *
     * @param rightHandSide
     * @return
     */
    public boolean largerThan(ImageSize rightHandSide){
        return pixelCount() > rightHandSide.pixelCount();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImageSize that = (ImageSize) o;

        return getHeight() == that.getHeight() && getWidth() == that.getWidth();
    }

    @Override
    public int hashCode() {
        return 31 * (getHeight() + 3 * getWidth());
    }

    @Override
    public String toString(){
        return StringFormats.strFromInt(getWidth()) + " x " + StringFormats.strFromInt(getHeight());
    }
}
