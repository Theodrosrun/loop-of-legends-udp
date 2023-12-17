package ch.heigvd;

import static java.lang.System.exit;

public class Viewer extends Client {

    /**
     * Constructor for the Viewer.
     *
     * @param multicastStreamHost Host address for multicast streaming.
     * @param multicastStreamPort Port for multicast streaming.
     */
    private Viewer(String multicastStreamHost, int multicastStreamPort) {
        super(null, 0, multicastStreamHost, multicastStreamPort);
    }

    /**
     * Watches the game, displaying the board and waiting for the quit command.
     */
    private void watchGame(){
        while (inputHandler.getKey() != Key.QUIT) {
            printBoard();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        while (inputHandler.getKey() != Key.ENTER) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        quit();
    }

    /**
     * Overrides the quit method to clear the terminal and wait for the enter key to exit.
     */
    @Override
    protected void quit() {
        terminal.clear();
        terminal.print("Press enter to exit\n");

        while (inputHandler.getKey() != Key.ENTER) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        exit(0);
    }

    public static void main(String[] args) {
        // Mutlicast stream
        String multicastStreamHost = "239.1.1.2";
        int multicastSteamPort = 20001;

        Viewer viewer = new Viewer(multicastStreamHost, multicastSteamPort);
        viewer.startReceiveMulticast();
        viewer.watchGame();
    }
}
