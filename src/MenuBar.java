import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: Feb 19, 2011
 * Time: 1:15:11 PM
 * To change this template use File | Settings | File Templates.
 */

// http://download.oracle.com/javase/tutorial/uiswing/components/menu.html

/**
 * The main menu bar which will be added to the main GUI.
 */
class MenuBar extends JMenuBar implements ActionListener {

    // TODO: installer, store these pages locally
    final private static String URL_HELP_TOPICS = "http://www.davidlivingston.info/mmv/help-topics.html";
    final private static String URL_ABOUT = "http://www.davidlivingston.info/mmv/about.html";
    final private static String URL_PROJECT_HOMEPAGE = "https://github.com/david-livingston/mmv_swe_project";

    // TODO: add these to a set and throw Exception if duplicate; event handling code assumes
    // these will be unique; has to be a less hackish way to do this...
    final private static String MENU_ITEM_KEY_SAVE_AS = "Save Image As...";
    final private static String MENU_ITEM_HELP_TOPICS = "Help Topics";
    final private static String MENU_ITEM_PROJECT_PAGE = "Project Homepage @ GitHub";
    final private static String MENU_ITEM_ABOUT = "About";

    final private MandelJPanel mJPanel;

    /**
     * @param panel the container which this menubar will be added to; needed
     * so the selected menuitem will have a way to invoke the requested action
     */
    public MenuBar(MandelJPanel panel){
        super();

        mJPanel = panel;

        // make top level menus to go on menubar
        JMenu fileMenu = new JMenu("File");
        JMenu helpMenu = new JMenu("Help");

        // make dropdown menu elements which will be associated with top level menus
        JMenuItem saveImage = new JMenuItem(MENU_ITEM_KEY_SAVE_AS);
        JMenuItem helpTopics = new JMenuItem(MENU_ITEM_HELP_TOPICS);
        JMenuItem projectInfo = new JMenuItem(MENU_ITEM_PROJECT_PAGE);
        JMenuItem about = new JMenuItem(MENU_ITEM_ABOUT);

        // register event listeners for dropdown menu elements
        saveImage.addActionListener(this);
        helpTopics.addActionListener(this);
        projectInfo.addActionListener(this);
        about.addActionListener(this);

        // add drop down menu elements to their top level menu
        fileMenu.add(saveImage);
        helpMenu.add(helpTopics);
        helpMenu.add(projectInfo);
        helpMenu.add(about);

        // finally add top level menus to the menubar (this) // todo -- finish menus
        add(fileMenu);
        /* STUB */ add(new JMenu("Navigation"));
        /* STUB */ add(new JMenu("Color Scheme"));
        /* STUB */ add(new JMenu("Resolution"));
        /* STUB */ add(new JMenu("Advanced Options"));
        add(helpMenu);
    }

    /**
     * Call back for clicking an element of a dropdown menu
     *
     * @param e
     */
    public void actionPerformed(ActionEvent e){
        // File | Save Image As...
        if(matches(e, MENU_ITEM_KEY_SAVE_AS)){
            saveImage("mandel", "png");
        }
        // Help | Help Topics
        else if (matches(e, MENU_ITEM_HELP_TOPICS)) {
            openWebPage(URL_HELP_TOPICS);
        }
        // Help | About
        else if (matches(e, MENU_ITEM_ABOUT)) {
            openWebPage(URL_ABOUT);
        }
        // Help | Project Homepage @ Github
        else if (matches(e, MENU_ITEM_PROJECT_PAGE)) {
            openWebPage(URL_PROJECT_HOMEPAGE);
        }
        // stuff that's stubbed out but not implemented
        else {
            System.err.println("Feature not implemented: " + e.getActionCommand());
        }
    }

    /**
     * Whether the ActionEvent e was fired by a menu element associated with
     * String key.
     *
     * There's probably a better way to do this.
     *
     * @param e
     * @param key Argument to the constructor of the JMenuItem which fired
     *  ActionEvent e; this is also the text that is displayed to the user
     *  on that menu choice.
     * @return whether the ActionEvent e was fired by a menu element
     *  associated with String key
     */
    boolean matches(ActionEvent e, String key){
        return e.getActionCommand().equals(key);
    }

    /**
     * Saves the image currently being displayed to the user at a file location of the
     * user's choosing. Uses a filechooser dialog.
     *
     * Note: multiple TODO tasks in this method
     *
     * @param defaultName the file name prefilled in the save dialog
     * @param defaultExtension the file extension prefilled int he save dialog
     * @return whether the file was successfully saved
     */
    boolean saveImage(final String defaultName, final String defaultExtension){
        try {
            // figure out where to save the image
            // TODO: set file filters
            // TODO: make dialog open in My Pictures
            final JFileChooser jfc = new JFileChooser();
            jfc.setSelectedFile(new File(defaultName + "." + defaultExtension));
            final int retVal = jfc.showSaveDialog(mJPanel);

            if(JFileChooser.APPROVE_OPTION != retVal){
                return false; // user cancelled
            }

            final File savefile = jfc.getSelectedFile();

            // save image as PNG, doc re. saving BufferedImage:
            // http://download.oracle.com/javase/tutorial/2d/images/saveimage.html
            // TODO: other formats, esp. lossless format (bmp ?)
            BufferedImage bi = mJPanel.getCurrentImage();
            ImageIO.write(bi, defaultExtension, savefile);
        } catch (Exception ioe) {
            System.err.println(ioe);
            return false;
        }
        return true;
    }

    /**
     * Opens a webpage in the user's default browser.
     *
     * Note: this requires Java 6, more complicated in Java 5. See:
     * http://www.centerkey.com/java/browser/
     *
     * @param url location of the webpage to open
     * @return whether the page was opened
     */
    boolean openWebPage(final String url){
        try {
            java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
        } catch (Exception e){
            System.err.println("Error opening webpage: " + url + "\n\tError: " + e);
            return false;
        }
        return true;
    }
}
