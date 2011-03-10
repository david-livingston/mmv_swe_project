import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: Feb 19, 2011
 * Time: 1:15:11 PM
 * To change this template use File | Settings | File Templates.
 */

// http://download.oracle.com/javase/tutorial/uiswing/components/menu.html
    
public class MenuBar extends JMenuBar implements ActionListener {

    public MenuBar(){
        super();
        JMenu fileMenu = new JMenu("File");
        JMenuItem saveImage = new JMenuItem("Save Image As...");
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
    }
}
