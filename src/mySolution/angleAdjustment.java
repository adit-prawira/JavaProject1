package mySolution;

//Child class of Slicer class.
import java.lang.Math;
import bagel.util.Vector2;

public class angleAdjustment extends Slicer{
    private final static double PI = Math.PI;

    //Slicer facing directions
    private final static double FACE_RIGHT = 0.0;
    private final static double FACE_LEFT = PI;
    private final static double FACE_UP = (3*PI)/2.0;
    private final static double FACE_DOWN = PI/2.0;

    public angleAdjustment(Vector2 initialPoint, Vector2 finalPoint){
        super(initialPoint, finalPoint);
    }
    /* Constants below are optimum bounding values found
     through experiments.
     * These values will constrain a slicer to not over-rotate
     itself.
     */
    private double RIGHT_BOUND = 0.01;
    private double RIGHT_BL_BOUND1 = 0.5, RIGHT_BL_BOUND2 =0.6;
    private double RIGHT_MR_BOUND1 = 0.2, RIGHT_MR_BOUND2 = 0.3;
    private double UP_MIN_BOUND = 0.0, UP_MAX_BOUND = 1.0;
    private double LEFT_MIN_BOUND = -1.0, LEFT_MAX_BOUND = 0.0;
    private double LEFT_DIFFERENCE_PERCENTAGE = 0.25;

    /* Variables below will calculate an angle between final
    and initial point
    */
    private double adjX = getFinalPoint().x - getInitialPoint().x;
    private double oppY = getFinalPoint().y - getInitialPoint().y;
    private double xAbs = Math.abs(adjX), yAbs = Math.abs(oppY);
    private double angle =  Math.atan(oppY / adjX);
    private double absAngle = Math.abs(angle);
    private boolean isSpecialAngle = xAbs == yAbs;

    /* Boolean variables which indicates whether a slicer is moving
     Horizontally or vertically.
     * Decides a slicer's orientation depending on its transfer points.
     */
    private boolean isHorizontal = (xAbs > yAbs), isVertical = (xAbs < yAbs);
    private boolean isRight = (adjX > 0) && isHorizontal || (adjX > 0) && isSpecialAngle;
    private boolean isLeft = (adjX < 0) && isHorizontal || (adjX < 0) && isSpecialAngle;
    private boolean isUp = (oppY < 0) && isVertical || (oppY < 0) && isSpecialAngle;
    private boolean isDown = (oppY > 0) && isVertical || (oppY > 0) && isSpecialAngle;

    public double getAngleAdjustment() {
        //Guards below will determine the direction of a slicer's rotation
        if(isRight){
            if(absAngle <= RIGHT_BOUND){
                angle = FACE_RIGHT;
            }
            if(absAngle > RIGHT_BL_BOUND1 && absAngle < RIGHT_BL_BOUND2||
                    absAngle > RIGHT_MR_BOUND1 && absAngle<RIGHT_MR_BOUND2){
                angle = FACE_RIGHT;
            }
        }

        if(isUp){
            if(angle>UP_MIN_BOUND && angle < UP_MAX_BOUND){
                angle = FACE_UP - angle;
            }else{
                angle = FACE_UP;
            }
        }

        if(isLeft){
            if(angle == 0.0){
                angle = FACE_LEFT - angle;
            }else{
                if(angle > LEFT_MIN_BOUND && angle < LEFT_MAX_BOUND){
                    angle = FACE_LEFT - angle;
                    double differencePercentage = (angle-PI)/PI;
                    if(differencePercentage < LEFT_DIFFERENCE_PERCENTAGE){
                        angle = FACE_LEFT;
                    }
                }else{
                    angle = FACE_LEFT;
                }
            }
        }

        if(isDown){
            if(angle > PI){
                angle =  angle - FACE_DOWN;
            }
            if(absAngle > 0.0 && angle < FACE_DOWN){
                angle = FACE_DOWN;
            }
        }
        return angle;
    }
}
