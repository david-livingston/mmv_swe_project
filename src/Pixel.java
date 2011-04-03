/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: 4/3/11
 * Time: 12:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class Pixel {

    final int x;
    final int y;

    public Pixel(int x, int y) {
        Global.checkArgument(x >= 0 && y >= 0);
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
