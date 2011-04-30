import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: 4/28/11
 * Time: 12:41 PM
 *
 * Because I'm tired of the Save/Open state feature breaking every time I change anything; forget the
 * default serialization strategy.
 */
public class SaveableState implements Serializable {

    final private String colorPaletteName;
    final private boolean arbitraryPrecisionEnabled;
    final private Double xMin;
    final private Double xMax;
    final private Double yMin;
    final private Double yMax;
    final private int maxIterations;
    final private int logicalXResolution;
    final private int logicalYResolution;
    final private int displayXResolution;
    final private int displayYResolution;

    public SaveableState(
            final ComplexRegion renderRegion,
            final ImageSize logicalImageSize,
            final ImageSize displayImageSize,
            final int maxIterations,
            final String colorPaletteName,
            final boolean arbitraryPrecisionEnabled
        )
    {
        this.colorPaletteName = colorPaletteName;
        this.arbitraryPrecisionEnabled = arbitraryPrecisionEnabled;
        this.maxIterations = maxIterations;
        xMin = new Double(renderRegion.getRealMin());
        xMax = new Double(renderRegion.getRealMax());
        yMin = new Double(renderRegion.getImagMin());
        yMax = new Double(renderRegion.getImagMax());
        logicalXResolution = logicalImageSize.getWidth();
        logicalYResolution = logicalImageSize.getHeight();
        displayXResolution = displayImageSize.getWidth();
        displayYResolution = displayImageSize.getHeight();
    }

    public MandelCanvas toMandelCanvas(){
        // todo: implement arbitrary precision
        assert !arbitraryPrecisionEnabled;

        Palette palette = new PaletteSet().getPalette(colorPaletteName);
        if(null == palette){
            Static.log.error("SaveableState.toMandelCanvas()", "Did not find palette: " + colorPaletteName);
            palette = new PaletteSet().getDefault();
        }

        assert null != palette;

        return new MandelCanvas(
            new ComplexRegion(
               new ComplexNumber(
                    xMin.doubleValue(),
                    yMax.doubleValue()
               ),
               new ComplexNumber(
                    xMax.doubleValue(),
                    yMin.doubleValue()
               )
            ),
            new ImageSize(
                logicalYResolution,
                logicalXResolution
            ),
            new ImageSize(
                displayYResolution,
                displayXResolution
            ),
            maxIterations,
            palette
        );
    }
}
