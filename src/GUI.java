import javax.swing.*;

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
 *
 * TODO: use JDesktopPane
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

    // JDesktopPane desktop;

    /**
     * Constructs the GUI which has the effect of launching the program.
     */
    private GUI() {

        mJPanel = new MandelJPanel(xResolution, yResolution);

        setJMenuBar(new MenuBar(mJPanel));
        /*
        desktop = new JDesktopPane();
        JInternalFrame i = new JInternalFrame("", true, true, true, true);
        i.setVisible(true);
        i.setSize(300,300);
        desktop.add(i);
        desktop.setVisible(true);
        setContentPane(desktop);
        */

        getContentPane().add(new MandelJPanel(xResolution, yResolution));
        setSize(xResolution, yResolution);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        StatusBar statusBar = new StatusBar();
        getContentPane().add(statusBar, java.awt.BorderLayout.SOUTH);
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
