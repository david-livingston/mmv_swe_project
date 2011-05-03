/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: 4/26/11
 * Time: 10:26 AM
 * To change this template use File | Settings | File Templates.
 *
 * Thread which forces evaluation of some columns (modulo determined) of the
 * MandelPoint 2d array.
 */
public class RenderThread implements Runnable {

    private final MandelCanvas canvas;
    private final int columnIndicator;

    public RenderThread(MandelCanvas canvas, int columnIndicator) {
        this.canvas = canvas;
        this.columnIndicator = columnIndicator;
    }

    /**
     * Iterate through the columns, and if it is a column assigned to this
     * this thread, evaluate it.
     */
    public void run() {
        for(int i = 0; i < canvas.getColumnCount(); ++i)
            if(0 == (i + 1) % columnIndicator) // +1 to avoid modulo 0 error, every column still calc'ed
                canvas.initLogicalBufferedImageColumn(i);
    }

}
