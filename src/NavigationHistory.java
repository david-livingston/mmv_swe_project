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

    public NavigationHistory(final int xRes, final int yRes){
        current = home = new MandelCanvasFactory(xRes, yRes).getHome();
    }

    public MandelCanvas getCurrent(){
        return current;
    }

    public void zoom(Point upperLeftClick, Point lowerRightClick){
        next.clear();
        previous.push(current);
        // todo: doesn't always work, might have to avoid pushing the pixel data & only push the coordinates describing the zoom region
        try {
            current = current.doZoom(upperLeftClick, lowerRightClick);
        } catch (OutOfMemoryError ouch) {
            previous.clear();
            System.err.println("Navigation history abandoned b/c heap space exceeded.");
            current = current.doZoom(upperLeftClick, lowerRightClick);
        }
    }

    public void back(){
        if(previous.isEmpty())
            return;
        next.push(current);
        current = previous.pop();
    }

    public void home(){
        if(home != current)
            previous.push(current);
        current = home;
        next.clear();
    }

    public void forward(){
        if(next.isEmpty())
            return;
        previous.push(current);
        current = next.pop();
    }

}
