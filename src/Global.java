/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: 4/3/11
 * Time: 11:38 AM
 * To change this template use File | Settings | File Templates.
 */
public class Global {

    // set to true for developer builds
    // in regular builds, user can enable assertions to activiate debug mode
    private final static boolean forceDebugMode = true;

    // incremented each time a build is released to be tested
    private static final int serialID = 2;

    // incremented after adding a major feature
    private static final int majorVersion = 3;
    // significant changes or bug fix, new minor feature
    private static final char minorVersion = 'A';
    // a new commit (if i remember) but not significant enough to incr minorVersion
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
        if(forceDebugMode)
            return true;

        boolean assertsEnabled = false;
        // intentional side effect to detect whether assertions are enabled, based on example at:
        // http://download.oracle.com/javase/1.4.2/docs/guide/lang/assert.html
        assert assertsEnabled = true;
        return assertsEnabled;
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

    public static void logUserError(String location, String error){
        System.out.println("User error at: " + location + ": " + error);
    }
}
