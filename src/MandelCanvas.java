import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: David
 * Date: Feb 13, 2011
 * Time: 7:44:16 PM
 * To change this template use File | Settings | File Templates.
 */

/*
 * Class describes a region of the Mandelbrot set (zoomed out view initially),
 * the number of pixels used to represent that area and an equal number of
 * MandelPoints, and the BufferedImage entailed by mapping those pixels to
 * colors with Palette.java.
 *
 * Most of the associated GUI code for this picture is in MandelJPanel.java.
 */
public class MandelCanvas  implements Serializable {

    // describes the region of the Mandelbrot set to be displayed
    private final ComplexRegion renderRegion;

    private final ImageSize imageSize;

    private final MandelPoint[][] mandelPoints;

    // todo: increase iterationMax as picture is zoomed
    private int iterationMax = 100;

    public MandelCanvas(final ComplexRegion renderRegion, final ImageSize imageSize)
    {
        this.renderRegion = renderRegion;
        this.imageSize = imageSize;

        mandelPoints = new MandelPoint[imageSize.getWidth()][imageSize.getHeight()];

        for(int x = 0; x < imageSize.getWidth(); ++x)
            for(int y = 0; y < imageSize.getHeight(); ++y)
                mandelPoints[x][y] = new MandelPoint(renderRegion.getComplexPointFromPixel(new Pixel(x, y), imageSize, true));
    }

    /**
     * Maps a pixel to a MandelPoint to a Color (per Palette.java); ensures
     * the pixel has been iterated before returning result.
     *
     * @param x horizontal offset of pixel from top-left (0,0)
     * @param y vertical offset of pixel from top-left (0,0)
     * @return the color at input pixel
     */
    Color getColorAtPoint(final int x, final int y){
        final MandelPoint m = mandelPoints[x][y];
        m.iterate(iterationMax);
        return Palette.getColor(m);
    }

    /**
     * Alters this object to describe a different area of the Mandelbrot set
     * per the region described by the input pixels (mouse clicks).
     *
     * TODO: !!! fix zooming to unexpected places, maybe lock the zoombox in JPanel again
     * todo: spawn new threads to do this (here might not be the best place)
     * todo: fix recalculating region to maintain aspect ratio
     * todo: refactor param names
     *
     * @param upperLeftCorner first click of user (may not actually be upperleftcorner)
     * @param lowerRightCorner second click of user
     * @return
     */
    public MandelCanvas doZoom(Pixel upperLeftCorner, Pixel lowerRightCorner){
        // swap the click points if user clicked lower right corner before upper left corner
        if(upperLeftCorner.getX() > lowerRightCorner.getX() || upperLeftCorner.getY() > lowerRightCorner.getY()){
            Pixel tmp = lowerRightCorner;
            lowerRightCorner = upperLeftCorner;
            upperLeftCorner = tmp;
        }
        // translate first click into a complex number
        final ComplexNumber translated_upperLeftCorner = pointToCoordinates(upperLeftCorner);
        final double next_realMinimum = translated_upperLeftCorner.getReal();
        final double next_imaginaryMaximum = translated_upperLeftCorner.getImag();
        // translate second click into a complex number
        final ComplexNumber translated_lowerRightCorner = pointToCoordinates(lowerRightCorner);
        final double next_realMaximum = translated_lowerRightCorner.getReal();
        final double next_imaginaryMinimum = translated_lowerRightCorner.getImag();

        return new MandelCanvas(
            new ComplexRegion(
                new ComplexNumber(next_realMinimum, next_imaginaryMaximum),
                new ComplexNumber(next_realMaximum, next_imaginaryMinimum)
            ),
            imageSize
        );
    }

    public ComplexNumber pointToCoordinates(Pixel pixel){
        return renderRegion.getComplexPointFromPixel(pixel, imageSize, true);
    }

    public Pixel coordinatesToPoint(ComplexNumber coordinates){
        return renderRegion.getPixelFromComplexPoint(coordinates, imageSize);
    }

    /**
     * get an image with pixel data based on the MandelPoint's contained in the
     * described region
     *
     * it might be best to cache the buffered image but the JPanel that uses
     * this to display to the screen already (i think) caches it so it really
     * only affects saving the file
     *
     * @return the mandelbrot data, colored per Palette.java, as far as
     *  currently calculated
     */
    public BufferedImage getAsBufferedImage(){
        final BufferedImage img = new BufferedImage(imageSize.getWidth(), imageSize.getHeight(), BufferedImage.TYPE_INT_RGB);
        for(int x = 0; x < imageSize.getWidth(); ++x)
            for(int y = 0; y < imageSize.getHeight(); ++y)
                img.setRGB(x, y, getColorAtPoint(x, y).getRGB());
        return img;
    }

    public Object[][] getAttributeValues(){
        return new Object[][] {
            { "real (x) min: ", renderRegion.getRealMin() },
            { "real (x) max: ", renderRegion.getRealMax() },
            { "imaginary (y) min: ", renderRegion.getImagMin() },
            { "imaginary (y) max: ", renderRegion.getImagMax() },
            { "delta: ", renderRegion.getAverageDelta(imageSize) },
            { "iteration limit: ", iterationMax },
            { "logical x resolution: ", imageSize.getWidth() },
            { "logical y resolution: ", imageSize.getHeight() }
        };
    }

    public int getIterationMax() {
        return iterationMax;
    }

    public void setIterationMax(int iterationMax) {
        this.iterationMax = iterationMax;
    }

    public ComplexRegion getAsComplexRectangle(){
        return renderRegion;
    }

}
