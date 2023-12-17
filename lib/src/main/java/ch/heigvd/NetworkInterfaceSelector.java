package ch.heigvd;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class NetworkInterfaceSelector {

    private List<NetworkInterface> networkInterfaces;
    private int selectedIndex = 0;

    public NetworkInterfaceSelector() throws SocketException {
        networkInterfaces = getNetworkInterfaces();
    }

    private List<NetworkInterface> getNetworkInterfaces() throws SocketException {
        List<NetworkInterface> interfaces = new ArrayList<>();
        Enumeration<NetworkInterface> allInterfaces = NetworkInterface.getNetworkInterfaces();
        for (NetworkInterface netInterface : Collections.list(allInterfaces)) {
            if (netInterface.isUp() && !netInterface.isLoopback()) {
                interfaces.add(netInterface);
            }
        }
        return interfaces;
    }

    public NetworkInterface selectNetworkInterface() throws IOException {
        Terminal terminal = new DefaultTerminalFactory().createTerminal();
        Screen screen = new TerminalScreen(terminal);
        screen.setCursorPosition(null);
        screen.startScreen();
        screen.clear();

        TerminalSize size = screen.getTerminalSize();
        TextGraphics textGraphics = screen.newTextGraphics();

        String title = "Please, select your interface";
        textGraphics.putString(2, 1, title, SGR.BOLD);

        while (true) {
            for (int i = 0; i < networkInterfaces.size(); i++) {
                NetworkInterface netInterface = networkInterfaces.get(i);
                String interfaceName = netInterface.getDisplayName();
                if (i == selectedIndex) {
                    textGraphics.setBackgroundColor(TextColor.ANSI.GREEN);
                } else {
                    textGraphics.setBackgroundColor(TextColor.ANSI.DEFAULT);
                }
                textGraphics.putString(2, i + 2, interfaceName);
            }
            screen.refresh();

            KeyStroke keyStroke = screen.readInput();
            if (keyStroke.getKeyType() == KeyType.ArrowUp) {
                selectedIndex = (selectedIndex - 1 + networkInterfaces.size()) % networkInterfaces.size();
            } else if (keyStroke.getKeyType() == KeyType.ArrowDown) {
                selectedIndex = (selectedIndex + 1) % networkInterfaces.size();
            } else if (keyStroke.getKeyType() == KeyType.Enter) {
                screen.stopScreen();
                return networkInterfaces.get(selectedIndex);
            }
        }
    }
}

