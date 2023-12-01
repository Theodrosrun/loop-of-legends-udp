package ch.heigvd;

import com.googlecode.lanterna.input.InputProvider;
import com.googlecode.lanterna.input.KeyStroke;
import java.io.IOException;
import java.util.concurrent.Semaphore;

/**
 * InputHandler class
 * This class is used to handle the input from the user and to parse it
 * to a KEY enum
 */
public class InputHandler {

    /**
     * The current keyStroke
     */
    private KeyStroke key = null;

    /**
     * The semaphore used to pause the input handler
     */
    private final Semaphore wait = new Semaphore(0);

    /**
     * The pause state
     */
    private boolean pause = false;

    /**
     * The frequency at which the input is read in milliseconds
     */
    private final int READ_FREQUENCY;

    /**
     * The input provider
     */
    private final InputProvider INPUT_PROVIDER;

    /**
     * The stop request state
     */
    boolean stopRequest = false;

    /**
     * Constructor and runner for the InputHandler
     *
     * @param inputProvider the input provider
     * @param readFrequency the frequency at which the input is read in milliseconds
     */
    public InputHandler(InputProvider inputProvider, int readFrequency) {
        READ_FREQUENCY = readFrequency;
        INPUT_PROVIDER = inputProvider;
        Thread thread = new Thread(this::run);
        thread.start();
    }

    /**
     * The run method of the InputHandler
     */
    private void run() {
        while (!stopRequest) {

            if (pause) {
                try {
                    wait.acquire();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            KeyStroke input = null;
            try {
                input = INPUT_PROVIDER.readInput();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (key != input) {
                key = input;
            }
            try {
                Thread.sleep(READ_FREQUENCY);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Get the current keyStroke
     *
     * @return the current keyStroke
     */
    public KeyStroke getKeyStroke() {
        return key;
    }

    /**
     * Get the current key
     *
     * @return the current key
     */
    public KEY getKey() {
        return KEY.parseKeyStroke(key);
    }

    /**
     * Reset the current key
     */
    public void resetKey() {
        key = null;
    }

    /**
     * Pause the input handler
     */
    public void pauseHandler() {
        pause = true;
    }

    /**
     * Restore the input handler
     */
    public void restoreHandler() {
        pause = false;
        wait.release();
    }


    /**
     * Check if the key is a direction
     *
     * @param key the key to check
     * @return true if the key is a direction, false otherwise
     */
    public static boolean isDirection(KeyStroke key) {
        if (key == null) return false;
        return switch (key.getKeyType()) {
            case ArrowUp, ArrowDown, ArrowLeft, ArrowRight -> true;
            default -> false;
        };
    }
}
