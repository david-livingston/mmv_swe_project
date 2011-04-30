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
 */
public class FileUtilities {

    public static File ensureExtension(final File file, final String ext){
        if(file.getName().endsWith("." + ext))
            return file;
        else
            return new File(file.getParent(), file.getName() + "." + ext);
    }

    public static File makeFileWithSequentialIDSuffix(final String name, final String ext, boolean includeBuildString){
        final String longerRawName = name + (includeBuildString? "_v" + VersionInfo.getVersion() : "");
        final String longerName = longerRawName + "." + ext;

        final File desktop = getDesktop();
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

    public static File getDesktop(){
        // adapted from: http://stackoverflow.com/questions/570401/in-java-under-windows-how-do-i-find-a-redirected-desktop-folder
        FileSystemView fileSys = FileSystemView.getFileSystemView();
        // why is this undocumented method named getHomeDirectory() rather than getDesktopDirectory()
        // does it ever fail?
        // might be a Windows only solution :| also look at FileSystemView.getRoots(): File[]
        final File desktop = fileSys.getHomeDirectory();
        if(!desktop.exists() || !desktop.canRead())
            Static.log.error("FileUtilities.getDesktop()", "location: " + desktop.getAbsolutePath() + ", exists: " + desktop.exists() + ", can read: " + desktop.canRead());
        return desktop;
    }

    public static File getFileFromSaveDialog(final Component window, final String defaultName, final File initialDirectory, final SimpleFileFilter filter, final boolean includeBuildString){
        final JFileChooser jfc = new JFileChooser();
        if(null != filter)
            jfc.setFileFilter(filter);
        jfc.setSelectedFile(makeFileWithSequentialIDSuffix(defaultName, filter.getExtension(), includeBuildString));

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
                    Static.log.error("MenuBar.saveImage()", "unexpected value returned by JOptionPane.showConfirmDialog()");
            }
        }

        return saveFile;
    }

    public static File getFileFromOpenDialog(final Component window, final File initialDirectory, final SimpleFileFilter filter){
        final JFileChooser jfc = new JFileChooser();
        if(null != filter)
            jfc.setFileFilter(filter);

        if(null != initialDirectory){
            for(File f : initialDirectory.listFiles())
                if(f.isFile() && filter.accept(f)){
                    jfc.setSelectedFile(f);
                    System.out.println("selected: " + f);
                    break;
                }
        }

        if(JFileChooser.APPROVE_OPTION != jfc.showOpenDialog(window))
            return null;

        return jfc.getSelectedFile();
    }
}
