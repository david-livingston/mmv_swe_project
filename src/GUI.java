import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: David
 * Date: Feb 13, 2011
 * Time: 7:57:37 PM
 * To change this template use File | Settings | File Templates.
 */

/*
 * Run this: main program.
 */
public class GUI extends JFrame {

    final static int xResolution = 850;
    final static int yResolution = 700;

    JDesktopPane desktop;

    public GUI() {
        setJMenuBar(new MenuBar());
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

    public static void main(String... args) {
        try { // Set System L&F
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Exception setting native look & feel: " + e);
        }
        new GUI();
    }
}
