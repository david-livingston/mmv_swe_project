/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: 3/15/11
 * Time: 4:44 AM
 * To change this template use File | Settings | File Templates.
 */
public class MandelCanvasFactory {

    private final static double defaultRealMinimum = -3.5;
    private final static double defaultRealMaximum = 1.0;

    private final static double defaultImaginaryMaximum = 1.25;
    private final double defaultImaginaryMinimum; // auto calc'ed for proper aspect ratio, in theory should be -1.0 * defaultImaginaryMaximum

    private final double defaultDelta;
    private final int defaultIterationMax = 100;

    private final MandelCanvas home;

    public MandelCanvasFactory(final ImageSize logicalImageSize, final ImageSize displayImageSize){
        defaultDelta = (defaultRealMaximum - defaultRealMinimum)/logicalImageSize.getWidth();
        defaultImaginaryMinimum = defaultImaginaryMaximum - logicalImageSize.getHeight() * defaultDelta;
        home = new MandelCanvas(
            new ComplexRegion(
                new ComplexNumber(defaultRealMinimum, defaultImaginaryMaximum),
                new ComplexNumber(defaultRealMaximum, defaultImaginaryMinimum)
            ),
            logicalImageSize,
            displayImageSize,
            defaultIterationMax,
            new PaletteSet().getDefault()
        );
    }

    public MandelCanvas getHome(){
        return home;
    }
}
