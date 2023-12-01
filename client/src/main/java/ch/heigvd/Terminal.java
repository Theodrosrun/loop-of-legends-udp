package ch.heigvd;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.TextInputDialogBuilder;
import com.googlecode.lanterna.input.InputProvider;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.TerminalResizeListener;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.dialogs.TextInputDialog;


import javax.swing.*;
import java.io.IOException;
import java.util.regex.Pattern;
/**
 * This class represents a terminal. It is used to display messages to the user
 * and to get input from the user.
 */
public class Terminal implements InputProvider {

    /**
     * The terminal
     */
    private com.googlecode.lanterna.terminal.Terminal terminal;
    /**
     * The screen
     */
    private Screen screen = null;
    /**
     * The text graphics used to display messages
     */
    private TextGraphics text;

    /**
     * Constructor
     */
    public Terminal() {
        try {
            terminal = new DefaultTerminalFactory().createTerminal();
            screen = new TerminalScreen(terminal);
            screen.startScreen();
            terminal.addResizeListener(new TerminalResizeListener() {
                @Override
                public void onResized(com.googlecode.lanterna.terminal.Terminal terminal, TerminalSize terminalSize) {
                    screen.doResizeIfNecessary();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        text = screen.newTextGraphics();
    }

    /**
     * Get the user input
     * @return the user input
     */
    public String userInput() {
        WindowBasedTextGUI textGUI = new MultiWindowTextGUI(screen);
        String input = "\0";
        input = new TextInputDialogBuilder()
                .setTitle("UserName")
                .setDescription("Enter your UserName please")
                .build()
                .showDialog(textGUI);
        return input;
    }


    /**
     * Print a message to the terminal
     * @param s the message to print
     */
    public void print(String s) {
        try {
            screen.setCursorPosition(null);
            String[] lines = s.split("\n");
            int terminalHeight = terminal.getTerminalSize().getRows();
            int terminalWidth = terminal.getTerminalSize().getColumns();

            while (lines.length + 2 > terminalHeight || lines[0].length() + 2 > terminalWidth) {
                text.putString(0,0,"Terminal too small, please resize\n");
                text.putString(0,1,"Current size: " + terminalWidth + "x" + terminalHeight + "\n");
                text.putString(0,2,"Required size: " + lines[0].length() + "x" + lines.length + "\n");
                terminalHeight = terminal.getTerminalSize().getRows();
                terminalWidth = terminal.getTerminalSize().getColumns();
                screen.refresh();
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }


            for (int i = 0; i < lines.length; i++) {
                text.putString(0, i, lines[i]);
            }
            screen.refresh();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Unused method but required by the interface
     */
    @Override
    public KeyStroke pollInput() throws IOException {
        return null;
    }

    /**
     * Read the user input
     * @return the user input
     * @throws IOException if an I/O error occurs
     */
    @Override
    public KeyStroke readInput() throws IOException {
        return terminal.readInput();
    }

    /**
     * Clear the terminal
     */
    public void clear() {
        screen.clear();
    }
}
