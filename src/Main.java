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
 *
 * This class is the entry point for the program, so supports all features,
 * but specifically:
 * Requirement 1.1.9 Ability to open save state file
 * supported by a command line option (-open filename)
 *
 * Requirement 1.2.2 HD Rendering and Decoupled Size
 * supports additional logical image resolutions via command line (-size sizename)
 *
 * Requirement 1.2.3 Multithreading
 * number of cores reserved is command line configurable (-reserved_cpus number)
 *
 * Requirement 1.1.2 Statistics Window & 1.2.0 GUI enhancements
 * Some update behavior command line configurable (-gc_before_stats_update)
 */
public class Main {

    // options which can be changed at command line
    final public static ImageSize defaultLogicalImageSize = ImageSize.REAL_HD;
    public static boolean stats_table_force_gc = false;

    final public static SystemInfo systemInfo = new SystemInfo();
    final public static SimpleLogger log = new SimpleLogger(VersionInfo.DEBUG);

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
                else if(arg.equalsIgnoreCase("dvd"))
                    initialLogicalImageSize = ImageSize.DVD;
                else if(arg.equalsIgnoreCase("sd2"))
                    initialLogicalImageSize = ImageSize.EXAMPLE_NTSC_4_to_3_SD;
                break;
            } else
                sawSizeArg = arg.equalsIgnoreCase("-size");

        for(String arg : args)
            if(arg.equalsIgnoreCase("-gc_before_stats_update")){
                stats_table_force_gc = true;
                break;
            }

        int cores_reserved = SystemInfo.UNSPECIFIED_RESERVED_CORES;
        boolean sawCoreArg = false;

        for(String arg : args)
            if(sawCoreArg){
                try {
                    cores_reserved = Integer.parseInt(arg);
                } catch (Exception e) {
                    log.nonFatalException("Invalid command line arg for reserved cores: " + arg, e);
                } finally {
                    break;
                }
            } else {
                sawCoreArg = arg.equalsIgnoreCase("-reserved_cpus");
            }

        if(cores_reserved != SystemInfo.UNSPECIFIED_RESERVED_CORES && cores_reserved >= 0 && cores_reserved < systemInfo.getProcessorCount())
            systemInfo.setReservedCores(cores_reserved);

        new MainWindow(initialLogicalImageSize, fileToOpen);
    }

}
