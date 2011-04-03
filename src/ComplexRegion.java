import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: 3/28/11
 * Time: 2:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class ComplexRegion {

    private final ComplexNumber upperLeft;
    private final ComplexNumber lowerRight;

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

    public double getRealDelta(final int res){
        return getDelta(res, true);
    }

    public double getImagDelta(final int res){
        return getDelta(res, false);
    }

    public double getAverageDelta(final ImageSize size){
        return (getRealDelta(size.getWidth()) + getImagDelta(size.getHeight()))/2.0;
    }

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

    public Pixel getPixelFromComplexPoint(ComplexNumber complexNumber, ImageSize imageSize){
        return new Pixel(
            (int)((complexNumber.getReal() - getRealMin())/getRealDelta(imageSize.getWidth())),
            (int)((getImagMax() - complexNumber.getImag())/getImagDelta(imageSize.getHeight()))
        );
    }

    private double getDelta(final int resolution, final boolean real){
        Global.checkArgument(resolution > 0);

        final double distance =
            real ? getRealMax() - getRealMin()
                 : getImagMax() - getImagMin()
        ;

        Global.assertState(distance >= 0.0);

        return distance / ((double) resolution);
    }
}
