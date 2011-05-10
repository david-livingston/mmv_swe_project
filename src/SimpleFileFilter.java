import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: 4/6/11
 * Time: 12:12 PM
 * To change this template use File | Settings | File Templates.
 *
 * Base functionality for a GUI file dialog filter. Display directories and
 * files ending in the specified extension. Description provided to user re.
 * the filter is simply the extension name unless overridden to be more
 * helpful.
 *
 * Makes the following features more convenient for the user:
 * Requirement 1.1.7 Ability to save a PNG Image
 * Requirement 1.1.8 Ability to save the current state using a custom file format
 * Requirement 1.1.9 Ability to open a state file
 */
public abstract class SimpleFileFilter extends FileFilter {

    public abstract String getExtension();

    public boolean accept(File file) {
        return file.isDirectory() || file.getName().toLowerCase().endsWith("." + getExtension());
    }

    public String getDescription() {
        return "*." + getExtension();
    }
}
