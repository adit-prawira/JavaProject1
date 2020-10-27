package mySolution;

import bagel.AbstractGame;
import bagel.Input;
import bagel.Keys;
import bagel.Window;
import bagel.Image;
import bagel.map.TiledMap;
import bagel.util.Point;
import bagel.util.Vector2;
import java.lang.Math;
import bagel.DrawOptions;

public class ShadowDefend extends AbstractGame {

    //Assume maximum of 1000 polyline points
    private final static int MAX_DATA = 1000;

    // Only 5 slicers max in a Wave.
    private final static int MAX_SLICERS = 5;

    // Declare all bagel attributes variables.
    private final Image[] slicerImage = new Image[MAX_SLICERS];
    private Vector2[] slicerPost = new Vector2[MAX_DATA];
    private DrawOptions[] rotate = new DrawOptions[MAX_SLICERS];
    private final TiledMap map;

    // Declare slicer class.
    private Slicer[] Slicer = new Slicer[MAX_SLICERS];

    /* Initialize normal time delay, speed, and
    minimum timescale
     */
    private final static double SLICER_SPEED = 1.0;
    private final static double NORMAL_TIME_DELAY = 5.0;
    private final static double MIN_TIMESCALE = 1.0;

    //Declare polyline points for each slicer, and status.
    private int[] pointSet = new int[MAX_SLICERS];
    private double[] newX = new double[MAX_SLICERS];
    private double[] newY = new double[MAX_SLICERS];
    private boolean[] SlicerInProgress = new boolean[MAX_SLICERS];
    private int nPolylinePoints = 0;

    // Initialize wave status as not in progress.
    private boolean waveInProgress = false;

    private double slicerSpeed, timeScale = 1;

    // Initialize the assumed FPS of 60 frames/second.
    private final static double FPS = 60;
    private double slicerDistanceInterval;

    // Below is the main function to run the game.
    public static void main(String[] args) {
        new ShadowDefend().run();
    }

    /**
     * Setup the game
     */
    public ShadowDefend(){
        map = new TiledMap("res/levels/1.tmx");

        /* Initialize Slicer's Speed and distance interval between Slicers
         corresponding with the assumed FPS (Frame Rate)
         */
        slicerSpeed = SLICER_SPEED;

        // Spawn Interval of 300 pix
        slicerDistanceInterval = FPS*NORMAL_TIME_DELAY*SLICER_SPEED;

         // Access all polyline points, and store it inside Vector2 class array
        for(Point point : map.getAllPolylines().get(0)){
            slicerPost[nPolylinePoints] = new Vector2(point.x, point.y);
            nPolylinePoints ++;
        }

        /* Declare images, polyline points, rotation and the status attributes
         of a slicer in a wave
         */
        for(int i = 0; i < MAX_SLICERS; i++){
            rotate[i] = new DrawOptions();
            pointSet[i] = 1;
            slicerImage[i] = new Image("res/images/slicer.png");
            SlicerInProgress[i] = true;
        }

    }

    /* Function below is to find optimum Path of the slicer
     * It will choose to go up, down, left or right and
     will update new position of a slicer.
     */
    private void getOptimumPath(double deltaX, double deltaY,
                                double xDest, double yDest,
                                double slicerSpeed, int SlicerN) {

        // Using absolute library for bounding deltaX and deltaY
        deltaX = Math.abs(deltaX);
        deltaY = Math.abs(deltaY);

        /* if deltaX > deltaY, slicer will choose
        to move in horizontal direction
         */
        if (deltaX > deltaY) {
            if (newX[SlicerN] < xDest) {
                newX[SlicerN] += slicerSpeed;
            }
            if (newX[SlicerN] > xDest) {
                newX[SlicerN] -= slicerSpeed;
            }
        }

        /* code below will do the vice
        versa, which is to move vertically
         */
        if (deltaX < deltaY) {
            if (newY[SlicerN] < yDest) {
                newY[SlicerN] += slicerSpeed;
            }
            if (newY[SlicerN] > yDest) {
                newY[SlicerN]-= slicerSpeed;
            }
        }
    }

