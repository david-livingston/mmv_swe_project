import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: Feb 19, 2011
 * Time: 1:15:11 PM
 * To change this template use File | Settings | File Templates.
 *
 * The main menu bar which will be added to the main GUI.
 *
 * Requirement 1.1.1 A menu bar will be used to hold various features that will be used.
 *
 * See: http://download.oracle.com/javase/tutorial/uiswing/components/menu.html
 */
public class MenuBar extends JMenuBar implements ActionListener {

    final private static String URL_HELP_TOPICS = "http://www.davidlivingston.info/mmv/help-topics.html";
    final private static String URL_ABOUT = "http://www.davidlivingston.info/mmv/about.html";
    final private static String URL_PROJECT_HOMEPAGE = "https://github.com/david-livingston/mmv_swe_project";

    // TODO: add these to a set and throw Exception if duplicate; event handling code assumes
    // these will be unique; has to be a less hackish way to do this...
    final private static String MENU_ITEM_SAVE_IMAGE_AS = "Save Image As...";
    final private static String MENU_ITEM_SAVE_STATE_AS = "Save State As...";
    final private static String MENU_ITEM_OPEN_STATE_FILE = "Open State File...";
    final private static String MENU_ITEM_HOME = "Home";
    final private static String MENU_ITEM_BACK = "Back";
    final private static String MENU_ITEM_NEXT = "Next";
    final private static String MENU_ITEM_REFRESH = "Refresh";
    final private static String MENU_ITEM_INCREASE_MAX_ITERATIONS = "Increase Max Iterations";
    final private static String MENU_ITEM_INPUT_MAX_ITERATIONS = "Input Max Iterations";
    final private static String MENU_ITEM_HELP_TOPICS = "Help Topics";
    final private static String MENU_ITEM_PROJECT_PAGE = "Project Homepage @ GitHub";
    final private static String MENU_ITEM_ABOUT = "About";

    final private MandelJPanel mJPanel;

    final private PaletteSet palettes = new PaletteSet();
    final private HashMap<String, JCheckBoxMenuItem> colorMenuItems = new HashMap<String, JCheckBoxMenuItem>();

    private File lastFolderAccessed = null;
    private final MainWindow mainWindow;

