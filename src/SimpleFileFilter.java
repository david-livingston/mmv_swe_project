import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: 4/6/11
 * Time: 12:12 PM
 * To change this template use File | Settings | File Templates.
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
