import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: 4/30/11
 * Time: 2:27 PM
 * To change this template use File | Settings | File Templates.
 *
 * Collection of static methods for dealing with File paths. Does not completely implement,
 * but supports the features specified by:
 *  - Requirement 1.1.7 Ability to save a PNG image
 *  - Requirement 1.1.8 Ability to save current state in a custom file format
 *  - Requirement 1.1.9 Ability to open a saved state file
 *
 */
public class FileUtilities {

    /**
     * Considers the path of an input File and guarantees that it ends with the correct extension.
     *
     * Examples: (one of the few places I wish we had done unit testing for the program)
     *  ensureExtension(new File("foo.txt"), "png") yields file path "foo.txt.png"
     *  ensureExtension(new File("foo.txt"), "txt") yields the input file at path "foo.txt"
     *  ensureExtension(new File("foo.txt"), ".png" error, should not include '.' as beginning of extension
     *  ensureExtension(new File("foo"), "txt.bak") yields file path "foo.txt.bak"
     *
     * Notice, an incorrect extension in the input file is not removed. This is because files may contain
     * periods in their name, and extensions while typically 3 characters may be longer. There's fool proof
     * way to distinguish between a file with the wrong extension and a file with a period in its name.
     *
     * @param file has the correct name and path but may not have the correct extension
     * @param ext the desired extension for the file name, should NOT include a period as the first
     *  character
     * @return the input File but with the path modified as necessary
     */
    public static File ensureExtension(final File file, final String ext){
        assert '.' != ext.charAt(0);
        if(file.getName().endsWith("." + ext))
            return file;
        else
            return new File(file.getParent(), file.getName() + "." + ext);
    }

    /**
     * Creates a file with a number suffix in the name if necessary to avoid name collisions.
     *
     * Example: if the current working directory contains a single file named "foo.png", then:
     *  makeFileWithSequentialIDSuffix(new File("foo"), "png", false) yields a file with path "foo (1).png"
     *  makeFileWithSequentialIDSuffix(new File("bar"), "png", false) yields a file with path "bar.png"
     *
     * Limitation: only attempts to create number suffixes up to a certain hardcoded limit, the
     *  checks to see if files with a given name exist may be slow (e.g. on a network share) and
     *  we don't want the program to freeze up if it encounters a directory with a ridiculous number
     *  of files with this naming format, although this may be a premature optimization
     *
     * @param file
     * @param ext
     * @param includeBuildString
     * @return
     */
    public static File makeFileWithSequentialIDSuffix(final File file, final String ext, boolean includeBuildString){
        assert !file.isDirectory() && (!file.exists() || file.isFile());
        final String name = file.getName();
        final String longerRawName = name + (includeBuildString? "_v" + VersionInfo.getVersion() : "");
        final String longerName = longerRawName + "." + ext;

        final File parent = file.getParentFile();
        File out = parent != null ? new File(parent, longerName) : new File(longerName);

        if(out.exists()){
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
     * @return
     */
    public static File getDesktop(){
        // adapted from: http://stackoverflow.com/questions/570401/in-java-under-windows-how-do-i-find-a-redirected-desktop-folder
        FileSystemView fileSys = FileSystemView.getFileSystemView();
        // why is this undocumented method named getHomeDirectory() rather than getDesktopDirectory()
        // does it ever fail?
        // might be a Windows only solution :| also look at FileSystemView.getRoots(): File[]
        final File desktop = fileSys.getHomeDirectory();
        if(!desktop.exists() || !desktop.canRead())
            Main.log.error("FileUtilities.getDesktop()", "location: " + desktop.getAbsolutePath() + ", exists: " + desktop.exists() + ", can read: " + desktop.canRead());
        return desktop;
    }

    /**
     * Uses a save file dialog to allow a user to select a file path that the program can then use (presumably as a
     * location to save a file to).
     *
     * @param window the save dialog will be associated with this gui element (is it okay to pass null here?)
     * @param defaultName name that will initially show in the save dialog, will be the file name returned if the
     *   user does not change it, attempts to avoid name collisions by including sequential number id (although
     *   this feature only works for the directory the dialog initially opens in, would require more extensive
     *   code to work as the user changed to other directories). Including the extension is optional, the
     *   method will add an extension based on the file filter if no extension is included in the name.
     * @param initialDirectory the directory where the file dialog opens, may be changed by user, may be null
     * @param filter // todo: should be able to pass a list of file filters
     * @param includeBuildString
     * @return
     */
    public static File getFileFromSaveDialog(final Component window, final String defaultName, final File initialDirectory, final SimpleFileFilter filter, final boolean includeBuildString){
        assert null != window; // todo: look into using non-nullable annotations instead of sprinkling these asserts everywhere
        assert null != filter;

        final JFileChooser jfc = new JFileChooser();
        if(null != filter)
            jfc.setFileFilter(filter);
        {
            final File initialFile = initialDirectory == null ? new File(defaultName) : new File(initialDirectory, defaultName);
            jfc.setSelectedFile(makeFileWithSequentialIDSuffix(initialFile, filter.getExtension(), includeBuildString));
        }
        if(JFileChooser.APPROVE_OPTION != jfc.showSaveDialog(window))
            return null;

        final File saveFile = ensureExtension(jfc.getSelectedFile(), filter.getExtension());

        if(saveFile.exists()) {
            switch(JOptionPane.showConfirmDialog(window, "File: " + saveFile.getName() + " already exists.\nOverwrite?")){
                case JOptionPane.OK_OPTION :
                    break;
                case JOptionPane.NO_OPTION :
                    return getFileFromSaveDialog(window, defaultName, initialDirectory, filter, includeBuildString);
                case JOptionPane.CANCEL_OPTION :
                    return null;
                default:
                    Main.log.error("MenuBar.saveImage()", "unexpected value returned by JOptionPane.showConfirmDialog()");
            }
        }

        return saveFile;
    }

    /**
     * Uses an open dialog to allow a user to select a file path to pass to the program (presumably for the program
     * to open).
     *
     * @param window the save dialog will be associated with this gui element (okay to pass null here?)
     * @param initialDirectory the folder initially selected for viewing when the dialog opens, does not prevent the
     *  user from navigating to another folder to find a file to open, okay to pass null here
     * @param filter
     * @return
     */
    public static File getFileFromOpenDialog(final Component window, File initialDirectory, final SimpleFileFilter filter){
        assert null != window; // todo: look into using non-nullable annotations instead of sprinkling these asserts everywhere
        assert null != filter;

        final JFileChooser jfc = new JFileChooser();
        if(null != filter)
            jfc.setFileFilter(filter);

        if(null == initialDirectory)
            initialDirectory = new File("tmp").getParentFile();

        // see if there is a file matching the input filter in the initial directory that we can
        // pre-select for the user
        if(null != initialDirectory){
            File mostRecent = null;
            for(File f : initialDirectory.listFiles())
                if(f.isFile() && filter.accept(f)){
                    if(null == mostRecent)
                        mostRecent = f;
                    else if(f.lastModified() > mostRecent.lastModified())
                        mostRecent = f;
                }
            if(null != mostRecent)
                jfc.setSelectedFile(mostRecent);
        }

        if(JFileChooser.APPROVE_OPTION != jfc.showOpenDialog(window))
            return null;

        return jfc.getSelectedFile();
    }
}
