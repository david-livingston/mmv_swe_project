/**
 * Created by IntelliJ IDEA.
 * User: David
 * Date: Feb 13, 2011
 * Time: 7:25:30 PM
 * To change this template use File | Settings | File Templates.
 */

import java.io.Serializable;

/**
 * This class represents a complex point that is being iterated through the Mandelbrot
 * process. Initially the constant (starting) location and current location are the same.
 * The current location changes once per each iteration.
 */
public class MandelPoint implements Serializable {

    private final ComplexNumber startingLocation;
    private ComplexNumber currentLocation;
    private int iterationCount = 0;
    private final double squaredMaxDistance = 4.0;
    private boolean escaped = false;

    public MandelPoint(final ComplexNumber startingLocation){
        this.startingLocation = currentLocation = startingLocation;
    }

    // this is the key method in the program
    // it labels a complex point as either a prisoner or escapee of the mandelbrot set
    // if escapee it also indicates how long it took for the point to escape
    // see wikipedia article for details: http://en.wikipedia.org/wiki/Mandelbrot_set#Escape_time_algorithm
    public void iterate(final int iterationLimit){
        if(escaped)
            return;

        while(iterationCount++ < iterationLimit){
            currentLocation = ComplexNumber.add(startingLocation, ComplexNumber.square(currentLocation));
            if(currentLocation.sqrMagnitude() > squaredMaxDistance){
                escaped = true;
                break;
            }
        }
    }

    public boolean didEscape() { return escaped; }

    public int getIterationCount() { return iterationCount; }

    public ComplexNumber getCurrentLocation(){
        return currentLocation;
    }
}
