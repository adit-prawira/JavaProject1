//Slicer class is a parent class.
package mySolution;
import bagel.util.Vector2;

public class Slicer{
    private Vector2 initialPoint, finalPoint;

    /* initial point : (x_n-1, y_n-1).
     * final point : (x_n, y_n).
     * n = number of polyline points.
     */
    public Slicer(Vector2 initialPoint, Vector2 finalPoint){
        this.initialPoint = initialPoint;
        this.finalPoint = finalPoint;
    }

    public Vector2 getInitialPoint(){return initialPoint;}
    public void setInitialPoint(Vector2 initialPoint){this.initialPoint = initialPoint;}

    public Vector2 getFinalPoint(){return finalPoint;}
    public void setFinalPoint(Vector2 finalPoint){this.finalPoint = finalPoint;}

    //Access angle adjustment from child class.
    public double getAngleAdjustment(){
        return 0.0;
    }

}