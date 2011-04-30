import java.util.LinkedHashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: 4/30/11
 * Time: 9:11 AM
 * To change this template use File | Settings | File Templates.
 */
public class RenderStatsTable {

    /** Holds the attribute value pair data displayed in the table. This implementation
     * of Map is chosen because its iteration order is the same as insertion order,
     * presumably related data will be entered consecutively so it makes sense to display
     * it the same way */
    LinkedHashMap<String, String> rows = new LinkedHashMap<String, String>();


}
