import java.math.BigDecimal;
import java.util.LinkedHashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: 4/30/11
 * Time: 9:11 AM
 * To change this template use File | Settings | File Templates.
 *
 * Model for the statistics table shown in the GUI.
 *
 * TODO: time renders
 * TODO: display stats about how many pixels are prisoners/escapees
 * TODO: tool-tips for some rows (esp. memory)
 * TODO: update thread
 * TODO: pull some of the GUI code here from MainWindow.java (or preferably in a separate class)
 * TODO: formatting of values (e.g. commas in integer values > 999)
 */
public class RenderStatsTable {

    /** Holds the attribute value pair data displayed in the table. This implementation
     * of Map is chosen because its iteration order is the same as insertion order,
     * presumably related data will be entered consecutively so it makes sense to display
     * it the same way */
    private LinkedHashMap<String, String> rows = new LinkedHashMap<String, String>();

    public RenderStatsTable(MandelCanvas canvas) {
        updateCanvas(canvas);
        updateSystemStats();
    }

    public void updateCanvas(MandelCanvas canvas) {
        rows.put("Real Min", strFromDouble(canvas.getRenderRegion().getRealMin()));
        rows.put("Real Max", strFromDouble(canvas.getRenderRegion().getRealMax()));
        rows.put("Imag Min", strFromDouble(canvas.getRenderRegion().getImagMin()));
        rows.put("Imag Max", strFromDouble(canvas.getRenderRegion().getImagMax()));
        rows.put("Delta", strFromDouble(canvas.getDelta()));
        rows.put("Logical Size", "" + canvas.getLogicalImageSize());
        rows.put("Displayed Size", "" + canvas.getDisplayImageSize());
        rows.put("Aspect Ratio", strFromDouble(canvas.getLogicalImageSize().widthToHeight()));
    }

    public void updateSystemStats(){
        rows.put("CPU Count", "" + Main.systemInfo.getProcessorCount());
        rows.put("Max Usable Mem", "" + strFromByteCount(Main.systemInfo.getMaxMemory()));
        rows.put("Remaining Mem", "" + strFromByteCount(Main.systemInfo.getRemainingMemory()) + "  (" + Main.systemInfo.getPercentRemainingMemory() + "%)");
    }

    /**
     * todo: control how many decimal places are displayed
     *
     * @param d
     * @return
     */
    private static String strFromDouble(Double d){
        return new BigDecimal(d).toPlainString();
    }

    private static String strFromByteCount(long size){
        return (size/(1024 * 1024)) + " MB";
    }

    /**
     * The GUI component expects data to be provided as a 2d array.
     *
     * @return
     */
    public String[][] getAttributeValues(){
        final String[][] out = new String[rows.size()][2];
        int ctr = 0;
        for(String key : rows.keySet())
            out[ctr++] = new String[] { " " + key + ": ", " " + rows.get(key) };
        return out;
    }
}
