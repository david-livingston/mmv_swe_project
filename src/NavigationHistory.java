import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: 3/15/11
 * Time: 4:24 AM
 * To change this template use File | Settings | File Templates.
 *
 * todo: deactivate irrelevant options in menubar
 *
 * Attempts to implement an (internet) browser style navigation history for the
 * sequence of zooms that led to the current image.
 */
public class NavigationHistory {

    private final MandelCanvas home;
    private MandelCanvas current;
    private final LinkedBlockingDeque<MandelCanvas> previous = new LinkedBlockingDeque<MandelCanvas>();
    private final LinkedBlockingDeque<MandelCanvas> next = new LinkedBlockingDeque<MandelCanvas>();
    private LocationThumbnail thumbnail;

    /**
     * If starting in webstart mode, the param fileToOpen should indicate the
     * file to display on startup; if not, fileToOpen should be null, start
     * at home screen (as specified by MandelCanvasFactory).
     *
     * @param logicalImageSize
     * @param displayedImageSize
     * @param fileToOpen
     */
    public NavigationHistory(final ImageSize logicalImageSize, final ImageSize displayedImageSize, final File fileToOpen){
        home = new MandelCanvasFactory(logicalImageSize, displayedImageSize).getHome();
        if(null == fileToOpen)
            current = home;
        else try {
            current = MandelCanvasFactory.unmarshallFromSaveableState(fileToOpen);
        } catch (ClassNotFoundException cnfe) {
            Main.log.nonFatalException("Serialization issue opening file: " + fileToOpen, cnfe);
        } catch (FileNotFoundException fnfe) {
            Main.log.nonFatalException("Could not find file: " + fileToOpen, fnfe);
        } catch (IOException ioe) {
            Main.log.nonFatalException("IOException in InputStream while unmarshalling file: " + fileToOpen, ioe);
        }
    }

    /**
     * Need a ref to thumbnail so it can be told to update when the selected
     * image changes.
     *
     * todo: move this feature into the gui, doesn't belong here
     *
     * @param thumbnail
     */
    public void associateThumbnail(LocationThumbnail thumbnail){
        this.thumbnail = thumbnail;
    }

    /**
     * @return the MandelCanvas currently selected, i.e. b/w the previous
     * and back stacks.
     */
    public MandelCanvas getCurrent(){
        return current;
    }

    /**
     * User has selected a new MandelCanvas via zooming (rather than using
     * a navigation history feature like 'back'). History should treat this
     * situation analogous to clicking a new link in a browser.
     *
     * @param screenSelection
     */
    public void zoom(final ImageRegion screenSelection){
        current.setLightWeight(true);
        next.clear();
        previous.push(current);
        try {
            current = current.toZoomedCanvas(screenSelection);
        } catch (OutOfMemoryError ouch) {
            previous.clear();
            Main.log.error("NavigationHistory.zoom()", "Navigation history abandoned b/c heap space exceeded.");
            current = current.toZoomedCanvas(screenSelection);
        }
    }

    /**
     * If possible, retrieve the last selected image and update all navigation
     * data accordingly.
     */
    public void back(){
        current.setLightWeight(true);
        if(previous.isEmpty())
            return;
        next.push(current);
        current = previous.pop();
        updateThumbnail();
    }

    /**
     * Return to home screen. Clears 'next' b/c most browsers do this, when
     * selecting home (in theory 'next' recovers pages that were left when
     * using 'back', since the user did not use back for the most recent
     * navigation, 'next' makes less sense).
     *
     * Interestingly, on chrome if you use 'home' and then 'back,' the
     * 'next' list is restored. todo: consider whether this is a good idea
     */
    public void home(){
        if(home != current)
            setCurrent(home);
        next.clear();
    }

    public void forward(){
        if(next.isEmpty())
            return;
        setCurrent(next.pop());
    }

    /**
     * Mostly a helper method for other navigation functions; not private
     * because opening a save file will require access to this feature.
     *
     * @param canvas
     */
    public void setCurrent(MandelCanvas canvas) {
        current.setLightWeight(true);
        previous.push(current);
        current = canvas;
        updateThumbnail();
    }

    /**
     * In response to a zoom event, thumbnail must indicate the new zoom
     * region.
     *
     * todo: this is better placed somewhere in the GUI, like MandelJPanel.java
     */
    private void updateThumbnail(){
        thumbnail.setFocus(current.getRenderRegion());
    }
}
