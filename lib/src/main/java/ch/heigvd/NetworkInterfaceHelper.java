package ch.heigvd;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class NetworkInterfaceHelper {
    public static NetworkInterface getFirstNetworkInterfaceAvailable() throws SocketException {
        Enumeration<NetworkInterface> interfacesReseau = NetworkInterface.getNetworkInterfaces();

        while (interfacesReseau.hasMoreElements()) {
            NetworkInterface interfaceReseau = interfacesReseau.nextElement();
            if (interfaceReseau.isUp() && !interfaceReseau.isLoopback()) {
                return interfaceReseau;
            }
        }
        return null; // TODO - Handle null case
    }
}
