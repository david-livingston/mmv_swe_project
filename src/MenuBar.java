import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;

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
public class MenuBar extends JMenuBar implements ActionListener {

    // TODO: installer, store these pages locally
    final private static String URL_HELP_TOPICS = "http://www.davidlivingston.info/mmv/help-topics.html";
    final private static String URL_ABOUT = "http://www.davidlivingston.info/mmv/about.html";
    final private static String URL_PROJECT_HOMEPAGE = "https://github.com/david-livingston/mmv_swe_project";

    // TODO: add these to a set and throw Exception if duplicate; event handling code assumes
    // these will be unique; has to be a less hackish way to do this...
    final private static String MENU_ITEM_KEY_SAVE_AS = "Save Image As...";
    final private static String MENU_ITEM_KEY_SAVE_STATE_AS = "Save State As...";
    final private static String MENU_ITEM_KEY_OPEN_STATE_FILE = "Open State File...";
    final private static String MENU_ITEM_KEY_HOME = "Home";
    final private static String MENU_ITEM_KEY_BACK = "Back";
    final private static String MENU_ITEM_KEY_NEXT = "Next";
    final private static String MENU_ITEM_INCREASE_MAX_ITERATIONS = "Increase Max Iterations";
    final private static String MENU_ITEM_INPUT_MAX_ITERATIONS = "Input Max Iterations";
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

