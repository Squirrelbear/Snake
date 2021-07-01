import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Snake
 * Author: Peter Mitchell (2021)
 *
 * BoardPanel class:
 * Defines a panel representing a grid with a snake
 * on it and apples that spawn in groups every time
 * they are cleared.
 */
public class BoardPanel extends JPanel {
    /**
     * Reference to the snake object controlled by the player.
     */
    private Snake snake;
    /**
     * Reference to the game object for passing changes in difficulty.
     */
    private Game game;
    /**
     * Dimensions of each cell on the grid.
     */
    private final int CELL_DIM = 32;
    /**
     * Number of cells on a side in the grid.
     */
    private int width, height;
    /**
     * When set to true, this will prevent doubling back on yourself.
     * This is done by disallowing changes in direction except for to the other axis.
     */
    private boolean preventDoubleBacks;
    /**
     * A unit vector representing the translation for the next move by the snake.
     * Set from player's input using processInput().
     */
    private Position nextTranslation;
    /**
     * A list of positions where apples are located on the board.
     * These positions can be anywhere including under the snake.
     */
    private List<Position> applePositions;
    /**
     * Reference to a shared random object.
     */
    private Random rand;
    /**
     * Font used to render the game over text.
     */
    private Font endGameFont = new Font("Arial", Font.BOLD, 20);
    /**
     * The text to show when a game over is reached.
     */
    private final String endMessage = "Game Over! Press R to Restart.";
    /**
     * The number of apples to spawn at the start, and every time all apples have been collected.
     */
    private final int spawnAppleCount = 3;
    /**
     * The number of segments to start the snake with at the start of a game and when restarting.
     */
    private final int startSnakeLength = 3;
    /**
     * Linear amount of time in ms to speed up by for each apple collected.
     */
    private int difficultyChangeRate = 15;
    /**
     * The start update rate in ms.
     */
    private int minDifficulty = 500;
    /**
     * The minimum to not go below for update rate in ms.
     */
    private int maxDifficulty = 20;
    /**
     * The current difficulty for how much ms to remove from the minDifficulty.
     */
    private int currentDifficulty = 0;

    /**
     * Creates a board with a snake and fresh set of apples ready to be updated with update().
     *
     * @param width Number of cells horizontally.
     * @param height Number of cells vertically.
     * @param game Reference to the Game object.
     */
    public BoardPanel(int width, int height, Game game) {
        this.width = width;
        this.height = height;
        this.game = game;
        setPreferredSize(new Dimension(width*CELL_DIM, height*CELL_DIM));
        setBackground(Color.BLACK);

        rand = new Random();
        snake = new Snake(width, height, CELL_DIM, this);
        applePositions = new ArrayList<>();
        restart();
        preventDoubleBacks = true;
    }

    /**
     * Updates the snake based on the nextTranslation unit vector.
     * Then updates the difficulty using the number of segments, and force a repaint().
     * Timer is only updated with difficulty changes when the difficulty changes from eating more apples.
     */
    public void update() {
        snake.moveSnake(nextTranslation);
        int newDifficulty = Math.max(minDifficulty - snake.getSnakeLength() * difficultyChangeRate, maxDifficulty);
        if(newDifficulty != currentDifficulty) {
            currentDifficulty = newDifficulty;
            game.changeTimerFrequency(newDifficulty);
        }
        repaint();
    }

    /**
     * Process the input of a single key to modify the next state change.
     * Handles WASD and arrow keys to set the unit vector for moving the snake.
     * This can be prevented if preventDoubleBacks is enabled which will force only swaps on a different axis.
     * Very rapid key pressing will still allow you to accidentally double back.
     * Additional key actions are also provided for R to restart at any time.
     * And Escape for quit.
     *
     * @param keyCode The key pressed from a KeyListener event.
     */
    public void processInput(int keyCode) {
        switch(keyCode) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                if(!preventDoubleBacks || nextTranslation.y == 0)
                    nextTranslation.setPosition(0,-1);
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                if(!preventDoubleBacks || nextTranslation.x == 0)
                    nextTranslation.setPosition(1,0);
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                if(!preventDoubleBacks || nextTranslation.y == 0)
                    nextTranslation.setPosition(0,1);
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                if(!preventDoubleBacks || nextTranslation.x == 0)
                    nextTranslation.setPosition(-1,0);
                break;
            case KeyEvent.VK_R:
                restart();
                break;
            case KeyEvent.VK_ESCAPE:
                System.exit(0);
                break;
        }
    }

    /**
     * Attempts to eat an apple at the designated position. Otherwise checks if there are no apples and
     * spawns a new set of apples if there are none.
     *
     * @param newPosition The position currently being moved to by the snake.
     * @return True if an apple was removed at newPosition.
     */
    public boolean eatApple(Position newPosition) {
        if(applePositions.contains(newPosition)) {
            applePositions.remove(newPosition);
            return true;
        }
        if(applePositions.isEmpty()) {
            spawnApples(spawnAppleCount);
        }
        return false;
    }

    /**
     * Spawns count number of apples anywhere on the board.
     * Does not take into account the current cells filled by the snake.
     * They can spawn under the snake.
     *
     * @param count The number of apples to spawn.
     */
    public void spawnApples(int count) {
        applePositions.clear();
        for(int i = 0; i < count; i++) {
            applePositions.add(new Position(rand.nextInt(width), rand.nextInt(height)));
        }
    }

    /**
     * Restarts by creating a random direction unit vector to start the snake.
     * Then resetting the snake with the new direction and startSnakeLength.
     * Then finally spawns apples on the grid.
     */
    public void restart() {
        int r1 = rand.nextInt(2);
        int r2 = (rand.nextInt(2) == 0 ? -1 : 1);
        nextTranslation = new Position(((r1 == 0) ? r2 : 0),((r1 == 1) ? r2 : 0));
        snake.createNewSnake(startSnakeLength, new Position(width/2, height/2), nextTranslation);
        spawnApples(spawnAppleCount);
    }

    /**
     * Draws the apples, snake, and possible game over message to the screen.
     *
     * @param g Reference to the Graphics object for drawing.
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        drawApples(g);
        snake.paint(g);
        if(snake.hasCollided()) {
            drawEndText(g);
        }
    }

    /**
     * Draws all apples stored in applePositions to the grid.
     *
     * @param g Reference to the Graphics object for drawing.
     */
    private void drawApples(Graphics g) {
        g.setColor(Color.GREEN);
        for(Position p : applePositions) {
            g.fillRect(p.x*CELL_DIM,p.y*CELL_DIM, CELL_DIM, CELL_DIM);
        }
    }

    /**
     * Draws a single line of text indicating the game is over.
     *
     * @param g Reference to the Graphics object for drawing.
     */
    private void drawEndText(Graphics g) {
        g.setColor(new Color(128,0,0));
        g.setFont(endGameFont);
        int strWidth = g.getFontMetrics().stringWidth(endMessage);
        g.drawString(endMessage, width*CELL_DIM/2-strWidth/2, height*CELL_DIM/2);
    }
}
