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
        final int threadCount = Static.systemInfo.getBestThreadCount();
        workers = new Thread[threadCount];
        for(int i = 0; i < workers.length; ++i)
            workers[i] = new Thread(new RenderThread(canvas, (i + 1)), "RenderThread-" + (i+1) + "of" + threadCount);
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
}
