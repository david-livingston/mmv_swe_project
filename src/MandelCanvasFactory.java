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

    private final MandelCanvas home;

    public MandelCanvasFactory(final ImageSize imageSize){
        defaultDelta = (defaultRealMaximum - defaultRealMinimum)/imageSize.getWidth();
        defaultImaginaryMinimum = defaultImaginaryMaximum - imageSize.getHeight() * defaultDelta;
        home = new MandelCanvas(
            new ComplexRegion(
                new ComplexNumber(defaultRealMinimum, defaultImaginaryMaximum),
                new ComplexNumber(defaultRealMaximum, defaultImaginaryMinimum)
            ),
            imageSize
        );
    }

    public MandelCanvas getHome(){
        return home;
    }
}
