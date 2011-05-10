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
 *
 * GUI code necessary to connect the JFrame object (MainWindow.java) and its menubar
 * (MenuBar.java) to the picture (MandelCanvas.java).
 *
 * Requirement 1.0.0 Display fractal image
 * Class contains all the elements for the render window where the primary view of
 * the fractal is contained.
 */
public class MandelJPanel extends JPanel implements MouseListener, MouseMotionListener {

    private BufferedImage image;
    private final NavigationHistory navigation;

    private Pixel firstClick = null;
    private Pixel mouseLocation = null;

    private JInternalFrame thumbNailFrame = null;
    private JTable renderStatsJTable = null;
    private LocationThumbnail thumbnail = null;
    private final RenderStatsTable renderStatsModel;

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
        renderStatsModel = new RenderStatsTable(navigation.getCurrent());
        refreshBufferedImage();
        addMouseListener(this);
        addMouseMotionListener(this);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                refreshBufferedImage();

                // todo: this should have its own update timer/thread
                updateRenderStats();
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
        if(e.getButton() != MouseEvent.BUTTON1) {
            // other mouse clicks aren't associated with any behavior now
            // but the user might be trying to cancel a zoom
            firstClick = null;
            return;
        }

        if(null != firstClick){
            doZoom(
                navigation.getCurrent().getLogicalImageSize().adjustImageRegionAspectRatio(firstClick, new ImageRegion(firstClick, new Pixel(e.getPoint())))
            );
            firstClick = null;
        }else
            firstClick = new Pixel(e.getPoint());
    }

    /**
     * Make sure the zoom box doesn't stay on screen if the user leaves the area.
     * This was previously handled by a hack b/c I didn't realize this method existed.
     *
     * @param e
     */
    public void mouseExited(MouseEvent e) {
        firstClick = null;
        repaint();
    }

    /**
     * Make sure the zoom box doesn't stay on screen if user left the area.
     * Probably redundant with mouseExited() but included just in case.
     *
     * @param e
     */
    public void mouseEntered(MouseEvent e) {
        firstClick = null;
        repaint();
    }

    /**
     *
     * @param e
     */
    public void mouseDragged(MouseEvent e) {
        // eventually it might be good to implement zoom
        // selection with mouse dragging
    }

    /**
     * Initiates the zooming process.
     *
     * @param selection the selected region (relative to displayed image size)
     *  to zoom in on
     */
    void doZoom(final ImageRegion selection){
        thumbnail.setFocus(
                new ComplexRegion(
                        navigation.getCurrent().pointToCoordinates(selection.getUpperLeftCorner()),
                        navigation.getCurrent().pointToCoordinates(selection.getLowerRightCorner())
                )
        );
        thumbNailFrame.repaint();
        navigation.zoom(selection);
        navigation.getCurrent().setmJPanel(this);
        refreshBufferedImage();

        // thumbnail isn't displayed on startup because it would show the exact same
        // view as the render window, make sure it's turned on after we zoom
        thumbNailFrame.setVisible(true);
        updateRenderStats();
    }

    /**
     *
     */
    public void updateRenderStats(){
        renderStatsModel.update(navigation.getCurrent());
        renderStatsJTable.setModel(new DefaultTableModel(renderStatsModel.getAttributeValues(), new Object[]{ "Attribute", "Value" } ));
    }

    /**
     * Ensures the displayed image held by MandelCanvas object currently selected is uptodate, retrieve it,
     * repaint it into window.
     */
    public void refreshBufferedImage(){
        // todo: replace this line w/ functionality in MandelCanvas
        final int height = (int)(getWidth() * navigation.getCurrent().getLogicalImageSize().heightToWidth());

        if(height > 0){
            // this method is called when the GUI is first constructed and this object dimensions are 0x0
            // will cause exception if not treated as special case
            displayedImageSize = new ImageSize(height, getWidth());
        }
        image = navigation.getCurrent().getDisplayedBufferedImage(displayedImageSize);
        if(null != thumbnail)
            thumbnail.repaint();
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
        mouseLocation = new Pixel(e.getPoint());
        repaint();
    }

    /**
     * Gets the raw (not scaled) image held by the currently selected (in navigation)
     * MandelCanvas object.
     *
     * @return
     */
    public BufferedImage getCurrentLogicalImage(){
        return navigation.getCurrent().getLogicalBufferedImage();
    }

    /**
     * Accessor for the navigation history used by this render window.
     *
     * @return
     */
    public NavigationHistory getNavigationHistory(){
        return navigation;
    }

    /**
     *
     * @param renderStats
     */
    public void associateRenderStats(JTable renderStats) {
        this.renderStatsJTable = renderStats;
    }

    /**
     * TODO: Thumbnail should be refactored into one class instead of passing references
     * to its window separately everywhere.
     *
     * @param locationThumbnailInternalFrame
     */
    public void associateThumbnail(JInternalFrame locationThumbnailInternalFrame) {
        thumbNailFrame = locationThumbnailInternalFrame;
    }

    /**
     *
     * @param thumbnail
     */
    public void associateThumbnail(LocationThumbnail thumbnail){
        this.thumbnail = thumbnail;
        navigation.associateThumbnail(thumbnail);
    }

    public Object[][] getAttributeValues(){
        return renderStatsModel.getAttributeValues();
    }

    //-------------------------------------------
    public void mouseReleased(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}

}
