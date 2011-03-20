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
    // todo: improve region program initially renders
    private final double realMinimum;
    private final double imaginaryMaximum;
    private final double realMaximum;
    private final double imaginaryMinimum;

    // distance between pixels
    // if the aspect ratio of the logical picture does not match that of
    // the rendered (on screen), then you need two separate deltas:
    // realDelta and imaginaryDelta
    private final double delta;

    private final int countOfXPixels;
    private final int countOfYPixels;

    private final MandelPoint[][] mandelPoints;

    // todo: increase iterationMax as picture is zoomed
    private int iterationMax = 100;

    public MandelCanvas(final double realMinimum, final double imaginaryMaximum, final double realMaximum,
            final double imaginaryMinimum, final double delta,
            final int xResolution, final int yResolution)
    {
        this.delta = delta;
        this.imaginaryMinimum = imaginaryMinimum;
        this.realMaximum = realMaximum;
        this.realMinimum = realMinimum;
        this.imaginaryMaximum = imaginaryMaximum;

        countOfXPixels = xResolution;
        countOfYPixels = yResolution;
        mandelPoints = new MandelPoint[xResolution][yResolution];

        for(int x = 0; x < xResolution; ++x)
            for(int y = 0; y < yResolution; ++y)
                mandelPoints[x][y] = new MandelPoint(realMinimum + x * delta, imaginaryMaximum - y * delta);

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
    public MandelCanvas doZoom(Point upperLeftCorner, Point lowerRightCorner){
        // swap the click points if user clicked lower right corner before upper left corner
        if(upperLeftCorner.getX() > lowerRightCorner.getX() || upperLeftCorner.getY() > lowerRightCorner.getY()){
            Point tmp = lowerRightCorner;
            lowerRightCorner = upperLeftCorner;
            upperLeftCorner = tmp;
        }
        // translate first click into a complex number
        final double next_realMinimum = realMinimum + upperLeftCorner.getX() * delta;
        final double next_imaginaryMaximum = imaginaryMaximum - upperLeftCorner.getY() * delta;
        // translate second click into a complex number
        final double next_realMaximum = realMinimum + lowerRightCorner.getX() * delta;
        final double next_imaginaryMinimum = imaginaryMaximum - lowerRightCorner.getY() * delta;
        // delta - the complex distance between pixels - must be recalculated because we're zooming in
        // having real & imaginary axis share a delta keeps the aspect ratio correct
        final double next_delta = ((realMaximum - realMinimum)/countOfXPixels + (imaginaryMaximum - imaginaryMinimum)/countOfYPixels)/2.0;

        return new MandelCanvas(
            next_realMinimum,
            next_imaginaryMaximum,
            next_realMaximum,
            next_imaginaryMinimum,
            next_delta,
            countOfXPixels,
            countOfYPixels
        );
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
        final BufferedImage img = new BufferedImage(countOfXPixels, countOfYPixels, BufferedImage.TYPE_INT_RGB);
        for(int x = 0; x < countOfXPixels; ++x)
            for(int y = 0; y < countOfYPixels; ++y)
                img.setRGB(x, y, getColorAtPoint(x, y).getRGB());
        return img;
    }

    public Object[][] getAttributeValues(){
        return new Object[][] {
            { "real (x) min: ", realMinimum },
            { "real (x) max: ", realMaximum },
            { "imaginary (y) min: ", imaginaryMinimum },
            { "imaginary (y) max: ", imaginaryMaximum },
            { "delta: ", delta },
            { "iteration limit: ", iterationMax },
            { "logical x resolution: ", countOfXPixels },
            { "logical y resolution: ", countOfYPixels }
        };
    }

    public int getIterationMax() {
        return iterationMax;
    }

    public void setIterationMax(int iterationMax) {
        this.iterationMax = iterationMax;
    }

}
