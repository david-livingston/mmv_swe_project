import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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
public class MandelJPanel extends JPanel implements MouseListener, MouseMotionListener {

    private BufferedImage image;
    private final NavigationHistory navigation;

    private Pixel firstClick = null;
    private Pixel mouseLocation = null;

    private JInternalFrame thumbNailFrame = null;
    private JTable renderStats = null;
    private StatusBar statusBar = null;
    private LocationThumbnail thumbnail = null;

    private ImageSize displayedImageSize;

    /**
     *
     * @param logicalImageSize
     * @param displayedImageSize
     */
    public MandelJPanel(final ImageSize logicalImageSize, final ImageSize displayedImageSize){
        super();
        this.displayedImageSize = displayedImageSize;
        navigation = new NavigationHistory(logicalImageSize, displayedImageSize);
        refreshBufferedImage();
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
            doZoom(firstClick, new Pixel(e.getX(), e.getY()));
            firstClick = null;
        }else
            firstClick = new Pixel(e.getX(), e.getY());
    }

    public void mouseDragged(MouseEvent e) {}

    /**
     * Called by a mouse event listener to indicate user has selected two
     * points describing a region to zoom in on.
     *
     * todo: remove printlns, maybe use status bar
     *
     * @param firstClick the first point clicked by user to indicate start of a zoom region
     * @param secondClick the second point clicked by user to indicate end of zoom region
     */
    void doZoom(Pixel firstClick, Pixel secondClick){
        System.out.println("Beginning Zoom @ UpperLeft(" + firstClick.getX() + "," + firstClick.getY() + "); LowerRight(" + secondClick.getX() + "," + secondClick.getY() + ")");
        thumbnail.setFocus(
            new ComplexRegion(
                navigation.getCurrent().pointToCoordinates(firstClick),
                navigation.getCurrent().pointToCoordinates(secondClick)
            )
        );
        thumbNailFrame.repaint();
        navigation.zoom(firstClick, secondClick);
        refreshBufferedImage();
        thumbNailFrame.setVisible(true);
        updateRenderStats();
        System.out.println("Finished Zoom");
    }

    public void updateRenderStats(){
        renderStats.setModel(new DefaultTableModel(navigation.getCurrent().getAttributeValues(), new Object[]{ "Attribute", "Value" } ));
    }

    public void refreshBufferedImage(){
        image = navigation.getCurrent().getDisplayedBufferedImage(displayedImageSize);
        repaint();
    }

    /**
     * Call back for mouse movement; this isn't necessary for zooming, it is
     * needed to track where the zoom box will be drawn while the user is in
     * the mode for selecting a zoom region.
     *
     * @param e
     */
    public void mouseMoved(MouseEvent e) {
        mouseLocation = new Pixel(e.getX(), e.getY());
        repaint();
    }

    public BufferedImage getCurrentDisplayedImage(){
        return navigation.getCurrent().getDisplayedBufferedImage(displayedImageSize);
    }

    public BufferedImage getCurrentLogicalImage(){
        return navigation.getCurrent().getLogicalBufferedImage();
    }

    public NavigationHistory getNavigationHistory(){
        return navigation;
    }

    public Object[][] getAttributeValues(){
        return navigation.getCurrent().getAttributeValues();
    }

    //-------------------------------------------
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}

    public void associateThumbnail(JInternalFrame locationThumbnailInternalFrame) {
        thumbNailFrame = locationThumbnailInternalFrame;
    }

    public void associateRenderStats(JTable renderStats) {
        this.renderStats = renderStats;
    }

    public void associateStatusBar(StatusBar statusBar) {
        this.statusBar = statusBar;
    }

    public void associateThumbnail(LocationThumbnail thumbnail){
        this.thumbnail = thumbnail;
        navigation.associateThumbnail(thumbnail);
    }

    public void setDisplayedImageSize(final ImageSize newSize){
        displayedImageSize = newSize;
    }
}
