import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Snake
 * Author: Peter Mitchell (2021)
 *
 * Snake class:
 * Defines a snake that can have a variable length, and
 * constrains itself with rules about staying in the area,
 * not colliding with itself, and managing eating apples.
 */
public class Snake {
    /**
     * All the positions on the board where a snake segment is. Will not contain duplicates.
     */
    private List<Position> segments;
    /**
     * Width of the grid horizontally.
     */
    private int gameWidth;
    /**
     * Height of the grid vertically.
     */
    private int gameHeight;
    /**
     * Dimensions of each square on the grid.
     */
    private int cellDimension;
    /**
     * Reference to the boardPanel to get information about apple positions.
     */
    private BoardPanel boardPanel;
    /**
     * True when the Snake has collided with the edge of the grid, or with itself.
     * Indicates the game is over.
     */
    private boolean hasCollided;
    /**
     * The position where the collision occurred so it can be shown visually with a red flash.
     */
    private Position collisionPosition;
    /**
     * Toggle for the visual update of red flashing at collisionPosition.
     */
    private boolean flashCollided;

    /**
     * Initialises the empty state of a Snake object. Call createNewSnake() to place it on the grid.
     *
     * @param gameWidth Width of the grid horizontally.
     * @param gameHeight Height of the grid vertically.
     * @param cellDimension Reference to the boardPanel to get information about apple positions.
     * @param boardPanel Reference to the boardPanel to get information about apple positions.
     */
    public Snake(int gameWidth, int gameHeight, int cellDimension, BoardPanel boardPanel) {
        this.boardPanel = boardPanel;
        this.gameHeight = gameHeight;
        this.gameWidth = gameWidth;
        this.cellDimension = cellDimension;
        segments = new ArrayList<>();
        flashCollided = true;
    }

    /**
     * Moves the snake by one unit vector. Does nothing if the snake has already collided.
     * Checks for collisions with the snake and for invalid points off the board.
     * If the new position being moved to is valid it will check for eating apples at that position.
     *
     * @param nextTranslation The unit vector to apply for movement.
     */
    public void moveSnake(Position nextTranslation) {
        // Do not move if the snake has already been halted from a collision
        if(hasCollided) return;

        // Get the previous movement, and get the new position based on the last position added to the unit vector.
        Position lastPos = segments.get(segments.size()-1);
        Position newPos = new Position(lastPos.x + nextTranslation.x, lastPos.y + nextTranslation.y);

        // Check for collisions and prevent movement if a collision occurs
        if(segments.contains(newPos)) {
            collisionPosition = newPos;
            hasCollided = true;
        } else if(newPos.x < 0 || newPos.y <  0 || newPos.x >= gameWidth || newPos.y >= gameHeight) {
            collisionPosition = lastPos;
            hasCollided = true;
        } else {
            // Valid movement, will move and check for eating an apple then remove the end of the snake.
            segments.add(newPos);
            if (!boardPanel.eatApple(newPos))
                segments.remove(0);
        }
    }

    /**
     * Draws all segments onto the grid that are filled by the snake.
     * If the snake has collided somewhere a flashing red cell is overlaid at that location.
     *
     * @param g Reference to the Graphics object for drawing.
     */
    public void paint(Graphics g) {
        g.setColor(Color.WHITE);
        for(int i = 0; i < segments.size(); i++) {
            g.fillRect(segments.get(i).x*cellDimension+1, segments.get(i).y*cellDimension+1,
                    cellDimension-2, cellDimension-2);
        }

        if(hasCollided) {
            flashCollided = !flashCollided;
            if(flashCollided) {
                g.setColor(Color.RED);
                g.fillRect(collisionPosition.x * cellDimension + 1, collisionPosition.y * cellDimension + 1,
                        cellDimension - 2, cellDimension - 2);
            }
        }
    }

    /**
     * Initialise the snake to a position with a starting number of segments.
     * The segments are filled out using the startDirection unit vector.
     *
     * @param startLength Number of segments to start the snake with.
     * @param startPos Position to start the snake tail at.
     * @param startDirection Initial direction of motion as a unit vector.
     */
    public void createNewSnake(int startLength, Position startPos, Position startDirection){
        segments.clear();
        for(int i = 0; i < startLength; i++) {
            segments.add(new Position(startPos.x + startDirection.x * i, startPos.y + startDirection.y * i));
        }
        hasCollided = false;
    }

    /**
     * Gets the number of segments that are part of the snake.
     *
     * @return Number of segments in the snake.
     */
    public int getSnakeLength() {
        return segments.size();
    }

    /**
     * Gets the current collision state of the snake. This will be true when the snake has
     * either collided with itself or has collided with the edge of the grid.
     *
     * @return True when the snake has collided with something and is paused for game over.
     */
    public boolean hasCollided() {
        return hasCollided;
    }
}
