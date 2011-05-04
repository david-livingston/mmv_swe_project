import javax.swing.*;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: 4/30/11
 * Time: 1:57 PM
 * To change this template use File | Settings | File Templates.
 *
 * The class which launches the program, primarily by calling the MainWindow
 * constructor. This functionality could be squeezed into MainWindow but
 * is factored out in case more pre- or post- main program run features
 * are needed.
 */
public class Main {

    final public static SystemInfo systemInfo = new SystemInfo();
    final public static SimpleLogger log = new SimpleLogger(VersionInfo.DEBUG);
    final public static ImageSize defaultLogicalImageSize = ImageSize.FAKE_HD;

    /**
     * Only entry point of the program.
     *
     * @param args
     */
    public static void main(String[] args) {
        try { // Set System L&F
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            log.error("GUI.main()", "Exception setting native look & feel: " + e);
        }

        File fileToOpen = null;
        boolean sawOpenArg = false;

        for(String arg : args)
            if(sawOpenArg){
                fileToOpen = new File(arg);
                break;
            } else
                sawOpenArg = arg.equalsIgnoreCase("-open");

        ImageSize initialLogicalImageSize = defaultLogicalImageSize;
        boolean sawSizeArg = false;

        for(String arg : args)
            if(sawSizeArg){
                if(arg.equalsIgnoreCase("hd") || arg.equalsIgnoreCase("real_hd"))
                    initialLogicalImageSize = ImageSize.REAL_HD;
                else if(arg.equalsIgnoreCase("fake_hd"))
                    initialLogicalImageSize = ImageSize.FAKE_HD;
                else if(arg.equalsIgnoreCase("huge"))
                    initialLogicalImageSize = ImageSize.GREAT_MONITOR;
                else if(arg.equalsIgnoreCase("sd") || arg.equalsIgnoreCase("sd1"))
                    initialLogicalImageSize = ImageSize.EXAMPLE_NTSC_16_to_9_SD;
                else if(arg.equalsIgnoreCase("sd2"))
                    initialLogicalImageSize = ImageSize.EXAMPLE_NTSC_4_to_3_SD;
                break;
            } else
                sawSizeArg = arg.equalsIgnoreCase("-size");

        new MainWindow(initialLogicalImageSize, fileToOpen);
    }

}
