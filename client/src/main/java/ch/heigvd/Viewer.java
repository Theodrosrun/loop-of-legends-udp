package ch.heigvd;

import static java.lang.System.exit;

public class Viewer extends Client {

    private Viewer(String multicastHost, int multicastPort) {
        super(null, 0, multicastHost, multicastPort);
    }

    public static void main(String[] args) {

        // Mutlicast
        String multicastHost = "239.1.1.2";
        int multicastPort = 20000;

        Viewer viewer = new Viewer(multicastHost, multicastPort);
        viewer.startReceiveMulticast();
        while (viewer.inputHandler.getKey() != Key.QUIT) {
            viewer.printBoard();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        while (viewer.inputHandler.getKey() != Key.ENTER) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        viewer.quit();
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
}
