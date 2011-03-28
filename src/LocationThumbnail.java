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
    private IntegerRectangle focus = null;
    private MandelCanvas canvas;
    private JInternalFrame thumbNailFrame;


    public LocationThumbnail(int xResolution, int yResolution, JInternalFrame thumbNailFrame){
        this.thumbNailFrame = thumbNailFrame;
        canvas = new MandelCanvasFactory(xResolution, yResolution).getHome();
        image = canvas.getAsBufferedImage();
    }

    // todo: setFocus as box around zoom region + crosshairs (only crosshairs at the moment)
    public void setFocus(ComplexRectangle rect){
        focus = new IntegerRectangle(
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

        // todo: draw box around zoom region if it would be visible
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