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
 * TODO: change from a single class with one static method; this needs to be
 * more flexible so the user can change coloring schemes while the program
 * is running.
 *
 * Color's constructor uses the red, green, blue color model.
 * Alternate color constructors are available for hue, saturation, brightness.
 * RGB assumes each arg will be (0...255) inclusive, code doesn't check for this;
 * increasing the iteration max elsewhere in the code could break this.
 */
class Palette {

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
    public static Color getColor(MandelPoint m){

        if(!m.escaped)
            return Color.BLACK;
        else{
            int i = m.iterationCount;
            
            return new Color(
                (i * i) % 255,
                (i * i + i) % 255,
                (i + i) % 255
            );
            // another simple coloring scheme:
            // doesn't check to make sure each r,g,b is b/w 0 & 255
            /*
            return new Color(
                255 - i,
                255 - (i * 2)/3,
                255 - i * 2
            );*/
        }
    }
}