    /*function below will draw and rotate the slicer's image
     corresponds to the updated position.
     */
    private void DrawAndMoveSlicer(Slicer Slicer, Image slicerImage,
                                   double newX, double newY,
                                   double slicerSpeed, int SlicerN){
        double deltaX = Slicer.getFinalPoint().x - newX;
        double deltaY = Slicer.getFinalPoint().y- newY;
        double absDeltaX = Math.abs(deltaX);
        double absDeltaY = Math.abs(deltaY);

        /* Update Slicer's next move and orientation when
         distance between points is approaching to zero.
         * (int) is used to smooth slicer's transitions between
         points.
         */
        getOptimumPath(deltaX, deltaY, Slicer.getFinalPoint().x,
                Slicer.getFinalPoint().y, slicerSpeed, SlicerN);

        if (((int)absDeltaX >= 0 && (int)absDeltaX < slicerSpeed) &&
                ((int)absDeltaY >= 0 && (int)absDeltaY < slicerSpeed)) {

            //Rotate each slicer independently according to its current path.
            rotate[SlicerN].setRotation(Slicer.getAngleAdjustment());

            /* After finishing a move, a slicer will
            access the next polyline points
             */
            pointSet[SlicerN]++;
        }

        // Draw slicer for each updated information.
        slicerImage.draw(newX, newY, rotate[SlicerN]);
    }

    /**
     * Updates the game state approximately 60 times a second, potentially reading from input.
     * @param input The input instance which provides access to keyboard/mouse state information.
     */

    @Override
    protected void update(Input input) {

        //Draw Map on a window.
        map.draw(0, 0, 0, 0,
                Window.getWidth(), Window.getHeight());

        Window.removeFrameThrottle();

        /* Declare Slicer class and update its initial and
        next destination point
         */
        for (int i = 0; i < MAX_SLICERS; i++) {
            Slicer[i] = new Slicer(slicerPost[pointSet[i] - 1],
                    slicerPost[pointSet[i]]);

            /* The loop will check status of a slicer whether it has
            reach its final destination
             */
            if(pointSet[i] == nPolylinePoints-1){
                SlicerInProgress[i] = false;
            }
        }

        /*When key S is pressed a wave start and won't do anything
        when a wave is in progress
         */
        if (input.isDown((Keys.S)) && !waveInProgress) {
            waveInProgress = true;

            /* Initialize each position of a slicer.
             * Separate distance between slicers corresponds
             with the assumed FPS.
             */
            for (int i = 0; i < MAX_SLICERS; i++) {
                newX[i] = slicerPost[0].x - i*slicerDistanceInterval;;
                newY[i] = slicerPost[0].y;
            }
        }

        //Key L is pressed and increase timescale by 1 increment
        if (input.wasPressed(Keys.L)) {
            timeScale++;
            slicerSpeed *= timeScale;
        }

        /* Key K is pressed and decreased timescale by 1 decrement
        if timescale >= 1.
         */
        if (input.wasPressed(Keys.K)) {
            if (timeScale > MIN_TIMESCALE) {
                timeScale--;
                slicerSpeed /= timeScale;
                /* If time scale is reduce to its original value
                , slicer will be brought back to its normal speed.
                */
                if(timeScale == MIN_TIMESCALE){
                    slicerSpeed = SLICER_SPEED;
                }
            }
        }

        // Starting a wave
        if (waveInProgress) {

            // Draw a slicer progressively when time delay has been met.
            for(int n = 0; n<MAX_SLICERS; n++){
                if(SlicerInProgress[n]){

                    /* Declare angleAdjustment class of the slicer to orientate its position
                    depending on the next path it will take.
                     */
                    Slicer[n] = new angleAdjustment(slicerPost[pointSet[n] - 1],
                            slicerPost[pointSet[n]]);
                    DrawAndMoveSlicer(Slicer[n], slicerImage[n], newX[n],
                            newY[n], slicerSpeed, n);

                }
            }

            // Close Window when all slicers have reach its final destination.
            if(!SlicerInProgress[0] && !SlicerInProgress[1] && !SlicerInProgress[2]
                    && !SlicerInProgress[3]&&!SlicerInProgress[4]){
                Window.close();
            }
        }

    }
}
