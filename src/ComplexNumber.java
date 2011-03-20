/**
 * Created by IntelliJ IDEA.
 * User: David
 * Date: Feb 13, 2011
 * Time: 7:13:43 PM
 * To change this template use File | Settings | File Templates.
 */

import java.io.Serializable;

/**
 * This class represents an immutable complex point (the sum of a real number
 * and the product of another real number and the square root of -1, i.e.
 * (a + bi).
 *
 * This class only implements the methods necessary for generating the
 * Mandelbrot set. Using objects and method calls for the numbers in the
 * Mandelbrot calculation may make the code clearer but I'm not sure about
 * the overhead; performance should probably be tested to see how much of this
 * the compiler optimizes away.
 */
public class ComplexNumber implements Serializable {

    private final double real;
    private final double imaginaryCoefficient;

    /**
     * Constructs an immutable complex number.
     * @param real
     * @param imaginaryCoefficient
     */
    public ComplexNumber(double real, double imaginaryCoefficient) {
        this.real = real;
        this.imaginaryCoefficient = imaginaryCoefficient;
    }

    /**
     * @param lhs a complex number to be added
     * @param rhs another complex number to be added
     * @return the sum of the two arguments
     */
    public static ComplexNumber add(ComplexNumber lhs, ComplexNumber rhs){
        return new ComplexNumber(
            lhs.real + rhs.real,
            lhs.imaginaryCoefficient + rhs.imaginaryCoefficient
        );
    }

    /**
     * @param lhs a complex number to be multiplied
     * @param rhs another complex number to be multiplied
     * @return the product of the two arguments
     */
    private static ComplexNumber multiply(ComplexNumber lhs, ComplexNumber rhs){
        return new ComplexNumber(
            lhs.real * rhs.real - rhs.imaginaryCoefficient * lhs.imaginaryCoefficient,
            rhs.real * lhs.imaginaryCoefficient + lhs.real * rhs.imaginaryCoefficient
        );
    }

    /**
     * @param c a complex number to be multiplied by itself
     * @return the square of the argument
     */
    public static ComplexNumber square(ComplexNumber c){
        return multiply(c, c);
    }

    /**
     * @return the linear distance from this point to the origin (0 + 0i)
     */
    public double magnitude(){
        return Math.sqrt(real * real + imaginaryCoefficient * imaginaryCoefficient);
    }

    /**
     * Used to quickly test whether a point is farther from the origin than a
     * squared distance; useful for testing a point against the bailout
     * distance in the mandelbrot iteration.
     *
     * Example: to test whether a point is more than 2.0 away from the origin, test
     * c.sqrMagnitude > 4.0.
     *
     * @return same as magnitude() but before taking square root
     */
    public double sqrMagnitude(){
        return real * real + imaginaryCoefficient * imaginaryCoefficient;
    }
}
