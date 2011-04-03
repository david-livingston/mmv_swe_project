/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: 4/3/11
 * Time: 11:38 AM
 * To change this template use File | Settings | File Templates.
 */
public class Global {

    private static final boolean debug = true;

    public static void assertState(boolean validState){
        if(!debug) return;

        if(!validState)
            throw new IllegalStateException();
    }

    public static void checkArgument(boolean validState){
        if(!debug) return;

        if(!validState)
            throw new IllegalArgumentException();
    }
}
