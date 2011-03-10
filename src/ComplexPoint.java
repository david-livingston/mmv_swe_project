/**
 * Created by IntelliJ IDEA.
 * User: David
 * Date: Feb 13, 2011
 * Time: 7:13:43 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * This class just contains the methods to do simple math on complex numbers.
 */
public class ComplexPoint {

    public final double real;
    public final double complex;

    public ComplexPoint(double real, double complex) {
        this.real = real;
        this.complex = complex;
    }

    public static ComplexPoint add(ComplexPoint lhs, ComplexPoint rhs){
        return new ComplexPoint(
            lhs.real + rhs.real,
            lhs.complex + rhs.complex
        );
    }

    public static ComplexPoint multiply(ComplexPoint lhs, ComplexPoint rhs){
        return new ComplexPoint(
            lhs.real * rhs.real - rhs.complex * lhs.complex,
            rhs.real * lhs.complex + lhs.real * rhs.complex
        );
    }

    public static ComplexPoint square(ComplexPoint c){
        return multiply(c, c);
    }

    public double magnitude(){
        return Math.sqrt(real * real + complex * complex);
    }

    public double sqrMagnitude(){
        return real * real + complex * complex;
    }
}
