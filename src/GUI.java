import javax.swing.*;
import javax.swing.table.JTableHeader;
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

    // Specifies the number of horizontal and vertical pixels used displaying the picture.
    private final static ImageSize logicalImageSize = new ImageSize(1400, 1700);
    private final static ImageSize displayedImageSize = new ImageSize(700, 850);

    private final MandelJPanel mJPanel;

    private final JDesktopPane desktop = new JDesktopPane();

    private final JTable renderStats;

    /**
     * Constructs the GUI which has the effect of launching the program.
     */
    private GUI(File fileToOpen) {
        setTitle(Global.getTitle());
        mJPanel = new MandelJPanel(logicalImageSize, displayedImageSize, fileToOpen);
        setJMenuBar(new MenuBar(mJPanel));

        JInternalFrame renderInternalFrame = new JInternalFrame("Render Window", true, true, true, true);
        renderInternalFrame.setVisible(true);
        renderInternalFrame.setSize(displayedImageSize.getWidth(), displayedImageSize.getHeight());
        desktop.add(renderInternalFrame);
        desktop.setVisible(true);
        setContentPane(desktop);
        renderInternalFrame.add(mJPanel);

        setSize(displayedImageSize.getWidth() + 375, displayedImageSize.getHeight() + 75);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        StatusBar statusBar = new StatusBar();
        renderInternalFrame.add(statusBar, java.awt.BorderLayout.SOUTH);
        mJPanel.associateStatusBar(statusBar);

        // todo: clean up all this layout code
        // todo: make table not editable
        JInternalFrame attributeTableInternalFrame = new JInternalFrame("Attribute Values", true, true, true, true);
        attributeTableInternalFrame.setVisible(true);
        attributeTableInternalFrame.setSize(350, displayedImageSize.getHeight()/2 + 50);
        String[] columnNames = { "Attribute", "Value" };
        Object[][] data = mJPanel.getAttributeValues();
        renderStats = new JTable(data, columnNames);
        attributeTableInternalFrame.add(renderStats);
        attributeTableInternalFrame.setLocation(displayedImageSize.getWidth() + 10, 5);
        desktop.add(attributeTableInternalFrame);
        mJPanel.associateRenderStats(renderStats);
        JInternalFrame locationThumbnailInternalFrame = new JInternalFrame("Zoom Location", false, true, false, true);
        LocationThumbnail locationThumbnail = new LocationThumbnail(new ImageSize(175, 212), locationThumbnailInternalFrame);
        locationThumbnailInternalFrame.setVisible(false);
        locationThumbnailInternalFrame.setSize(225, 200);
        locationThumbnailInternalFrame.add(locationThumbnail);
        locationThumbnailInternalFrame.setLocation(displayedImageSize.getWidth() + 15, displayedImageSize.getHeight()/2 + 75);
        desktop.add(locationThumbnailInternalFrame);
        mJPanel.associateThumbnail(locationThumbnailInternalFrame);
        mJPanel.associateThumbnail(locationThumbnail);
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
