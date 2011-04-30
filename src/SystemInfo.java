import javax.swing.filechooser.FileSystemView;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: 4/30/11
 * Time: 10:58 AM
 * To change this template use File | Settings | File Templates.
 */
public class SystemInfo {

    private final int processorCount;
    private final long maxMemory;

    public SystemInfo() {
        final Runtime r = Runtime.getRuntime();
        processorCount = r.availableProcessors();
        maxMemory = r.maxMemory();
    }

    public int getProcessorCount() {
        return processorCount;
    }

    public long getMaxMemory() {
        return maxMemory;
    }

    /**
     * @return the amount of memory the program can still use (bytes) before throwing an Exception
     */
    public long getRemainingMemory() {
        final Runtime r = Runtime.getRuntime();
        // freeMemory is memory that has been allocated but is unused
        // add to the memory that can still be allocated to get the metric we care about
        return r.freeMemory() + (getMaxMemory() - r.totalMemory());
    }
}
