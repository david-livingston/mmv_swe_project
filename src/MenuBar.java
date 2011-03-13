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
    
public class MenuBar extends JMenuBar implements ActionListener {

    // TODO: installer, store these pages locally
    final static String URL_HELP_TOPICS = "http://www.davidlivingston.info/mmv/help-topics.html";
    final static String URL_ABOUT = "http://www.davidlivingston.info/mmv/about.html";
    final static String URL_PROJECT_HOMEPAGE = "https://github.com/david-livingston/mmv_swe_project";

    final static String MENU_ITEM_KEY_SAVE_AS = "Save Image As...";
    final static String MENU_ITEM_HELP_TOPICS = "Help Topics";
    final static String MENU_ITEM_PROJECT_PAGE = "Project Homepage @ GitHub";
    final static String MENU_ITEM_ABOUT = "About";

    final MandelJPanel mJPanel;

    public MenuBar(MandelJPanel panel){
        super();

        mJPanel = panel;

        JMenu fileMenu = new JMenu("File");
        JMenuItem saveImage = new JMenuItem(MENU_ITEM_KEY_SAVE_AS);
        saveImage.addActionListener(this);
        fileMenu.add(saveImage);
        add(fileMenu);

        // todo
        add(new JMenu("Navigation"));
        add(new JMenu("Color Scheme"));
        add(new JMenu("Resolution"));
        add(new JMenu("Advanced Options"));

        JMenu helpMenu = new JMenu("Help");
        JMenuItem helpTopics = new JMenuItem(MENU_ITEM_HELP_TOPICS);
        helpTopics.addActionListener(this);
        helpMenu.add(helpTopics);
        JMenuItem projectInfo = new JMenuItem(MENU_ITEM_PROJECT_PAGE);
        projectInfo.addActionListener(this);
        helpMenu.add(projectInfo);
        JMenuItem about = new JMenuItem(MENU_ITEM_ABOUT);
        about.addActionListener(this);
        helpMenu.add(about);
        add(helpMenu);
    }

    public void actionPerformed(ActionEvent e){

        // DO FILE SAVE ON IMAGE IF THAT WAS THE MENU ITEM SELECTION
        if(matches(e, MENU_ITEM_KEY_SAVE_AS)){
            saveImage("mandel", "png");
        } else if (matches(e, MENU_ITEM_HELP_TOPICS)) {
            openWebPage(URL_HELP_TOPICS);
        } else if (matches(e, MENU_ITEM_ABOUT)) {
            openWebPage(URL_ABOUT);
        } else if (matches(e, MENU_ITEM_PROJECT_PAGE)) {
            openWebPage(URL_PROJECT_HOMEPAGE);
        } else {
            System.err.println("Feature not implemented: " + e.getActionCommand());
        }
    }

    public boolean matches(ActionEvent e, String key){
        return e.getActionCommand().equals(key);
    }

    public boolean saveImage(String defaultName, String defaultExtension){
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

            File savefile = jfc.getSelectedFile();

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

    // this requires java 6, more complicated in java 5
    // see: http://www.centerkey.com/java/browser/
    public boolean openWebPage(String url){
        try {
            java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
        } catch (Exception e){
            System.err.println("Error opening webpage: " + url + "\n\tError: " + e);
            return false;
        }
        return true;
    }
}
