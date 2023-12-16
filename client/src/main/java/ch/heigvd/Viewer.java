package ch.heigvd;

import static java.lang.System.exit;

public class Viewer extends Client {

    private Viewer(String multicastStreamHost, int multicastStreamPort) {
        super(null, 0, multicastStreamHost, multicastStreamPort);
    }

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
