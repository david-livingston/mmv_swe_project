/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: 4/26/11
 * Time: 10:26 AM
 * To change this template use File | Settings | File Templates.
 */
public class RenderThread implements Runnable {

    private final MandelCanvas canvas;
    private final int columnIndicator;

    public RenderThread(MandelCanvas canvas, int columnIndicator) {
        this.canvas = canvas;
        this.columnIndicator = columnIndicator;
    }

    public void run() {
        for(int i = 0; i < canvas.getColumnCount(); ++i)
            if(0 == (i + 1) % columnIndicator) // +1 to avoid modulo 0 error, every column still calc'ed
                canvas.initLogicalBufferedImageColumn(i);
    }

}
