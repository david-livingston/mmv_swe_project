import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by IntelliJ IDEA.
 * User: David
 * Date: Feb 13, 2011
 * Time: 7:44:16 PM
 * To change this template use File | Settings | File Templates.
 */

/*
 * Class is a 2d array of MandelPoints. This class represents the picture to be drawn on
 * the screen and can zoom in on a new area of the picture.
 *
 * Most of the associated GUI code for this picture is in MandelJPanel.java.
 */
public class MandelCanvas {

    public double realMinimum = -2.5;
    public double imaginaryMaximum = 1.25;
    public double realMaximum = 1.0;
    public double imaginaryMinimum;
    public double delta;

    public final int countOfXPixels;
    public final int countOfYPixels;

    private final MandelPoint[][] mandelPoints;

    private int iterationMax = 100;

    public MandelCanvas(int xRes, int yRes){
        delta = (realMaximum - realMinimum)/xRes;
        imaginaryMinimum = imaginaryMaximum - yRes * delta;
        countOfXPixels = xRes;
        countOfYPixels = yRes;
        mandelPoints = new MandelPoint[xRes][yRes];

        for(int x = 0; x < xRes; ++x)
            for(int y = 0; y < yRes; ++y)
                mandelPoints[x][y] = new MandelPoint(realMinimum + x * delta, imaginaryMaximum - y * delta);
    }

    // locate the point, make sure it has been calculated, look up color in palette
    public Color getColorAtPoint(int x, int y){
        MandelPoint m = mandelPoints[x][y];
        m.iterate(iterationMax, 2.0);
        return Palette.getColor(m);
    }

    public void doZoom(Point upperLeftCorner, Point lowerRightCorner){
        // swap the click points if user clicked lower right corner before upper left corner
        if(upperLeftCorner.getX() > lowerRightCorner.getX() || upperLeftCorner.getY() > lowerRightCorner.getY()){
            Point tmp = lowerRightCorner;
            lowerRightCorner = upperLeftCorner;
            upperLeftCorner = tmp;
        }
        // translate first click into a complex number
        realMinimum = realMinimum + upperLeftCorner.getX() * delta;
        imaginaryMaximum = imaginaryMaximum - upperLeftCorner.getY() * delta;
        // translate second click into a complex number
        realMaximum = realMinimum + lowerRightCorner.getX() * delta;
        imaginaryMinimum = imaginaryMaximum - lowerRightCorner.getY() * delta;
        // delta - the complex distance between pixels - must be recalculated because we're zooming in
        // having real & imaginary axis share a delta keeps the aspect ratio correct
        delta = ((realMaximum - realMinimum)/countOfXPixels + (imaginaryMaximum - imaginaryMinimum)/countOfYPixels)/2.0;
        System.out.println("delta: " + delta);
        // update the array of complex points based on the new corners & new delta
        for(int x = 0; x < countOfXPixels; ++x)
            for(int y = 0; y < countOfYPixels; ++y)
               mandelPoints[x][y] = new MandelPoint(realMinimum + x * delta, imaginaryMaximum - y * delta);
    }

    public BufferedImage getAsBufferedImage(){
        BufferedImage img = new BufferedImage(countOfXPixels, countOfYPixels, BufferedImage.TYPE_INT_RGB);
        for(int x = 0; x < countOfXPixels; ++x)
            for(int y = 0; y < countOfYPixels; ++y)
                img.setRGB(x, y, getColorAtPoint(x, y).getRGB());
        return img;
    }

    public int getIterationMax() { return iterationMax; }

    public void increaseIterationMax(int increase){ iterationMax += increase; }

}
