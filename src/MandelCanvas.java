import java.awt.*;
import java.awt.geom.AffineTransform;
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

    private final ComplexRegion renderRegion;

    // todo: increase iterationMax as picture is zoomed
    private int iterationMax = 100;

    private transient MandelPoint[][] mandelPoints;
    private transient BufferedImage logicalBufferedImage;
    private transient BufferedImage displayedBufferedImage;
    private ImageSize logicalImageSize;
    private ImageSize displayImageSize;

    public MandelCanvas(final ComplexRegion renderRegion, final ImageSize requestedLogicalImageSize, final ImageSize requestedDisplayImageSize)
    {
        this.renderRegion = renderRegion;
        logicalImageSize = requestedLogicalImageSize;
        displayImageSize = requestedDisplayImageSize;

        mandelPoints = new MandelPoint[logicalImageSize.getWidth()][logicalImageSize.getHeight()];

        for(int x = 0; x < logicalImageSize.getWidth(); ++x)
            for(int y = 0; y < logicalImageSize.getHeight(); ++y)
                mandelPoints[x][y] = new MandelPoint(renderRegion.getComplexPointFromPixel(new Pixel(x, y), logicalImageSize, true));

        initLogicalBufferedImage();
        initDisplayBufferedImage();
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
            logicalImageSize,
            displayImageSize
        );
    }

    public ComplexNumber pointToCoordinates(Pixel pixel){
        return renderRegion.getComplexPointFromPixel(pixel, displayImageSize, true);
    }

    public Pixel coordinatesToPoint(ComplexNumber coordinates){
        return renderRegion.getPixelFromComplexPoint(coordinates, displayImageSize);
    }

    public BufferedImage getDisplayedBufferedImage(ImageSize requestedDisplayImageSize){
        if(displayImageSize.equals(requestedDisplayImageSize))
            return displayedBufferedImage;

        displayImageSize = requestedDisplayImageSize;
        initDisplayBufferedImage();
        return displayedBufferedImage;
    }

    public BufferedImage getLogicalBufferedImage(){
        return logicalBufferedImage;
    }

    private void initLogicalBufferedImage(){
        logicalBufferedImage = new BufferedImage(logicalImageSize.getWidth(), logicalImageSize.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for(int x = 0; x < logicalImageSize.getWidth(); ++x)
            for(int y = 0; y < logicalImageSize.getHeight(); ++y)
                logicalBufferedImage.setRGB(x, y, getColorAtPoint(x, y).getRGB());
    }

    private void initDisplayBufferedImage(){
        // http://helpdesk.objects.com.au/java/how-do-i-scale-a-bufferedimage
        final double scaleX = displayImageSize.getWidth()/((double)logicalImageSize.getWidth());
        final double scaleY = displayImageSize.getHeight()/((double)logicalImageSize.getHeight());
        displayedBufferedImage = new BufferedImage(displayImageSize.getWidth(), displayImageSize.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = displayedBufferedImage.createGraphics();
        AffineTransform xform = AffineTransform.getScaleInstance(scaleX, scaleY);
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        graphics2D.drawImage(logicalBufferedImage, xform, null);
        graphics2D.dispose();
    }

    public Object[][] getAttributeValues(){
        return new Object[][] {
            { "real (x) min: ", renderRegion.getRealMin() },
            { "real (x) max: ", renderRegion.getRealMax() },
            { "imaginary (y) min: ", renderRegion.getImagMin() },
            { "imaginary (y) max: ", renderRegion.getImagMax() },
            { "delta: ", renderRegion.getAverageDelta(logicalImageSize) },
            { "iteration limit: ", iterationMax },
            { "logical x resolution: ", logicalImageSize.getWidth() },
            { "logical y resolution: ", logicalImageSize.getHeight() },
            { "logical x/y ratio: ", logicalImageSize.getWidth()/((double)logicalImageSize.getHeight()) },
            { "displayed x resolution: ", displayImageSize.getWidth() },
            { "displayed y resolution: ", displayImageSize.getHeight() },
            { "displayed x/y ratio: ", displayImageSize.getWidth()/((double)displayImageSize.getHeight()) },
            { "processor count: ", Runtime.getRuntime().availableProcessors() },
            { "total memory: ", Runtime.getRuntime().totalMemory() },
            { "max memory: ", Runtime.getRuntime().maxMemory() },
            { "free memory: ", Runtime.getRuntime().freeMemory() }
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
