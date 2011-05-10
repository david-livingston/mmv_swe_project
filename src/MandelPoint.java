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
 *
 * Requirement 1.0.0 The program should display a basic fractal image
 * Each instance of this class represents one sampled point of the Mandelbrot set,
 * i.e. one pixel in the logical image.
 */
public class MandelPoint implements Serializable {

    private final ComplexNumber startingLocation;
    private ComplexNumber currentLocation;
    private int iterationCount = 0;
    private final double squaredMaxDistance = 4.0;
    private boolean escaped = false;

    /**
     * Construct a MandelPoint which starts at (before iteration) the input
     * ComplexPoint. The MandelPoint will be iterated through
     * z <- z^2 + c, where z and c are both initially set to the starting
     * location, c is held constant, and z is updated.
     *
     * @param startingLocation
     */
    public MandelPoint(final ComplexNumber startingLocation){
        this.startingLocation = currentLocation = startingLocation;
    }

    /**
     * This is the key method in the program it labels a complex point
     * as either a prisoner or escapee of the mandelbrot set if escapee
     * it also indicates how long it took for the point to escape see wikipedia
     * article for details: http://en.wikipedia.org/wiki/Mandelbrot_set#Escape_time_algorithm
     *
     * @param iterationLimit
     */
    public void iterate(final int iterationLimit){
        if(escaped)
            return;

        final double c_r = startingLocation.getReal();
        final double c_i = startingLocation.getImag();
        double r = currentLocation.getReal();
        double i = currentLocation.getImag();
        double new_r;
        double r_sq;
        double i_sq;

        while(iterationCount++ < iterationLimit){
            r_sq = r * r;
            i_sq = i * i;

            if(r_sq + i_sq > squaredMaxDistance){
                escaped = true;
                break;
            }

            new_r = r_sq - i_sq + c_r;
            i = 2 * r * i + c_i;
            r = new_r;
        }

        currentLocation = new ComplexNumber(r, i);
    }

    public boolean didEscape() { return escaped; }

    public int getIterationCount() { return iterationCount; }

    public ComplexNumber getCurrentLocation(){
        return currentLocation;
    }
}
