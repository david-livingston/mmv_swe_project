import java.awt.*;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: 4/3/11
 * Time: 12:14 PM
 * To change this template use File | Settings | File Templates.
 *
 * Custom version of java.awt.Point which uses floats for some reason and
 * has unneeded functionality. This object (unlike Point?) does not allow
 * coordinates that would be outside of the displayed quadrant (all pts
 * are quad 2, i.e. having both components non-negative).
 *
 * Methods are provided for converting to and from java.awt.Point objects
 * since many Swing methods require those objects as inputs.
 *
 * Requirement 1.0.0 Program should incorporate a GUI
 * This class is used extensively by all of the program's GUI code.
 */
public class Pixel implements Serializable {

    final int x;
    final int y;

    public Pixel(int x, int y) {
        assert x >= 0 && y >= 0;
        this.x = x;
        this.y = y;
    }

    public Pixel(Point p){
        assert p.getX() >= 0.0 && p.getY() >= 0.0;
        this.x = (int) p.getX();
        this.y = (int) p.getY();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Point asPoint(){
        return new Point(getX(), getY());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pixel pixel = (Pixel) o;

        if (x != pixel.x) return false;
        if (y != pixel.y) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }
}
