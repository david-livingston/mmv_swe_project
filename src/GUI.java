import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.File;

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

    // Specifies the number of horizontal and vertical pixels used for calculating the picture
    // dimensions of picture rendered on screen will likely be different
    private final static ImageSize logicalImageSize = ImageSize.REAL_HD;

    /**
     * Constructs the GUI which has the effect of launching the program.
     */
    private GUI(File fileToOpen) {

        final int frameHeightAddition = 25;
        final int frameWidthAddtion = 10;

        // this is somewhat naive b/c it gets the resolution of the primary display & doesn't consider multiple monitors
        final ImageSize monitorResolution = ImageSize.fromDimension(Toolkit.getDefaultToolkit().getScreenSize());
        // give from for title bar, menu bar, and possible bottom task bar
        final ImageSize mainWindow = new ImageSize(monitorResolution.getHeight() - 4 * frameHeightAddition, monitorResolution.getWidth());

        final ImageSize thumbNailImageSize = new ImageSize(216, 384); // REAL_HD / 5
        final ImageSize thumbNailFrameSize = new ImageSize(thumbNailImageSize.getHeight() + frameHeightAddition, thumbNailImageSize.getWidth() + frameWidthAddtion);
        final ImageSize renderStatsTableSize = new ImageSize(350, 600);

        final int widthAvailable = mainWindow.getWidth() - (10 + renderStatsTableSize.getWidth());
        final int matchingHeight = (int)(widthAvailable * ((double)logicalImageSize.getHeight())/logicalImageSize.getWidth());
        final ImageSize displayedImageSize = new ImageSize(matchingHeight, widthAvailable);
        final ImageSize renderWindowSize = new ImageSize(displayedImageSize.getHeight() + frameHeightAddition, displayedImageSize.getWidth() + frameWidthAddtion);
        assert 0.001 > Math.abs(logicalImageSize.heightToWidth() - displayedImageSize.heightToWidth());

        final Pixel upperLeftCornerThumbNailWindow = new Pixel(mainWindow.getWidth() - thumbNailFrameSize.getWidth(), mainWindow.getHeight() - thumbNailFrameSize.getHeight());
        final Pixel upperLeftCornerStatsTable = new Pixel(mainWindow.getWidth() - renderStatsTableSize.getWidth(), 0);
        final Pixel upperLeftCornerRenderWindow = new Pixel(0, 0);

        // SETUP DISPLAY OF MAIN RENDER WINDOW
        // JInternalFrame (for JDesktopPane) + JPanel
        // ----------------------------------------------------
        final MandelJPanel mJPanel = new MandelJPanel(logicalImageSize, displayedImageSize, fileToOpen);
        // JInternalFrame(String title, boolean resizable, boolean closable, boolean maximizable, boolean iconifiable)
        final JInternalFrame renderInternalFrame = new JInternalFrame("Render Window", true, false, true, true);
        renderInternalFrame.add(mJPanel);
        renderInternalFrame.setLocation(upperLeftCornerRenderWindow.asPoint());
        renderInternalFrame.setSize(renderWindowSize.asDimension());
        renderInternalFrame.setVisible(true);

        // SETUP DISPLAY OF LOCATION THUMBNAIL
        // -----------------------------------------------
        // JInternalFrame(String title, boolean resizable, boolean closable, boolean maximizable, boolean iconifiable)
        final JInternalFrame locationThumbnailInternalFrame = new JInternalFrame("Zoom Location", false, false, false, true);
        final LocationThumbnail locationThumbnail = new LocationThumbnail(thumbNailImageSize, locationThumbnailInternalFrame);
        locationThumbnailInternalFrame.setVisible(false);
        locationThumbnailInternalFrame.setSize(thumbNailFrameSize.asDimension());
        locationThumbnailInternalFrame.add(locationThumbnail);
        locationThumbnailInternalFrame.setLocation(upperLeftCornerThumbNailWindow.asPoint());
        mJPanel.associateThumbnail(locationThumbnailInternalFrame);
        mJPanel.associateThumbnail(locationThumbnail);

        // SETUP DISPLAY OF RENDER STATISTICS TABLE
        // todo: make table not editable
        // -----------------------------------------------
        // JInternalFrame(String title, boolean resizable, boolean closable, boolean maximizable, boolean iconifiable)
        JInternalFrame attributeTableInternalFrame = new JInternalFrame("Attribute Values", true, false, false, true);
        attributeTableInternalFrame.setVisible(true);
        attributeTableInternalFrame.setSize(renderStatsTableSize.getWidth(), renderStatsTableSize.getHeight());
        String[] columnNames = { "Attribute", "Value" };
        Object[][] data = mJPanel.getAttributeValues();
        final JTable renderStats = new JTable(data, columnNames);
        attributeTableInternalFrame.add(renderStats);
        attributeTableInternalFrame.setLocation(upperLeftCornerStatsTable.asPoint());
        mJPanel.associateRenderStats(renderStats);

        // SETUP DESKTOP PANE WHICH HOLDS ALL INTERIOR WINDOWS
        // -------------------------------------------------------
        final JDesktopPane desktop = new JDesktopPane();
        desktop.add(renderInternalFrame);
        desktop.setVisible(true);
        desktop.add(locationThumbnailInternalFrame);
        desktop.add(attributeTableInternalFrame);

        // SETUP MAIN WINDOW WHICH CONTAINS DESKTOP PANE & MENUBAR
        // -------------------------------
        setTitle(Global.getTitle());
        setJMenuBar(new MenuBar(mJPanel));
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // http://stackoverflow.com/questions/479523/java-swing-maximize-window
        // best voted answer on SO, not sure why the bitwise OR is necessary though
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
        setContentPane(desktop);
    }

    /**
     * Only entry point of the program.
     *
     * @param args
     */
    public static void main(String... args) {
        try { // Set System L&F
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            Global.logError("GUI.main()", "Exception setting native look & feel: " + e);
        }

        File fileToOpen = null;
        boolean sawOpenArg = false;

        for(String arg : args)
            if(sawOpenArg){
                fileToOpen = new File(arg);
                break;
            } else
                sawOpenArg = arg.equalsIgnoreCase("-open");

        new GUI(fileToOpen);
    }
}
