import java.awt.*;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: 3/15/11
 * Time: 4:24 AM
 * To change this template use File | Settings | File Templates.
 */

// todo: tie this class to menubar and deactivate irrelevant options
// todo: clean this up, javadoc
public class NavigationHistory {

    private final MandelCanvas home;
    private MandelCanvas current;
    private final LinkedBlockingDeque<MandelCanvas> previous = new LinkedBlockingDeque<MandelCanvas>();
    private final LinkedBlockingDeque<MandelCanvas> next = new LinkedBlockingDeque<MandelCanvas>();
    private LocationThumbnail thumbnail;

    public NavigationHistory(final ImageSize logicalImageSize, final ImageSize displayedImageSize){
        current = home = new MandelCanvasFactory(logicalImageSize, displayedImageSize).getHome();
    }

    public void associateThumbnail(LocationThumbnail thumbnail){
        this.thumbnail = thumbnail;
    }

    public MandelCanvas getCurrent(){
        return current;
    }

    public void zoom(Pixel upperLeftClick, Pixel lowerRightClick){
        current.setLightWeight(true);
        next.clear();
        previous.push(current);
        try {
            current = current.doZoom(upperLeftClick, lowerRightClick);
        } catch (OutOfMemoryError ouch) {
            previous.clear();
            System.err.println("Navigation history abandoned b/c heap space exceeded.");
            current = current.doZoom(upperLeftClick, lowerRightClick);
        }
    }

    public void back(){
        current.setLightWeight(true);
        if(previous.isEmpty())
            return;
        next.push(current);
        current = previous.pop();
        updateThumbnail();
    }

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

    public void setCurrent(MandelCanvas canvas) {
        current.setLightWeight(true);
        previous.push(current);
        current = canvas;
        updateThumbnail();
    }

    private void updateThumbnail(){
        thumbnail.setFocus(current.getAsComplexRectangle());
    }
}
