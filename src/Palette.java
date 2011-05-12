import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: David
 * Date: Feb 13, 2011
 * Time: 7:41:25 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * Class maps a Mandelbrot point to the color it should be displayed as. This
 * mapping is arbitrary: the only distinction necessary for the picture to
 * be a Mandelbrot image is that the escaping points should be distinguishable
 * from the prisoner points.
 *
 * Requirement 1.0.0 The program should display a basic fractal image
 * This class is essential to that requirement because because it determines
 * the color of a pixel; without it there would be no image to display.
 *
 * Color palettes electing not to normalize their counter values can be made
 * much more efficient by using a color lookup table for some counter values.
 *
 * Most currently implemented Palettes simply compute (r,b,g) triplets based
 * on a counter value. However, more complex schemes are possible (e.g.
 * being initialized on a MandelCanvas and doing logarithmic scaling between
 * counter values and color values).
 */
public abstract class Palette {

    final private String name;

    public Palette(String name){
        this.name = name;
    }

    /**
     * Maps a Mandelbrot point to the color it should be displayed as on
     * screen.
     *
     * Most Mandelbrot renders map (iterations to escape) ->
     * color, but using the whole point as a key allows a more flexible
     * coloring scheme if needed.
     *
     * @param m the point being considered
     * @return color the point should be displayed as
     */
    public Color getColor(MandelPoint m){
        if(!m.didEscape())
            return Color.BLACK;
        else{
            return getColorDetail(normalize(m.getIterationCount(), m.getCurrentLocation().magnitude()));
        }
    }

    /**
     * Primary function of the class; realized by subclasses.
     *
     * @param counter
     * @return
     */
    public abstract Color getColorDetail(double counter);

    /**
     * Takes the integer count of iterations to escape and converts it to a double by also
     * considering the point's distance from the origin at the instant it escapes.
     *
     * Based on explanation at: http://linas.org/art-gallery/escape/smooth.html
     *
     * @param counter
     * @param magnitude
     * @return
     */
    double normalize(final int counter, final double magnitude){
        return counter + 1 - Math.log(Math.log(magnitude))/Math.log(2.0);
    }

    public Color safeColor(double r, double g, double b){
        return new Color(
            (float) sanitize(r),
            (float) sanitize(g),
            (float) sanitize(b)
        );
    }

    double sanitize(double original){
        if(original < 0.0)
            original *= -1;
        if(original > 255.0)
            original %= 255.0;
        return original / 255.0;
    }

    public String getName() {
        return name;
    }
}
