import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;

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
    private int iterationMax;

    private transient MandelPoint[][] mandelPoints;
    private transient BufferedImage logicalBufferedImage;
    private transient BufferedImage displayedBufferedImage;
    private ImageSize logicalImageSize;
    private ImageSize displayImageSize;

    private transient boolean isLightWeight = true;

    private Component component = null;

    private Palette palette;

    /**
     * OBJECT IS CONSTRUCTED IN LIGHTWEIGHT STATE
     *
     * @param renderRegion
     * @param requestedLogicalImageSize
     * @param requestedDisplayImageSize
     */
    public MandelCanvas(final ComplexRegion renderRegion, final ImageSize requestedLogicalImageSize, final ImageSize requestedDisplayImageSize, final int initialCounterMax, final Palette initialColorPalette) {
        this.renderRegion = renderRegion;
        logicalImageSize = requestedLogicalImageSize;
        displayImageSize = requestedDisplayImageSize;
        iterationMax = initialCounterMax;
        palette = initialColorPalette;
    }

    /**
     * Determines whether the most memory intensive attributes of this object
     * (the 2d array of MandelPoints and the two BufferedImage s) should be
     * discarded to save memory.
     *
     * Any method accessing one of the possibly discarded attributes should
     * call setLightWeight(false) to ensure the values are recalculated if
     * necessary. Since objects are constructed in the lightweight state,
     * most of their state is lazy evaluated.
     *
     * @param lightWeight
     */
    public void setLightWeight(final boolean lightWeight){
        if(lightWeight == isLightWeight)
            return; // object in requested state, no action necessary

        isLightWeight = lightWeight;
        if(lightWeight) {
            mandelPoints = null;
            displayedBufferedImage = null;
            logicalBufferedImage = null;
        } else
            calcLightWeightAttributes();
    }

    public void calcLightWeightAttributes(){
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
     * NOTE: this method assumes the object is not in a lightweight state.
     * Array access violations will occur if this assumption is violated.
     *
     * @param x horizontal offset of pixel from top-left (0,0)
     * @param y vertical offset of pixel from top-left (0,0)
     * @return the color at input pixel
     */
    private Color getColorAtPoint(final int x, final int y){
        final MandelPoint m = mandelPoints[x][y];
        m.iterate(iterationMax);
        return palette.getColor(m);
    }

    public MandelCanvas toZoomedCanvas(final ImageRegion screenSelection){
        return new MandelCanvas(
            new ComplexRegion(
                pointToCoordinates(screenSelection.getUpperLeftCorner()),
                pointToCoordinates(screenSelection.getLowerRightCorner())
            ),
            logicalImageSize,
            displayImageSize,
            iterationMax,
            palette
        );
    }

    public ComplexNumber pointToCoordinates(Pixel pixel){
        return renderRegion.getComplexPointFromPixel(pixel, displayImageSize, true);
    }

    public Pixel coordinatesToPoint(ComplexNumber coordinates){
        return renderRegion.getPixelFromComplexPoint(coordinates, displayImageSize);
    }

    public BufferedImage getDisplayedBufferedImage(ImageSize requestedDisplayImageSize){
        setLightWeight(false);

        // unless the windows has been resized and the requested size of the new display
        // image has changed, the cached value of 'displayBufferedImage' should still
        // be accurate
        if(displayImageSize.equals(requestedDisplayImageSize))
            return displayedBufferedImage;

        displayImageSize = requestedDisplayImageSize;
        initDisplayBufferedImage();
        return displayedBufferedImage;
    }

    public BufferedImage getLogicalBufferedImage(){
        setLightWeight(false);
        return logicalBufferedImage;
    }

    /**
     * Constructs an image with one pixel per MandelPoint based on attribute 'logicalImageSize'.
     * This is necessary when the object is in a lightweight state (including when first
     * constructed), but must be converted to a fully calculated state.
     *
     * This must be called before 'initDisplayBufferedImage()' because the displayBufferedImage
     * is a resized version of the logicalBufferedImage.
     */
    private void initLogicalBufferedImage(){
        try{
            if(null != component)
                component.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            logicalBufferedImage = new BufferedImage(logicalImageSize.getWidth(), logicalImageSize.getHeight(), BufferedImage.TYPE_INT_ARGB);

            // todo - there has to be a way to get notified with a callback upon thread termination
            // rather than sleeping & polling to check if it's done
            // that would also allow the rest of this code to continue & the GUI to become immediately
            // responsive again
            Thread old = Thread.currentThread();
            Thread render = new Thread(new RenderThreadManager(this));
            render.start();

            do {
                old.sleep(400);
            } while (render.isAlive());
        } catch (Exception e) {
            Static.log.nonFatalException("", e);
        } finally {
            if(null != component)
                component.setCursor(Cursor.getDefaultCursor());
        }
    }

    public void initLogicalBufferedImageColumn(final int column){
        for(int y = 0; y < logicalImageSize.getHeight(); ++y)
            logicalBufferedImage.setRGB(column, y, getColorAtPoint(column, y).getRGB());
    }

    public int getColumnCount(){
        return logicalImageSize.getWidth();
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

    public int getIterationMax() {
        return iterationMax;
    }

    public void setIterationMax(int iterationMax) {
        this.iterationMax = iterationMax;
        initLogicalBufferedImage();
        initDisplayBufferedImage();
    }

    public ComplexRegion getAsComplexRectangle(){
        return renderRegion;
    }

    public ImageSize getLogicalImageSize() {
        return logicalImageSize;
    }

    public void increaseIterationMax() {
        setIterationMax(iterationMax + (int)Math.pow((double)iterationMax, 1.2));
        initLogicalBufferedImage();
        initDisplayBufferedImage();
    }

    public void setComponent(Component component) {
        this.component = component;
    }

    public void setPalette(String name){
        palette = new PaletteSet().getPalette(name);
        initLogicalBufferedImage();
        initDisplayBufferedImage();
    }

    public ComplexRegion getRenderRegion() {
        return renderRegion;
    }

    public ImageSize getDisplayImageSize() {
        return displayImageSize;
    }

    public Palette getPalette() {
        return palette;
    }

    public double getDelta(){
        return renderRegion.getAverageDelta(logicalImageSize);
    }
}
