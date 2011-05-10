import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: 3/28/11
 * Time: 2:02 PM
 * To change this template use File | Settings | File Templates.
 *
 * Represents an immutable rectangular area of a complex plane.
 *
 * Requirement 1.0.0 The program should display a basic fractal image.
 * This class bounds an area of the complex plane for which the fractal
 * calculation will be applied, colored, and displayed to the user. Necessary
 * because the plane has infinite extent, so a subsection must be chosen
 * to calculate.
 */
public class ComplexRegion implements Serializable {

    private final ComplexNumber upperLeft;
    private final ComplexNumber lowerRight;

    /**
     * todo: make sure input corners are not reversed
     *
     * @param upperLeft
     * @param lowerRight
     */
    public ComplexRegion(ComplexNumber upperLeft, ComplexNumber lowerRight) {
        this.upperLeft = upperLeft;
        this.lowerRight = lowerRight;
    }

    public ComplexNumber getUpperLeft() {
        return upperLeft;
    }

    public ComplexNumber getLowerRight() {
        return lowerRight;
    }

    @Override
    public String toString() {
        return "ComplexRegion{" +
                "upperLeft=" + upperLeft +
                ", lowerRight=" + lowerRight +
                '}';
    }

    public double getRealMin(){
        return upperLeft.getReal();
    }

    public double getRealMax(){
        return lowerRight.getReal();
    }

    public double getImagMin(){
        return lowerRight.getImag();
    }

    public double getImagMax(){
        return upperLeft.getImag();
    }

    /**
     * This method returns the distance between two points if the real axis is to be
     * divided into the input number of discrete contiguous chunks.
     *
     * @param res
     * @return
     */
    public double getRealDelta(final int res){
        return getDelta(res, true);
    }

    /**
     * This method returns the distance between two points if the imaginary axis is to be
     * divided into the input number of discrete contiguous chunks.
     *
     * @param res
     * @return
     */
    public double getImagDelta(final int res){
        return getDelta(res, false);
    }

    /**
     * Gives the average of getRealDelta() and getImaginaryDelta() based on the width and
     * height specified by input ImageSize. Useful for ensuring a locked aspect ratio.
     *
     * The returned result is a simple average and not weighted.
     *
     * @param size
     * @return
     */
    public double getAverageDelta(final ImageSize size){
        return (getRealDelta(size.getWidth()) + getImagDelta(size.getHeight()))/2.0;
    }

    /**
     * For the ComplexRegion represented by this object, overlay a plane of pixels represented by ImageSize size
     * and then map the input Pixel pixel to the ComplexNumber under the pixel.
     *
     * @param pixel
     * @param size
     * @param forceSameAspectRatio
     * @return
     */
    public ComplexNumber getComplexPointFromPixel(Pixel pixel, ImageSize size, boolean forceSameAspectRatio){
        final double realDelta, imagDelta;
        if(forceSameAspectRatio) {
            realDelta = imagDelta = getAverageDelta(size);
        } else {
            realDelta = getRealDelta(size.getWidth());
            imagDelta = getImagDelta(size.getHeight());
        }
        return new ComplexNumber(
            getRealMin() + pixel.getX() * realDelta,
            getImagMax() - pixel.getY() * imagDelta
        );
    }

    /**
     * Inverse of getComplexPointFromPixel().
     *
     * @param complexNumber
     * @param imageSize
     * @return
     */
    public Pixel getPixelFromComplexPoint(ComplexNumber complexNumber, ImageSize imageSize){
        return new Pixel(
            (int)((complexNumber.getReal() - getRealMin())/getRealDelta(imageSize.getWidth())),
            (int)((getImagMax() - complexNumber.getImag())/getImagDelta(imageSize.getHeight()))
        );
    }

    /**
     * Helper method for the axis specific getDelta functions to make sure they are implemented
     * in the same way.
     *
     * @param resolution the number of discrete contiguous chunks to divide the axis being considered into
     * @param real
     * @return
     */
    private double getDelta(final int resolution, final boolean real){
        assert resolution > 0;

        final double distance =
            real ? getRealMax() - getRealMin()
                 : getImagMax() - getImagMin()
        ;

        assert distance >= 0.0;

        return distance / ((double) resolution);
    }
}
