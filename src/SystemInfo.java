/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: 4/30/11
 * Time: 10:58 AM
 * To change this template use File | Settings | File Templates.
 *
 * Methods relating to the JVM and computer running the application.
 *
 * Requirement 1.1.2 Statistics Window
 * Requirement 1.2.3 Multithreading
 */
public class SystemInfo {

    private final int processorCount;
    private final long maxMemory;
    public static final int UNSPECIFIED_RESERVED_CORES = -2730; // or any rand negative value
    private int reservedProcessorCores = UNSPECIFIED_RESERVED_CORES;


    /**
     * Setting this to true will make the value displayed for remaining
     * memory both more accurate and less volatile. However, updates
     * will take SIGNIFICANTLY longer.
     */
    private final boolean forceGCBeforeUpdate;

    public SystemInfo() {
        final Runtime r = Runtime.getRuntime();
        processorCount = r.availableProcessors();
        maxMemory = r.maxMemory();
        forceGCBeforeUpdate = false;
    }

    /**
     * Ideally, I wouldn't have to repeat the code inside the constructor.
     * But Java (at least Oracle's std jdk) won't allow me to factor out
     * the final variable initializers into a common method (even if
     * both constructors call it) and doesn't think programmers need
     * default arguments ಠ_ಠ
     *
     * @param forceGCBeforeUpdate
     */
    public SystemInfo(final boolean forceGCBeforeUpdate) {
        final Runtime r = Runtime.getRuntime();
        processorCount = r.availableProcessors();
        maxMemory = r.maxMemory();
        this.forceGCBeforeUpdate = forceGCBeforeUpdate;
    }

    public int getProcessorCount() {
        return processorCount;
    }

    /**
     * @return the amount of system memory usable by the JVM, command line configurable, usually 256MB
     *   which is often *very* close to the amount of memory required to run this program.
     */
    public long getMaxMemory() {
        return maxMemory;
    }

    /**
     * @return the amount of memory the program can still use (bytes) before throwing an Exception
     */
    public long getRemainingMemory() {
        final Runtime r = Runtime.getRuntime();

        if(forceGCBeforeUpdate)
            r.gc(); // just a suggestion

        // freeMemory is memory that has been allocated but is unused
        // add to the memory that can still be allocated to get the metric we care about
        return r.freeMemory() + (getMaxMemory() - r.totalMemory());
    }

    public String getPercentRemainingMemoryAsString() {
        // return (int) (100.0 * ((double)getRemainingMemory())/getMaxMemory());
        return StringFormats.strFromRatio(getRemainingMemory(), getMaxMemory());
    }

    /**
     * Ideally this would be user configurable. Method attempts to balance
     * system responsiveness with application performance during renders.
     *
     * Requirement 1.2.3 Program will use multithreading for faster processing of images
     *
     * @return
     */
    public int getBestThreadCount() {
        final int cpus = getProcessorCount();
        if(reservedProcessorCores != UNSPECIFIED_RESERVED_CORES){
            return cpus - reservedProcessorCores;
        } else { // user did not specify, guess
            if(cpus >= 12)
                return cpus - 2;
            else if(cpus >= 6)
                return cpus - 1;
            else if(cpus > 0) // just b/c I'm paranoid
                return cpus;
            else
                return 1;
        }
    }

    /**
     * Tests whether the program was launched with assertions enabled (will probably
     * be false unless user made an effort to pass -ea or -enableassertions to the
     * JVM when launching).
     *
     * @return
     */
    public static boolean areAssertionsEnabled(){
        boolean assertsEnabled = false;
        // intentional side effect to detect whether assertions are enabled, based on example at:
        // http://download.oracle.com/javase/1.4.2/docs/guide/lang/assert.html
        assert assertsEnabled = true;
        return assertsEnabled;
    }

    public void setReservedCores(int cores_reserved) {
        reservedProcessorCores = cores_reserved;
    }
}
