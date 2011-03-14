import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

/**
 * Created by IntelliJ IDEA.
 * User: David
 * Date: Feb 13, 2011
 * Time: 8:11:30 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * GUI code necessary to connect the JFrame application (GUI.java) and its menubar
 * (MenuBar.java) to the picture (MandelCanvas.java).
 */
class MandelJPanel extends JPanel implements MouseListener, MouseMotionListener {

    private BufferedImage image;
    private final MandelCanvas canvas;

    private Point firstClick = null;
    private Point mouseLocation = null;

    /**
     * @param xRes size in pixels of horizontal axis
     * @param yRes size in pixels of vertical axis
     */
    public MandelJPanel(final int xRes, final int yRes){
        canvas = new MandelCanvas(xRes, yRes);
        image = canvas.getAsBufferedImage();
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    /**
     * Refreshes the BufferedImage associated with this component. If the
     * program is in zoom-mode it also draws a zoom box on top of the
     * BufferedImage.
     *
     * @param g the graphics context of this component
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, null);

        // draw zoom box, 3 pixels wide: white, black, white
        // todo: use BufferedImage features to indicate zoom region by opacity
        if(null != firstClick){ // if zooming
            int width = (int)mouseLocation.getX() - (int)firstClick.getX();
            int height = (int)mouseLocation.getY() - (int)firstClick.getY();
            
            g.setColor(Color.WHITE);
            g.drawRect((int)firstClick.getX(), (int)firstClick.getY(), width, height);
            g.drawRect((int)firstClick.getX() - 2, (int)firstClick.getY() - 2, width + 4, height + 4);
            g.setColor(Color.BLACK);
            g.drawRect((int)firstClick.getX() - 1, (int)firstClick.getY() - 1, width + 2, height + 2);
        }
    }

    /**
     * Mouse event handler; currently only used for zooming.
     *
     * todo: use mouse dragging (maybe as option), actually check which mouse button is being pressed
     *
     * @param e
     */
    public void mousePressed(MouseEvent e) {
        if(null != firstClick){
            doZoom(firstClick, new Point(e.getX(), e.getY()));
            firstClick = null;
        }else
            firstClick = new Point(e.getX(), e.getY());
    }

    public void mouseDragged(MouseEvent e) {}

    /**
     * Called by a mouse event listener to indicate user has selected two
     * points describing a region to zoom in on.
     *
     * todo: refactor param names
     * todo: remove printlns, maybe use status bar
     *
     * @param upperLeftClick the first point clicked by user to indicate start of a zoom region
     * @param lowerRightClick the second point clicked by user to indicate end of zoom region
     */
    void doZoom(Point upperLeftClick, Point lowerRightClick){
        System.out.println("Beginning Zoom @ UpperLeft(" + upperLeftClick.getX() + "," + upperLeftClick.getY() + "); LowerRight(" + lowerRightClick.getX() + "," + lowerRightClick.getY() + ")");
        canvas.doZoom(upperLeftClick, lowerRightClick);
        image = canvas.getAsBufferedImage();
        repaint();
        System.out.println("Finished Zoom");
    }

    /**
     * Call back for mouse movement; this isn't necessary for zooming, it is
     * needed to track where the zoom box will be drawn while the user is in
     * the mode for selecting a zoom region.
     *
     * @param e
     */
    public void mouseMoved(MouseEvent e) {
        mouseLocation = new Point(e.getX(), e.getY());
        repaint();
    }

    public BufferedImage getCurrentImage(){
        return canvas.getAsBufferedImage();
    }

    //-------------------------------------------
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}
}
