package ch.heigvd;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.UUID;

/**
 * Enumeration of the different messages that can be sent by the server
 * or the client.
 */
public enum Message {
    INIT("INIT"),
    DONE("DONE"),
    LOBB("LOBB"),
    JOIN("JOIN"),
    RADY("RADY"),
    REPT("REPT"),
    EROR("EROR"),
    DIRE("DIRE"),
    UPTE("UPTE"),
    QUIT("QUIT"),
    UNKN("UNKN");

    /**
     * The separator used to separate the message from the data
     */
    final static String SEPARATOR = " ";

    /**
     * The end of transmission character
     */
    final static char EOT = 0x04;

    /**
     * The message
     */
    private final String message;

    /**
     * Constructor
     * @param message the message
     */
    Message(String message) {
        this.message = message;
    }

    /**
     * Get the message from a string
     * @param message the string
     * @return the message
     */
    public static Message fromString(String message) {
        for (Message m : Message.values()) {
            if (m.toString().equalsIgnoreCase(message)) {
                return m;
            }
        }
        return UNKN;
    }

    /**
     * Set the command to send to the server
     * @param message the message
     * @param data the data
     * @return the command
     */
    public static String setCommand(UUID uuid, Message message, String data){
        if (data == null) {
            return uuid + SEPARATOR + message.toString() + EOT;
        } else {
            return uuid + SEPARATOR + message.toString() + SEPARATOR + data + EOT;
        }
    }

    /**
     * Set the command to send to the server
     * @param message the message
     * @return the command
     */
    public static String setCommand(UUID uuid, Message message) {
        return setCommand(uuid, message, null);
    }

    /**
     * Get the response from the server
     * @param input the reader
     * @return the response
     * @throws IOException if an I/O error occurs
     */
    public static String getResponse(String input) {
        StringBuilder response = new StringBuilder();
        int c;

        for (int i = 0; i < input.length(); i++) {
            c = input.charAt(i);

            if (c == EOT) {
                break;
            }
            response.append((char) c);
        }

        return response.toString();
    }

    /**
     * Get the UUID from a string
     * @param string the string
     * @return the UUID
     */
    public static UUID getUUID(String string) {
        return UUID.fromString(string.split(SEPARATOR, 3)[0]);
    }

    /**
     * Get the message from a string
     * @param string the string
     * @return the message
     */
    public static String getMessage(String string) {
        return string.split(SEPARATOR, 3)[1];
    }

    /**
     * Get the data from a string
     * @param string the string
     * @return the data
     */
    public static String getData(String string) {
        String[] tab = string.split(SEPARATOR, 3);
        return tab.length > 2 ? tab[2] : "";
    }

    @Override
    public String toString() {
        return message;
    }
}