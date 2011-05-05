import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: 3/15/11
 * Time: 4:44 AM
 * To change this template use File | Settings | File Templates.
 *
 * Assists in creating MandelCanvas objects.
 *
 * Current functionality:
 *      - creating the 'home' (zoomed out) starting MandelCanvas
 *      - reconstructing MandelCanvas objects from files of their serialized state
 */
public class MandelCanvasFactory {

    private final static double defaultRealMinimum = -3.5;
    private final static double defaultRealMaximum = 1.0;
    private final static double defaultImaginaryMaximum = 1.25;
    private final double defaultImaginaryMinimum; // auto calc'ed for proper aspect ratio, in theory should be -1.0 * defaultImaginaryMaximum
    private final double defaultDelta;

    private final int defaultIterationMax = 128;

    private final MandelCanvas home;

    /**
     * Constructor is pointless.
     *
     * TODO: static method which returns the 'home' view of MandelCanvas for input image sizes
     *
     * @param logicalImageSize
     * @param displayImageSize
     */
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

    /**
     * Retrieves MandelCanvas from a file containing serialized SaveableState
     * version of the MandelCanvas.
     *
     * Not very user friendly, presumably the GUI
     * will only call this method on files with the correct extension but if
     * an improper file is selected an error message rather than an exception
     * would be nicer.
     *
     * Uses a SaveableState object rather than the default serialization of
     * MandelCanvas because it is less likely to break the save/open state
     * feature everytime a program change is made.
     *
     * @param serialized
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static MandelCanvas unmarshallFromSaveableState(File serialized) throws IOException, ClassNotFoundException {
        assert null != serialized;
        final FileInputStream fis = new FileInputStream(serialized);
        final ObjectInputStream in = new ObjectInputStream(fis);
        final SaveableState ss = (SaveableState) in.readObject();
        final MandelCanvas out = ss.toMandelCanvas();
        out.calcLightWeightAttributes();
        return out;
    }
}
