/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: 4/26/11
 * Time: 11:01 AM
 * To change this template use File | Settings | File Templates.
 */
public class RenderThreadManager implements Runnable {

    private final Thread[] workers;

    public RenderThreadManager(final MandelCanvas canvas) {
        workers = new Thread[getBestThreadCount()];
        for(int i = 0; i < workers.length; ++i)
            workers[i] = new Thread(new RenderThread(canvas, (i + 1)), "RenderThread-" + (i+1) + "of" + getBestThreadCount());
    }

    public void run() {
        for(Thread t : workers)
            t.start();

        // todo - there has to be a way to get notified with a callback upon thread termination
        // rather than sleeping & polling to check if it's done
        Thread old = Thread.currentThread();
        try{
            do {
                old.sleep(200);
            } while (!isFinished());
        }catch(Exception e){
            System.err.println(e);
        }
    }

    public boolean isFinished(){
        for(Thread t : workers)
            if(t.isAlive())
                return false;
        return true;
    }

    public static int getBestThreadCount(){
        int cpus = Runtime.getRuntime().availableProcessors();
        if(cpus >= 6) // allow high core count machines to maintain one cpu for other tasks
            --cpus;
        else if(cpus < 2) // require at least 2 threads
            cpus = 2;
        return cpus;
    }
}
