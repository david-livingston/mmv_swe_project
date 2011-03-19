import javax.swing.*;
import javax.swing.table.TableModel;

/**
 * Created by IntelliJ IDEA.
 * User: David
 * Date: Feb 13, 2011
 * Time: 7:57:37 PM
 * To change this template use File | Settings | File Templates.
 */

/*
 * This is both the class that launches the program and the container
 * for all the other GUI elements; seems appropriate in an event driven
 * program this small.
 */
public class GUI extends JFrame {

    /* Specifies the number of horizontal and vertical pixels used displaying the picture.
     *
     * TODO: decouple the size of BufferedImage from the canvas it is displayed on so the
     * logical size of the image generated (and the size used when saving) does not need
     * to be exactly the same size as displayed on screen. */
    private final static int xResolution = 850;
    private final static int yResolution = 700;

    private final MandelJPanel mJPanel;

    private final JDesktopPane desktop = new JDesktopPane();

    private final JTable renderStats;

    /**
     * Constructs the GUI which has the effect of launching the program.
     */
    private GUI() {
        // todo: add memory stats to status bar, got a out of heap space exception once -- related to navigation history?
        setTitle("MMV: Multithreaded Mandelbrot Viewer");
        mJPanel = new MandelJPanel(xResolution, yResolution);
        setJMenuBar(new MenuBar(mJPanel));

        JInternalFrame renderInternalFrame = new JInternalFrame("Render Window", true, true, true, true);
        renderInternalFrame.setVisible(true);
        renderInternalFrame.setSize(xResolution, yResolution);
        desktop.add(renderInternalFrame);
        desktop.setVisible(true);
        setContentPane(desktop);
        renderInternalFrame.add(mJPanel);

        setSize(xResolution + 375, yResolution + 75);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        StatusBar statusBar = new StatusBar();
        renderInternalFrame.add(statusBar, java.awt.BorderLayout.SOUTH);
        mJPanel.associateStatusBar(statusBar);

        // todo: clean up all this layout code
        // todo: make table not display delta w/ scientific notation
        // todo: make table not editable
        // todo: more stats in table
        JInternalFrame attributeTableInternalFrame = new JInternalFrame("Attribute Values", true, true, true, true);
        attributeTableInternalFrame.setVisible(true);
        attributeTableInternalFrame.setSize(350, yResolution/2 + 50);
        String[] columnNames = { "Attribute", "Value" };
        Object[][] data = mJPanel.getAttributeValues();
        renderStats = new JTable(data, columnNames);
        attributeTableInternalFrame.add(renderStats);
        attributeTableInternalFrame.setLocation(xResolution + 10, 5);
        desktop.add(attributeTableInternalFrame);
        mJPanel.associateRenderStats(renderStats);

        // todo: make the crosshairs move as different zoom regions are selected
        JInternalFrame locationThumbnailInternalFrame = new JInternalFrame("Zoom Location", false, true, false, true);
        locationThumbnailInternalFrame.setVisible(false);
        locationThumbnailInternalFrame.setSize(225, 200);
        locationThumbnailInternalFrame.add(new LocationThumbnail(212, 175));
        locationThumbnailInternalFrame.setLocation(xResolution + 15, yResolution/2 + 75);
        desktop.add(locationThumbnailInternalFrame);
        mJPanel.associateThumbnail(locationThumbnailInternalFrame);
    }

    /**
     * Only entry point of the program.
     *
     * @param args not parsed
     */
    public static void main(String... args) {
        try { // Set System L&F
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Exception setting native look & feel: " + e);
        }
        new GUI();
    }
}
