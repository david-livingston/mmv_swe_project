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

/*
 * GUI code necessary to connect the JFrame application (GUI.java) to the picture (MandelCanvas.java).
 */
public class MandelJPanel extends JPanel implements MouseListener, MouseMotionListener {

    private BufferedImage image;

    private final int xRes;
    private final int yRes;
    private final MandelCanvas canvas;

    Point firstClick = null;
    Point mouseLocation = null;

    public MandelJPanel(int xRes, int yRes){
        this.xRes = xRes;
        this.yRes = yRes;
        canvas = new MandelCanvas(xRes, yRes);
        image = canvas.getAsBufferedImage();
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // image = canvas.getAsBufferedImage();
        g.drawImage(image, 0, 0, null);

        // draw zoom box
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

    public void mousePressed(MouseEvent e) {
        if(null != firstClick){
            doZoom(firstClick, new Point(e.getX(), e.getY()));
            firstClick = null;
        }else
            firstClick = new Point(e.getX(), e.getY());
    }

    public void doZoom(Point upperLeftClick, Point lowerRightClick){
        System.out.println("Beginning Zoom @ UpperLeft(" + upperLeftClick.getX() + "," + upperLeftClick.getY() + "); LowerRight(" + lowerRightClick.getX() + "," + lowerRightClick.getY() + ")");
        canvas.doZoom(upperLeftClick, lowerRightClick);
        image = canvas.getAsBufferedImage();
        repaint();
        System.out.println("Finished Zoom");
    }

    //-------------------------------------------
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}

    //------------------------------------------
    public void mouseDragged(MouseEvent e) {}

    public void mouseMoved(MouseEvent e) {
        mouseLocation = new Point(e.getX(), e.getY());
        repaint();
    }

    public BufferedImage getCurrentImage(){
        return canvas.getAsBufferedImage();
    }
}
