/**
 * Created by IntelliJ IDEA.
 * User: David
 * Date: Feb 13, 2011
 * Time: 7:25:30 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * This class represents a complex point that is being iterated through the Mandelbrot
 * process. Initially the constant (starting) location and current location are the same.
 * The current location changes once per each iteration.
 */
public class MandelPoint {

    public final ComplexNumber startingLocation;
    public ComplexNumber currentLocation;
    public int iterationCount = 0;

    // todo: escpaed is a predicate method, base on magnitude not iterations, iterations must be flexible if this is to be resumed when iterationMax increases
    public boolean escaped = false;

    public MandelPoint(double real, double imaginary){
        startingLocation = new ComplexNumber(real, imaginary);
        currentLocation = startingLocation;
    }

    // this is the key method in the program
    // it labels a complex point as either a prisoner or escapee of the mandelbrot set
    // if escapee it also indicates how long it took for the point to escape
    // see wikipedia article for details: http://en.wikipedia.org/wiki/Mandelbrot_set#Escape_time_algorithm
    public void iterate(int iterationLimit, double escape){
        if(escaped) return;

        final double sqrEscape = escape * escape;

        while(iterationCount++ < iterationLimit){
            currentLocation = ComplexNumber.add(startingLocation, ComplexNumber.square(currentLocation));
            if(currentLocation.sqrMagnitude() > sqrEscape){
                escaped = true;
                break;
            }
        }
    }
}
