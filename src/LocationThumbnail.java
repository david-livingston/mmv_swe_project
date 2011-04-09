import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: 3/16/11
 * Time: 5:18 AM
 * To change this template use File | Settings | File Templates.
 */
public class LocationThumbnail extends JPanel {

    private final BufferedImage image;
    private ImageRegion focus = null;
    private MandelCanvas canvas;
    private JInternalFrame thumbNailFrame;


    public LocationThumbnail(final ImageSize imageSize, final JInternalFrame thumbNailFrame){
        this.thumbNailFrame = thumbNailFrame;
        canvas = new MandelCanvasFactory(imageSize, imageSize).getHome();
        image = canvas.getDisplayedBufferedImage(imageSize);
    }

    public void setFocus(ComplexRegion rect){
        focus = new ImageRegion(
            canvas.coordinatesToPoint(rect.getUpperLeft()),
            canvas.coordinatesToPoint(rect.getLowerRight())
        );
        thumbNailFrame.repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // todo: increase opacity
        g.drawImage(image, 0, 0, null);

        if(null == focus)
            return;

        g.setColor(Color.WHITE);
        g.drawRect(focus.getXMin(), focus.getYMin(), focus.getXMax() - focus.getXMin(), focus.getYMax() - focus.getYMin());
        final int middleX = focus.getXMin() + (focus.getXMax() - focus.getXMin())/2;
        final int middleY = focus.getYMin() + (focus.getYMax() - focus.getYMin())/2;
        g.drawLine(middleX, 0, middleX, focus.getYMin());
        g.drawLine(middleX, focus.getYMax(), middleX, this.getHeight());
        g.drawLine(0, middleY, focus.getXMin(), middleY);
        g.drawLine(focus.getXMax(), middleY, this.getWidth(), middleY);
    }
}