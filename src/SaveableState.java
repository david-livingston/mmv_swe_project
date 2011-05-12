import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: 4/28/11
 * Time: 12:41 PM
 *
 * Because I'm tired of the Save/Open state feature breaking every time I change anything; forget the
 * default serialization strategy applied to MandelCanvas.
 *
 * Requirement 1.1.8 Ability to save the current state using a custom file format
 */
class SaveableState implements Serializable {

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

    /**
     * Use all the attributes which define how to begin rendering the picture;
     * do not save any attribute which is derivable from this minimal base set.
     *
     * @param renderRegion
     * @param logicalImageSize
     * @param displayImageSize
     * @param maxIterations
     * @param colorPaletteName
     * @param arbitraryPrecisionEnabled
     */
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
        xMin = renderRegion.getRealMin();
        xMax = renderRegion.getRealMax();
        yMin = renderRegion.getImagMin();
        yMax = renderRegion.getImagMax();
        logicalXResolution = logicalImageSize.getWidth();
        logicalYResolution = logicalImageSize.getHeight();
        displayXResolution = displayImageSize.getWidth();
        displayYResolution = displayImageSize.getHeight();
    }

    /**
     * Get the full object from the minimal starting information saved in this.
     *
     * @return
     */
    public MandelCanvas toMandelCanvas(){
        // todo: implement arbitrary precision
        assert !arbitraryPrecisionEnabled;

        Palette palette = new PaletteSet().getPalette(colorPaletteName);
        if(null == palette){
            Main.log.error("SaveableState.toMandelCanvas()", "Did not find palette: " + colorPaletteName);
            palette = new PaletteSet().getDefault();
        }

        assert null != palette;

        return new MandelCanvas(
            new ComplexRegion(
               new ComplexNumber(
                    xMin,
                    yMax
               ),
               new ComplexNumber(
                    xMax,
                    yMin
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
