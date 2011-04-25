import java.awt.*;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: 4/3/11
 * Time: 12:14 PM
 * To change this template use File | Settings | File Templates.
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