        add(makeMenu("File",
            MENU_ITEM_KEY_SAVE_AS,
            MENU_ITEM_KEY_SAVE_STATE_AS,
            MENU_ITEM_KEY_OPEN_STATE_FILE
        ));
        // todo: color scheme
        // todo: resolution
        add(makeMenu("Navigation",
            MENU_ITEM_KEY_HOME,
            MENU_ITEM_KEY_BACK,
            MENU_ITEM_KEY_NEXT
        ));
        add(makeMenu("Advanced Options",
            MENU_ITEM_INCREASE_MAX_ITERATIONS,
            MENU_ITEM_INPUT_MAX_ITERATIONS
        ));
        add(makeMenu("Help",
            MENU_ITEM_HELP_TOPICS,
            MENU_ITEM_PROJECT_PAGE,
            MENU_ITEM_ABOUT
        ));
    }

    /**
     * Makes a menu with entries keyed on the var-arg string array, associates
     * each menu item with this object as an action listener
     *
     * @param topLevelName the menu name displayed for the group on the menubar
     * @param entries the individual menu elements
     * @return a menu ready to be added to the menu bar
     */
    private JMenu makeMenu(final String topLevelName, final String... entries){
        final JMenu menu = new JMenu(topLevelName);
        for(String entry : entries){
            JMenuItem item = new JMenuItem(entry);
            item.addActionListener(this);
            menu.add(item);
        }
        return menu;
    }

    /**
     * Call back for clicking an element of a dropdown menu
     *
     * @param e
     */
    public void actionPerformed(ActionEvent e){
        // this is necessary so the currently selected MandelCanvas (which nav history may have changed)
        // can get a handle to the render window so it can set the busy cursor before rerendering a region
        mJPanel.getNavigationHistory().getCurrent().setComponent(mJPanel);
        mJPanel.grabFocus();

        // why doesn't java have case stmts on Strings yet?
        // File | Save Image As...
        if(matches(e, MENU_ITEM_KEY_SAVE_AS)){
            saveImage("mandel", "png");
        }
        // File | Save State As
        else if(matches(e, MENU_ITEM_KEY_SAVE_STATE_AS)){
            saveState("mandel", "mmv");
        }
        // File | Open State File
        else if(matches(e, MENU_ITEM_KEY_OPEN_STATE_FILE)) {
            openState();
        }

        // Navigation | Home
        else if (matches(e, MENU_ITEM_KEY_HOME)) {
            navHome();
        }
        // Navigation | Back
        else if (matches(e, MENU_ITEM_KEY_BACK)) {
            navBack();
        }
        // Navigation | Next
        else if (matches(e, MENU_ITEM_KEY_NEXT)) {
            navForward();
        }

        // Advanced Options | Increase Max Iterations
        else if (matches(e, MENU_ITEM_INCREASE_MAX_ITERATIONS)) {
            increaseMaxIterations();
        }
        // Advanced Options | Input Max Iterations
        else if (matches(e, MENU_ITEM_INPUT_MAX_ITERATIONS)) {
            inputMaxIterations();
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
            Global.logError("MenuBar.actionPerformed()", "Feature not implemented: " + e.getActionCommand());
        }
    }

    private boolean openState() {
        MandelCanvas canvas = null;

        final JFileChooser jfc = new JFileChooser(findDesktop());
        jfc.setFileFilter(new MMVSimpleFileFilter());
        final int retVal = jfc.showOpenDialog(mJPanel);

        if(JFileChooser.APPROVE_OPTION != retVal){
            return false; // user cancelled
        }

        try {
            canvas = MandelCanvas.unmarshall(jfc.getSelectedFile());
        } catch (Exception e) {
            Global.logNonFatalException("unmarshalling file", e);
            return false;
        }

        mJPanel.getNavigationHistory().setCurrent(canvas);
        mJPanel.refreshBufferedImage();
        return true;
    }

    private boolean saveState(final String defaultName, final String defaultExtension) {
        try {
            // figure out where to save the image
            final JFileChooser jfc = new JFileChooser();
            MMVSimpleFileFilter ff = new MMVSimpleFileFilter();
            jfc.setFileFilter(ff);
            jfc.setSelectedFile(makeFileWithTimeTokenSuffix(defaultName, ff.getExtension(), true));
            final int retVal = jfc.showSaveDialog(mJPanel);

            if(JFileChooser.APPROVE_OPTION != retVal){
                return false; // user cancelled
            }

            final File saveFile = ensureExtension(jfc.getSelectedFile(), ff.getExtension());

            if(saveFile.exists()) {
                switch(JOptionPane.showConfirmDialog(mJPanel, "File: " + saveFile.getName() + " already exists.\nOverwrite?")){
                    case JOptionPane.OK_OPTION :
                        break;
                    case JOptionPane.NO_OPTION :
                        return saveState(defaultName, defaultExtension);
                    case JOptionPane.CANCEL_OPTION :
                        return false;
                    default:
                        Global.logError("MenuBar.saveState()", "unexpected value returned by JOptionPane.showConfirmDialog()");
                }
            }

            // http://java.sun.com/developer/technicalArticles/Programming/serialization/
            FileOutputStream fos = new FileOutputStream(saveFile);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(mJPanel.getNavigationHistory().getCurrent());
            out.close();
        } catch (Exception ioe) {
            Global.logNonFatalException("saving state file", ioe);
            return false;
        }
        return true;
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
    private boolean matches(ActionEvent e, String key){
        return e.getActionCommand().equals(key);
    }

    /**
     * Saves the image currently being displayed to the user at a file location of the
     * user's choosing. Uses a filechooser dialog.
     *
     * @param defaultName the file name prefilled in the save dialog
     * @param defaultExtension the file extension prefilled int he save dialog
     * @return whether the file was successfully saved
     */
    private boolean saveImage(final String defaultName, final String defaultExtension){
        try {
            // figure out where to save the image
            final JFileChooser jfc = new JFileChooser();
            PNGSimpleFileFilter ff = new PNGSimpleFileFilter();
            jfc.setFileFilter(ff);
            jfc.setSelectedFile(makeFileWithTimeTokenSuffix(defaultName, ff.getExtension(), false));
            final int retVal = jfc.showSaveDialog(mJPanel);

            if(JFileChooser.APPROVE_OPTION != retVal){
                return false; // user cancelled
            }

            final File saveFile = ensureExtension(jfc.getSelectedFile(), ff.getExtension());

            if(saveFile.exists()) {
                switch(JOptionPane.showConfirmDialog(mJPanel, "File: " + saveFile.getName() + " already exists.\nOverwrite?")){
                    case JOptionPane.OK_OPTION :
                        break;
                    case JOptionPane.NO_OPTION :
                        return saveImage(defaultName, defaultExtension);
                    case JOptionPane.CANCEL_OPTION :
                        return false;
                    default:
                        Global.logError("MenuBar.saveImage()", "unexpected value returned by JOptionPane.showConfirmDialog()");
                }
            }

            // save image as PNG, doc re. saving BufferedImage:
            // http://download.oracle.com/javase/tutorial/2d/images/saveimage.html
            // TODO: other formats, esp. lossless format (bmp ?)
            BufferedImage bi = mJPanel.getCurrentLogicalImage();
            ImageIO.write(bi, ff.getExtension(), saveFile);
        } catch (Exception ioe) {
            Global.logNonFatalException("", ioe);
            return false;
        }
        return true;
    }

    private File findDesktop(){
        // adapted from: http://stackoverflow.com/questions/570401/in-java-under-windows-how-do-i-find-a-redirected-desktop-folder
        FileSystemView fileSys = FileSystemView.getFileSystemView();
        // why is this undocumented method named getHomeDirectory() rather than getDesktopDirectory()
        // does it ever fail?
        // might be a Windows only solution :| also look at FileSystemView.getRoots(): File[]
        File desktop = fileSys.getHomeDirectory();
        if(!desktop.exists() || !desktop.canRead()) {
            Global.logError("MenuBar.findDesktop()", "location: " + desktop.getAbsolutePath() + ", exists: " + desktop.exists() + ", can read: " + desktop.canRead());
            return null;
        }
        return desktop;
    }

    private File ensureExtension(final File file, final String ext){
        if(file.getName().endsWith("." + ext))
            return file;
        else
            return new File(file.getParent(), file.getName() + "." + ext);
    }

    private File makeFileWithTimeTokenSuffix(final String name, final String ext, boolean includeBuildString){
        final String longerRawName = name + (includeBuildString? "_v" + Global.getVersion() : "");
        final String longerName = longerRawName + "." + ext;

        final File desktop = findDesktop();
        File out = desktop != null ? new File(desktop, longerName) : new File(longerName);

        if(out.exists()){
            File parent = out.getParentFile();
            for(int i = 1; i < 1000; ++i){
                File tmp = new File(parent, longerRawName + " (" + i + ")." + ext);
                if(!tmp.exists()){
                    out = tmp;
                    break;
                }
            }
        }

        return out;
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
    public boolean openWebPage(final String url){
        try {
            java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
        } catch (Exception e){
            Global.logNonFatalException("Error opening webpage: " + url, e);
            return false;
        }
        return true;
    }

    private void navHome() {
        mJPanel.getNavigationHistory().home();
        mJPanel.refreshBufferedImage();
    }

    private void navBack() {
        mJPanel.getNavigationHistory().back();
        mJPanel.refreshBufferedImage();
    }

    private void navForward() {
        mJPanel.getNavigationHistory().forward();
        mJPanel.refreshBufferedImage();
    }

    // todo: decreasing maxIterations has no effect; not sure whether to warn user it was ignored or actually decrease resolution?
    private boolean increaseMaxIterations(){
        mJPanel.getNavigationHistory().getCurrent().increaseIterationMax();
        mJPanel.refreshBufferedImage();
        mJPanel.updateRenderStats();
        return true;
    }

    // todo: decreasing maxIterations has no effect; not sure whether to warn user it was ignored or actually decrease resolution?
    private boolean inputMaxIterations(){
        final int iterMax = mJPanel.getNavigationHistory().getCurrent().getIterationMax();
        final String input = JOptionPane.showInputDialog(mJPanel,
            "Enter new iteration max (currently = " + iterMax + "): "
        );
        if(null == input) // user cancelled
            return true;
        int newIterMax;
        try {
            newIterMax = Integer.parseInt(input);
            if(newIterMax < 0){
                Global.logUserError("MenuBar.changeMaxIterations()", "iterMax can't be negative, input was: " + input);
                return false;
            }
        } catch (Exception e) {
            Global.logError("MenuBar.changeMaxIterations()", "could not parse input as integer: " + input);
            return false;
        }
        mJPanel.getNavigationHistory().getCurrent().setIterationMax(newIterMax);
        mJPanel.refreshBufferedImage();
        mJPanel.updateRenderStats();
        return true;
    }

}
