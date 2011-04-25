import org.omg.CORBA.PUBLIC_MEMBER;

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: 4/3/11
 * Time: 11:38 AM
 * To change this template use File | Settings | File Templates.
 */
public class Global {

    private static final boolean debug = true;

    private static final int serialID = 2;

    private static final int majorVersion = 1;
    private static final char minorVersion = 'D';
    private static final int buildVersion = 0;

    public static String getVersion(){
        return serialID +
            "[" + majorVersion + "." +
            minorVersion + "." +
            buildVersion + "]" +
            (isDebugEnabled() ? "D" : "")
        ;
    }

    public static String getTitle(){
        return "MMV: Multithreaded Mandelbrot Viewer \t \t v" +
            getVersion() + " \t \t " +
            (isDebugEnabled() ? "--DEBUG MODE--" : "")
        ;
    }

    public static boolean isDebugEnabled(){
        return debug;
    }

    public static void logError(String errorLocation, String error){
        if(isDebugEnabled())
            System.err.println("Error in: " + errorLocation + "; " + error);
    }

    public static void logNonFatalException(String error, Exception e){
        if(isDebugEnabled()){
            System.err.println(e.getClass() + ": " + error);
            e.printStackTrace();
        }
    }
}
