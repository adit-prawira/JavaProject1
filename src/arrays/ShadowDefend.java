package arrays;

import bagel.AbstractGame;
import bagel.Input;
import bagel.Keys;
import bagel.Window;
import bagel.map.TiledMap;
import bagel.util.Point;

/**
 * ShadowDefend, a tower defence game.
 */
public class ShadowDefend extends AbstractGame {

    private static final int HEIGHT = 768;
    private static final int WIDTH = 1024;
    private static final String MAP_FILE = "res/levels/1.tmx";
    // Change to suit system specifications. This could be
    // dynamically determined but that is out of scope.
    public static final double FPS = 60;
    // The spawn delay (in seconds) to spawn slicers
    private static final int SPAWN_DELAY = 5;
    private static final int INTIAL_TIMESCALE = 1;
    private static final int MAX_SLICERS = 5;

    // Timescale is made static because it is a universal property of the game and the specification
    // says everything in the game is affected by this
    private static int timescale = INTIAL_TIMESCALE;
    private final TiledMap map;
    private final Point[] polyline;
    private final Slicer[] slicers;
    private double frameCount;
    private int spawnedSlicers;
    private boolean waveStarted;
    private int slicersFinished;

    /**
     * Creates a new instance of the ShadowDefend game
     */
    public ShadowDefend() {
        super(WIDTH, HEIGHT, "ShadowDefend");
        this.map = new TiledMap(MAP_FILE);
        int polylinePointCount = map.getAllPolylines().get(0).size();
        // Copy the polyline into an array for later use
        this.polyline = new Point[polylinePointCount];
        this.slicersFinished = 0;
        // Gather polyline information from the map and put it into the array
        int i = 0;
        for (Point point : map.getAllPolylines().get(0)) {
            polyline[i++] = point;
        }
        this.slicers = new Slicer[MAX_SLICERS];
        this.spawnedSlicers = 0;
        this.waveStarted = false;
        this.frameCount = Integer.MAX_VALUE;
        // Temporary fix for the weird slicer map glitch (might have to do with caching textures)
        // This fix is entirely optional
        new Slicer(polyline);
    }

    /**
     * The entry-point for the game
     *
     * @param args Optional command-line arguments
     */
    public static void main(String[] args) {
        new ShadowDefend().run();
    }

    public static int getTimescale() {
        return timescale;
    }

    /**
     * Increases the timescale
     */
    private void increaseTimescale() {
        timescale++;
    }

    /**
     * Decreases the timescale but doesn't go below the base timescale
     */
    private void decreaseTimescale() {
        if (timescale > INTIAL_TIMESCALE) {
            timescale--;
        }
    }

    /**
     * Update the state of the game, potentially reading from input
     *
     * @param input The current mouse/keyboard state
     */
    @Override
    protected void update(Input input) {
        // Increase the frame counter by the current timescale
        frameCount += getTimescale();

        // Draw map from the top left of the window
        map.draw(0, 0, 0, 0, WIDTH, HEIGHT);

        // Handle key presses
        if (input.wasPressed(Keys.S)) {
            waveStarted = true;
        }

        if (input.wasPressed(Keys.L)) {
            increaseTimescale();
        }

        if (input.wasPressed(Keys.K)) {
            decreaseTimescale();
        }

        // Check if it is time to spawn a new slicer (and we have some left to spawn)
        if (waveStarted && frameCount / FPS >= SPAWN_DELAY && spawnedSlicers != MAX_SLICERS) {
            slicers[spawnedSlicers] = new Slicer(polyline);
            spawnedSlicers += 1;
            // Reset frame counter
            frameCount = 0;
        }

        // Check for slicers that have finished
        for (int i = 0; i < slicers.length; i++) {
            if (slicers[i] != null && slicers[i].isFinished()) {
                slicersFinished++;
                slicers[i] = null;
            }
        }

        // Close game if all slicers have finished traversing the polyline
        if (slicersFinished == MAX_SLICERS) {
            Window.close();
        }

        // Update all sprites
        for (Slicer s : slicers) {
            if (s != null) {
                s.update(input);
            }
        }
    }
}
