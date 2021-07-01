import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Snake
 * Author: Peter Mitchell (2021)
 *
 * Game class:
 * Defines the entry point for the Snake game.
 * Manages the game loop and input events passing them to the boardPanel.
 */
public class Game extends JFrame implements KeyListener, ActionListener {
    /**
     * Creates a new game and starts the game.
     *
     * @param args Not used.
     */
    public static void main(String[] args) {
        Game game = new Game();
        game.startGame();
    }

    /**
     * Timer used for the update/render loop.
     * Timer delay is changed to modify difficulty via the boardPanel.
     */
    private Timer gameLoopTimer;
    /**
     * Reference to the boardPanel to pass input to.
     */
    private BoardPanel boardPanel;

    /**
     * Creates a single JFrame with the boardPanel, then
     * configures the game loop timer, sets up the KeyListener
     * for input, and then makes it visible.
     */
    public Game() {
        super("Snake");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        boardPanel = new BoardPanel(20,20, this);
        getContentPane().add(boardPanel);

        gameLoopTimer = new Timer(500,this);
        addKeyListener(this);

        pack();
        setVisible(true);
    }

    /**
     * Starts the game by starting the game loop timer.
     */
    public void startGame() {
        gameLoopTimer.start();
    }

    /**
     * Changes the difficulty of the game by setting the update frequency.
     *
     * @param newDelay Time in ms to set the game loop timer to.
     */
    public void changeTimerFrequency(int newDelay) {
        gameLoopTimer.setDelay(newDelay);
    }

    /**
     * Event triggered every time the gameLoopTimer triggers.
     * Forces the boardPanel to perform an update and will include a repaint().
     *
     * @param e Not used.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        boardPanel.update();
    }

    /**
     * Passes the information about pressed keys to the boardPanel.
     *
     * @param e The information about the key that was pressed.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        boardPanel.processInput(e.getKeyCode());
    }

    /**
     * Not used.
     *
     * @param e Not used.
     */
    @Override
    public void keyReleased(KeyEvent e) {}
    /**
     * Not used.
     *
     * @param e Not used.
     */
    @Override
    public void keyTyped(KeyEvent e) {}
}
