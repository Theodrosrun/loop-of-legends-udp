package ch.heigvd;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import static java.lang.System.exit;

/**
 * This class is used to close the connection properly
 */
public class Exit implements Runnable {

    /**
     * The socket used to communicate with the server
     */
    private final Socket socket;
    /**
     * The output streams used to communicate with the server
     */
    private final BufferedWriter output;

    /**
     * The input stream used to communicate with the server
     */
    private final BufferedReader input;

    private ArrayList<Thread> pool;

    /**
     * The constructor of the class
     * @param socket the socket used to communicate with the server/client
     * @param output the output stream used to communicate with the server/client
     * @param input the input stream used to communicate with the server/client
     */
    public Exit(Socket socket, BufferedWriter output, BufferedReader input) {
        this.output = output;
        this.input = input;
        this.socket = socket;
    }

    public Exit(Socket socket, BufferedWriter output, BufferedReader input, ArrayList<Thread> pool) {
        this(socket, output, input);
        this.pool = pool;
    }

    /**
     * The run method of the thread
     */
    @Override
    public void run() {
        if (socket != null) {
            closeConnection();
        }
    }

    /**
     * This method is used to close the connection properly
     */
    private void closeConnection() {
        try {
            if (pool != null) {
                for (Thread thread : pool) {
                    thread.interrupt();
                }

                for (Thread thread : pool) {
                    thread.join();
                }
            }

            output.write(Message.setCommand(Message.QUIT));
            output.flush();
            output.close();
            input.close();
            socket.close();
        } catch (IOException e) {
            exit(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
