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

    final static String MENU_ITEM_KEY_SAVE_AS = "Save Image As...";

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

    }

    public void actionPerformed(ActionEvent e){
        System.out.println("Action! " + e);

        // DO FILE SAVE ON IMAGE IF THAT WAS THE MENU ITEM SELECTION
        if(e.getActionCommand().equals(MENU_ITEM_KEY_SAVE_AS)){
            try {
                // figure out where to save the image
                // TODO: set file filters
                // TODO: make dialog open in My Pictures
                final JFileChooser jfc = new JFileChooser();
                jfc.setSelectedFile(new File("mandel.png"));
                final int retVal = jfc.showSaveDialog(mJPanel);

                if(JFileChooser.APPROVE_OPTION != retVal){
                    return; // user cancelled
                }

                File savefile = jfc.getSelectedFile();

                // save image as PNG, doc re. saving BufferedImage:
                // http://download.oracle.com/javase/tutorial/2d/images/saveimage.html
                // TODO: other formats, esp. lossless format (bmp ?)
                BufferedImage bi = mJPanel.getCurrentImage();
                ImageIO.write(bi, "png", savefile);
            } catch (Exception ioe) {
                System.err.println(ioe);
            }
        }
    }
}
