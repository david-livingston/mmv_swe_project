import java.awt.*;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: 4/3/11
 * Time: 11:51 AM
 * To change this template use File | Settings | File Templates.
 */
public class ImageSize extends ImageRegion implements Serializable {

    public final static ImageSize HD1080 = new ImageSize(1080, 1920);

    public ImageSize(final int height, final int width){
        super(
            new Pixel(0, 0),
            new Pixel(width, height)
        );
    }


    public ImageRegion adjustImageRegionAspectRatio(final ImageRegion input){
        final double correct_width_to_height = ((double)getWidth()/getHeight());
        final double correct_height_to_width = ((double)getHeight()/getWidth());

        // I'm sure there's a much smarter way to do this.
        int newWidth = (int)(correct_width_to_height * input.getHeight());
        int newHeight = (int) (correct_height_to_width * newWidth);
        newWidth = (int)(correct_width_to_height * newHeight);

        return new ImageRegion(
            input.getUpperLeftCorner(),
            new Pixel(
                input.getXMin() + newWidth,
                input.getYMin() + newHeight
            )
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImageSize that = (ImageSize) o;

        return getHeight() == that.getHeight() && getWidth() == that.getWidth();
    }

    @Override
    public int hashCode() {
        return 31 * (getHeight() + 3 * getWidth());
    }
}
