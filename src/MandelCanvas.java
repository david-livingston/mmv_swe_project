import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;

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
 *
 * TODO: class needs refactoring, two types of attributes, always calculated and
 * lightweight (may or may not be calculated). The need for this has been
 * reduced since the introduction of the SaveableState class. Separate all
 * attributes into two sets: TransientState (new class) and SaveableState.
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

    private MandelJPanel mJPanel = null;

    private Palette palette;

    private int prisonerCount = 0;
    private int escapeeCount = 0;

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

    /**
     * Calculates, and stores, values for light-weight attributes.
     *
     * This may be necessary because the object has just been constructed or
     * because it was set to a light-weight state (presumably to reduce
     * memory requirements).
     */
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
     * This method calls m.iterate() which will perform the Mandelbrot
     * calculation if needed.
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

    /**
     * Given a selected area on this canvas, returns a new MandelCanvas bounded
     * by that selection.
     *
     * @param screenSelection
     * @return
     */
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

    /**
     * Referencing the ImageSize displayed on screen, map a pixel location
     * (probably a mouse click) onto its corresponding complex number.
     *
     * @param pixel
     * @return
     */
    public ComplexNumber pointToCoordinates(Pixel pixel){
        return renderRegion.getComplexPointFromPixel(pixel, displayImageSize, true);
    }

    /**
     * For a given ComplexNumber map to the closest Pixel on the displayed
     * ImageSize.
     *
     * @param coordinates
     * @return
     */
    public Pixel coordinatesToPoint(ComplexNumber coordinates){
        return renderRegion.getPixelFromComplexPoint(coordinates, displayImageSize);
    }

    /**
     * Accessor for the image object which should be displayed to user (not the image
     * for saving).
     *
     * If this object is in a lightweight state then it will be fully evaluated (taken
     * out of lightweight state) first. If the size of the attribute displayImage has
     * changed, the returned image will first be updated to match the new size.
     *
     * @param requestedDisplayImageSize
     * @return
     */
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

    /**
     * Accessor for the image with a one-to-one correspondence between pixels and the MandelPoint
     * objects upon which the images are based. This is the image that can be saved to disk and
     * serves as the basis for the scaled version displayed to the user.
     *
     * @return
     */
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
            if(null != mJPanel)
                mJPanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

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
            Main.log.nonFatalException("", e);
        } finally {
            if(null != mJPanel)
                mJPanel.setCursor(Cursor.getDefaultCursor());
        }
    }

    /**
     * Sets the color for each pixel in the logical image.
     *
     * @param column
     */
    public void initLogicalBufferedImageColumn(final int column){
        for(int y = 0; y < logicalImageSize.getHeight(); ++y)
            logicalBufferedImage.setRGB(column, y, getColorAtPoint(column, y).getRGB());
    }

    /**
     * @return number of pixels comprising the x (real) axis.
     */
    public int getColumnCount(){
        return logicalImageSize.getWidth();
    }

    /**
     * Creates a scaled version of the logical image.
     *
     * There are quicker scaling methods in the Java standard library but
     * none that give better picture quality.
     */
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

    /**
     * @return last value for how many iterations a pixel is allowed before
     *   it a prisoner point if it did not pass the escape radius.
     */
    public int getIterationMax() {
        return iterationMax;
    }

    /**
     * Setter. Since the logical image is dependent on this attribute, and the
     * displayed image on the logical image, a change requires recalculating
     * both.
     *
     * @param iterationMax
     */
    public void setIterationMax(int iterationMax) {
        this.iterationMax = iterationMax;
        initLogicalBufferedImage();
        initDisplayBufferedImage();
    }

    /**
     * @return
     */
    public ImageSize getLogicalImageSize() {
        return logicalImageSize;
    }

    /**
     * Rather than having the user specify a new value for iteration max, this
     * method increases it using a predefined formula.
     *
     * TODO: more intelligent increasing strategy; when that is done, use it
     * to automatically increase iteration max everytime the image is zoomed.
     */
    public void increaseIterationMax() {
        setIterationMax(iterationMax + (int)Math.pow((double)iterationMax, 1.2));
        initLogicalBufferedImage();
        initDisplayBufferedImage();
    }

    /**
     * This object must have a reference to a Component object so it can set
     * the cursor to busy when it is rendering.
     *
     * todo: not this
     *
     * @param mJPanel
     */
    public void setmJPanel(MandelJPanel mJPanel) {
        this.mJPanel = mJPanel;
    }

    /**
     * Both logical and displayed images are based on a coloring scheme. This
     * method allows a new scheme to be indicated and updates both images.
     *
     * @param name
     */
    public void setPalette(String name){
        palette = new PaletteSet().getPalette(name);
        initLogicalBufferedImage();
        initDisplayBufferedImage();
    }

    /**
     * Accessor for the complex values that define the bounds of this image.
     *
     * @return
     */
    public ComplexRegion getRenderRegion() {
        return renderRegion;
    }

    /**
     * The size of the scaled image which is presented to user.
     *
     * @return
     */
    public ImageSize getDisplayImageSize() {
        return displayImageSize;
    }

    /**
     * The palette which determined coloring of the currently set logical
     * and display images.
     *
     * @return
     */
    public Palette getPalette() {
        return palette;
    }

    /**
     * Distance between two vertically or horizontally adjacent complex numbers
     * quantized to match the pixelation of the logical image, assuming aspect
     * ratio has been enforced and these distances are equal, if not: then the
     * average of the two.
     *
     * @return
     */
    public double getDelta(){
        return renderRegion.getAverageDelta(logicalImageSize);
    }

    public void updatePrisonerOrEscapeeCounts(){
        setLightWeight(false);
        prisonerCount = escapeeCount = 0;
        for(MandelPoint[] foo : mandelPoints)
            for(MandelPoint p : foo)
                if(p.didEscape())
                    ++escapeeCount;
                else
                    ++prisonerCount;
    }

    public int getEscapeeCount(){
        return escapeeCount;
    }

    public int getPrisonerCount(){
        return prisonerCount;
    }
}
