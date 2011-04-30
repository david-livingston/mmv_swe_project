/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: 4/30/11
 * Time: 1:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class SimpleLogger {

    private final boolean DEBUG;

    public SimpleLogger(boolean debugEnabled){
        DEBUG = debugEnabled;
    }

    public void error(String errorLocation, String error){
        if(DEBUG)
            System.err.println("Error in: " + errorLocation + "; " + error);
    }

    public void nonFatalException(String error, Exception e){
        if(DEBUG){
            System.err.println(e.getClass() + ": " + error);
            e.printStackTrace();
        }
    }

    public void userError(String location, String error){
        if(DEBUG)
            System.out.println("User error at: " + location + ": " + error);
    }
}
