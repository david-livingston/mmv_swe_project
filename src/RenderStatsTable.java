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
 * TODO: tool-tips for some rows (esp. memory)
 * TODO: update thread
 * TODO: pull some of the GUI code here from MainWindow.java (or preferably in a separate class)
 */
public class RenderStatsTable {

    /** Holds the attribute value pair data displayed in the table. This implementation
     * of Map is chosen because its iteration order is the same as insertion order,
     * presumably related data will be entered consecutively so it makes sense to display
     * it the same way */
    private LinkedHashMap<String, String> rows = new LinkedHashMap<String, String>();

    private long renderStartTime = 0L;
    private long renderStopTime = 0L;
    private MandelCanvas canvas;

    public RenderStatsTable(MandelCanvas canvas) {
        updateCanvas(canvas);
        updateSystemStats();
    }

    private void updateCanvas(MandelCanvas canvas) {
        if(null != canvas)
            this.canvas = canvas;
        else {
            Main.log.error("RenderStatsTable.java", "null passed to updateCanvas()");
            return;
        }
        rows.put("Iteration Max", "" + StringFormats.strFromInt(canvas.getIterationMax()));
        canvas.updatePrisonerOrEscapeeCounts();
        rows.put("Prisoners", StringFormats.strFromInt(canvas.getPrisonerCount()) + "  " + StringFormats.strFromRatio(canvas.getPrisonerCount(), canvas.getLogicalImageSize().pixelCount()));
        rows.put("Escapees", StringFormats.strFromInt(canvas.getEscapeeCount()) + "  " + StringFormats.strFromRatio(canvas.getEscapeeCount(), canvas.getLogicalImageSize().pixelCount()));
        // rows.put("Render (or update) Time", getRenderTimerSecondsElapsed() + " secs");
        rows.put("Real Min", StringFormats.strFromDouble(canvas.getRenderRegion().getRealMin()));
        rows.put("Real Max", StringFormats.strFromDouble(canvas.getRenderRegion().getRealMax()));
        rows.put("Imag Min", StringFormats.strFromDouble(canvas.getRenderRegion().getImagMin()));
        rows.put("Imag Max", StringFormats.strFromDouble(canvas.getRenderRegion().getImagMax()));
        rows.put("Delta", StringFormats.strFromDouble(canvas.getDelta()));
        rows.put("Logical Size", "" + canvas.getLogicalImageSize());
        rows.put("Displayed Size", "" + canvas.getDisplayImageSize() + "  " + StringFormats.strFromRatio(canvas.getDisplayImageSize().pixelCount(), canvas.getLogicalImageSize().pixelCount()));
        rows.put("Aspect Ratio", StringFormats.strFromDouble(canvas.getLogicalImageSize().widthToHeight()));
    }

    private void updateSystemStats(){
        if(Main.stats_table_force_gc)
            System.gc(); // just a suggestion
        rows.put("CPU Count", "" + Main.systemInfo.getProcessorCount());
        rows.put("CPUs Used", "" + Main.systemInfo.getBestThreadCount());
        rows.put("Max Usable Mem", "" + StringFormats.strFromByteCount(Main.systemInfo.getMaxMemory()));
        rows.put("Remaining Mem", (Main.stats_table_force_gc ? "" : "~ ") + StringFormats.strFromByteCount(Main.systemInfo.getRemainingMemory()) + "  " + Main.systemInfo.getPercentRemainingMemoryAsString());
    }

    public void update(MandelCanvas canvas){
        updateCanvas(canvas);
        updateSystemStats();
    }

    public void update(){
        update(canvas);
    }

    /**
     * The GUI component expects data to be provided as a 2d array.
     *
     * @return
     */
    public String[][] getAttributeValues(){
        update();
        final String[][] out = new String[rows.size()][2];
        int ctr = 0;
        for(String key : rows.keySet())
            out[ctr++] = new String[] { " " + key + ": ", " " + rows.get(key) };
        return out;
    }

    public void clearRenderTimer(){
        renderStartTime = renderStopTime = 0L;
    }

    public void startRenderTimer(){
        clearRenderTimer();
        renderStartTime = System.currentTimeMillis();
    }

    public void stopRenderTimer(){
        renderStopTime = System.currentTimeMillis();
        assert renderStopTime >= renderStartTime;
    }

    public long getRenderTimerSecondsElapsed(){
        assert renderStartTime >= 0L;
        assert renderStopTime >= renderStartTime;

        if(0L == renderStopTime){ // still timing || not started
            return 0L != renderStartTime ? (System.currentTimeMillis() - renderStartTime)/1000L : 0L;
        }else{
            return (renderStopTime - renderStartTime)/1000L;
        }
    }
}
