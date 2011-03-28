/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: 3/28/11
 * Time: 2:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class ComplexRectangle {

    private final ComplexNumber upperLeft;
    private final ComplexNumber lowerRight;

    public ComplexRectangle(ComplexNumber upperLeft, ComplexNumber lowerRight) {
        this.upperLeft = upperLeft;
        this.lowerRight = lowerRight;
    }

    public ComplexNumber getUpperLeft() {
        return upperLeft;
    }

    public ComplexNumber getLowerRight() {
        return lowerRight;
    }

    @Override
    public String toString() {
        return "ComplexRectangle{" +
                "upperLeft=" + upperLeft +
                ", lowerRight=" + lowerRight +
                '}';
    }
}