    /**
     * @param panel the container which this menubar will be added to; needed
     * so the selected menuitem will have a way to invoke the requested action
     */
    public MenuBar(MandelJPanel panel, MainWindow appWindow){
        super();

        mJPanel = panel;
        mainWindow = appWindow;

        add(makeMenu("File",
                MENU_ITEM_SAVE_IMAGE_AS,
                MENU_ITEM_SAVE_STATE_AS,
                MENU_ITEM_OPEN_STATE_FILE
        ));
        // todo: resolution
        add(makeMenu("Navigation",
                MENU_ITEM_HOME,
                MENU_ITEM_BACK,
                MENU_ITEM_NEXT
        ));
        add(makeColorPaletteMenu("Color Palettes",
                palettes,
                colorMenuItems
        ));
        add(makeMenu("Image Options",
            MENU_ITEM_REFRESH,
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

    private JMenu makeColorPaletteMenu(final String topLevelName, final PaletteSet ps, HashMap<String, JCheckBoxMenuItem> colorMenuItems){
        final JMenu menu = new JMenu(topLevelName);
        for(String entry : ps.getNames()){
            JCheckBoxMenuItem item = new JCheckBoxMenuItem(entry);
            if(entry.equals(ps.getDefault().getName()))
                item.setSelected(true);
            colorMenuItems.put(entry, item);
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
        mJPanel.getNavigationHistory().getCurrent().setmJPanel(mJPanel);
        mJPanel.grabFocus();

        // why doesn't java have case stmts on Strings yet?
        // File | Save Image As...
        if(matches(e, MENU_ITEM_SAVE_IMAGE_AS)){
            saveImage();
        }
        // File | Save State As
        else if(matches(e, MENU_ITEM_SAVE_STATE_AS)){
            saveState();
        }
        // File | Open State File
        else if(matches(e, MENU_ITEM_OPEN_STATE_FILE)) {
            openState();
        }

        // Navigation | Home
        else if (matches(e, MENU_ITEM_HOME)) {
            navHome();
        }
        // Navigation | Back
        else if (matches(e, MENU_ITEM_BACK)) {
            navBack();
        }
        // Navigation | Next
        else if (matches(e, MENU_ITEM_NEXT)) {
            navForward();
        }

        // Color Palettes
        else if(matches(e, palettes)) {
            changePalette(e, palettes);
        }

        // Image Options | Refresh
        else if (matches(e, MENU_ITEM_REFRESH)) {
            refresh();
        }
        // Image Options | Increase Max Iterations
        else if (matches(e, MENU_ITEM_INCREASE_MAX_ITERATIONS)) {
            increaseMaxIterations();
        }
        // Image Options | Input Max Iterations
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
            Main.log.error("MenuBar.actionPerformed()", "Feature not implemented: " + e.getActionCommand());
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
    private boolean matches(ActionEvent e, String key){
        return e.getActionCommand().equals(key);
    }

    private boolean matches(ActionEvent e, PaletteSet ps){
        for(String name : ps.getNames())
            if(matches(e, name))
                return true;
        return false;
    }

    private boolean changePalette(ActionEvent e, PaletteSet ps){
        String name = null;
        for(String n : ps.getNames())
            if(matches(e, n))
                name = n;

        if(null == name)
            return false;

        for(String itemName : colorMenuItems.keySet())
            colorMenuItems.get(itemName).setSelected(itemName.equals(name));

        mJPanel.getNavigationHistory().getCurrent().setPalette(name);
        mJPanel.refreshBufferedImage();

        return true;
    }

    /**
     * Saves the image currently being displayed to the user at a file location of the
     * user's choosing. Uses a filechooser dialog.
     *
     * @return whether the file was successfully saved
     */
    private boolean saveImage(){

        PNGSimpleFileFilter filter = new PNGSimpleFileFilter();

        final File saveFile = FileUtilities.getFileFromSaveDialog(
            mJPanel,
            "mandel",
            null == lastFolderAccessed ? FileUtilities.getDesktop() : lastFolderAccessed,
            filter,
            false
        );

        if(null == saveFile)
            return false;

        lastFolderAccessed = saveFile.getParentFile();

        try {
            // save image as PNG, doc re. saving BufferedImage:
            // http://download.oracle.com/javase/tutorial/2d/images/saveimage.html
            BufferedImage bi = mJPanel.getCurrentLogicalImage();
            ImageIO.write(bi, filter.getExtension(), saveFile);
        } catch (Exception ioe) {
            Main.log.nonFatalException("", ioe);
            return false;
        }

        return true;
    }

    public boolean saveState() {

        MMVSimpleFileFilter filter = new MMVSimpleFileFilter();

        final File saveFile = FileUtilities.getFileFromSaveDialog(
            mJPanel,
            "mandel",
            null == lastFolderAccessed ? FileUtilities.getDesktop() : lastFolderAccessed,
            filter,
            true
        );

        if(null == saveFile)
            return false;

        lastFolderAccessed = saveFile.getParentFile();

        try {
            // http://java.sun.com/developer/technicalArticles/Programming/serialization/
            FileOutputStream fos = new FileOutputStream(saveFile);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            final MandelCanvas curr = mJPanel.getNavigationHistory().getCurrent();
            final SaveableState ss = new SaveableState(curr.getRenderRegion(), curr.getLogicalImageSize(), curr.getDisplayImageSize(), curr.getIterationMax(), curr.getPalette().getName(), false);
            out.writeObject(ss);
            out.close();
            mJPanel.getNavigationHistory().getCurrent().setAsSaved();
        } catch (Exception ioe) {
            Main.log.nonFatalException("saving state file", ioe);
            return false;
        }

        return true;
    }


    private boolean openState() {
        final File selectedFile = FileUtilities.getFileFromOpenDialog(
            mJPanel,
            null == lastFolderAccessed ? FileUtilities.getDesktop() : lastFolderAccessed,
            new MMVSimpleFileFilter()
        );

        if(null == selectedFile)
            return false;

        lastFolderAccessed = selectedFile.getParentFile();

        MandelCanvas canvas;

        try {
            canvas = MandelCanvasFactory.unmarshallFromSaveableState(selectedFile);
            canvas.setAsSaved();
        } catch (Exception e) {
            Main.log.nonFatalException("unmarshalling file", e);
            return false;
        }

        mJPanel.getNavigationHistory().setCurrent(canvas);
        mJPanel.refreshBufferedImage();
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
    public boolean openWebPage(final String url){
        try {
            java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
        } catch (Exception e){
            Main.log.nonFatalException("Error opening webpage: " + url, e);
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
                Main.log.userError("MenuBar.changeMaxIterations()", "iterMax can't be negative, input was: " + input);
                return false;
            }
        } catch (Exception e) {
            Main.log.error("MenuBar.changeMaxIterations()", "could not parse input as integer: " + input);
            return false;
        }
        mJPanel.getNavigationHistory().getCurrent().setIterationMax(newIterMax);
        mJPanel.refreshBufferedImage();
        mJPanel.updateRenderStats();
        return true;
    }

    private void refresh(){
        mJPanel.refreshBufferedImage();
        mJPanel.updateRenderStats();
    }
}
