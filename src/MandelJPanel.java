import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;

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
    public MandelJPanel(final ImageSize logicalImageSize, final ImageSize displayedImageSize, final File fileToOpen){
        super();
        this.displayedImageSize = displayedImageSize;
        navigation = new NavigationHistory(logicalImageSize, displayedImageSize, fileToOpen);
        refreshBufferedImage();
        addMouseListener(this);
        addMouseMotionListener(this);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {

            }
        });
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
            final ImageRegion true_selection = new ImageRegion(firstClick, mouseLocation);
            final ImageRegion adjusted_selection = navigation.getCurrent().getLogicalImageSize().adjustImageRegionAspectRatio(firstClick, true_selection);
            
            g.setColor(Color.WHITE);
            g.drawRect(adjusted_selection.getXMin(), adjusted_selection.getYMin(), adjusted_selection.getWidth(), adjusted_selection.getHeight());
            g.drawRect(adjusted_selection.getXMin() - 2, adjusted_selection.getYMin() -2, adjusted_selection.getWidth() + 4, adjusted_selection.getHeight() + 4);
            g.setColor(Color.BLACK);
            g.drawRect(adjusted_selection.getXMin() - 1, adjusted_selection.getYMin() - 1, adjusted_selection.getWidth() + 2, adjusted_selection.getHeight() + 2);
        }
    }

    /**
     * Mouse event handler; currently only used for zooming.
     *
     * todo: use mouse dragging (maybe as option)
     *
     * @param e
     */
    public void mousePressed(MouseEvent e) {
        if(e.getButton() != MouseEvent.BUTTON1)
            return;

        if(null != firstClick){
            doZoom(
                navigation.getCurrent().getLogicalImageSize().adjustImageRegionAspectRatio(firstClick, new ImageRegion(firstClick, new Pixel(e.getPoint())))
            );
            firstClick = null;
        }else
            firstClick = new Pixel(e.getPoint());
    }

    public void mouseDragged(MouseEvent e) {

    }

    void doZoom(final ImageRegion selection){
        thumbnail.setFocus(
            new ComplexRegion(
                navigation.getCurrent().pointToCoordinates(selection.getUpperLeftCorner()),
                navigation.getCurrent().pointToCoordinates(selection.getLowerRightCorner())
            )
        );
        thumbNailFrame.repaint();
        navigation.zoom(selection);
        refreshBufferedImage();
        thumbNailFrame.setVisible(true);
        updateRenderStats();
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
        if(mouseLeavingArea(e))
            firstClick = null;
        mouseLocation = new Pixel(e.getPoint());
        repaint();
    }

    private boolean mouseLeavingArea(MouseEvent e){
        final Pixel location = new Pixel(e.getPoint());
        if(location.getX() < 2 || location.getY() < 2)
            return true;
        if(location.getX() > getWidth() - 2 || location.getY() > getHeight() - 2)
            return true;
        return false;
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
