/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: 3/15/11
 * Time: 4:44 AM
 * To change this template use File | Settings | File Templates.
 */
public class MandelCanvasFactory {

    private final static double defaultRealMinimum = -2.5;
    private final static double defaultImaginaryMaximum = 1.25;
    private final static double defaultRealMaximum = 1.0;
    private final double defaultImaginaryMinimum;
    private final double defaultDelta;

    private final int countOfXPixels;
    private final int countOfYPixels;

    public MandelCanvasFactory(final int countOfXPixels, final int countOfYPixels){
        this.countOfXPixels = countOfXPixels;
        this.countOfYPixels = countOfYPixels;
        defaultDelta = (defaultRealMaximum - defaultRealMinimum)/countOfXPixels;
        defaultImaginaryMinimum = defaultImaginaryMaximum - countOfYPixels * defaultDelta;
    }

    public MandelCanvas getHome(){
        return new MandelCanvas(
            defaultRealMinimum,
            defaultImaginaryMaximum,
            defaultRealMaximum,
            defaultImaginaryMinimum,
            defaultDelta,
            countOfXPixels,
            countOfYPixels
        );
    }
}
