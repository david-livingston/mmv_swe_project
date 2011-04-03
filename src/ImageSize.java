import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: 4/3/11
 * Time: 11:51 AM
 * To change this template use File | Settings | File Templates.
 */
public class ImageSize extends ImageRegion {

    public ImageSize(final int height, final int width){
        super(
            new Pixel(0, 0),
            new Pixel(width, height)
        );
    }
}
