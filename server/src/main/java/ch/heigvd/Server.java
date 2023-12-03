package ch.heigvd;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Server {
    private static final int NB_PLAYER = 4;
    private MulticastSocket multicastSocketEmitter;

    private ScheduledExecutorService scheduler;

    private Server(int port, String host) {
        this.scheduler = Executors.newScheduledThreadPool(NB_PLAYER);
        try {
            multicastSocketEmitter = new MulticastSocket(port);
            // Convertit le nom d'hôte spécifié dans la variable host en une adresse IP
            InetAddress multicastAddress = InetAddress.getByName(host);
            // Représente le groupe multicast auquel le socket se joindra pour envoyer des messages.
            InetSocketAddress group = new InetSocketAddress(multicastAddress, port);
            // Récupère une interface réseau spécifique à l'aide de son nom (fourni par interfaceName).
            // Ceci est important pour déterminer par quelle interface réseau les données multicast seront envoyées.
            NetworkInterface networkInterface = NetworkInterfaceHelper.getFirstNetworkInterfaceAvailable();
            // Fait rejoindre au socket multicast le groupe multicast spécifié sur l'interface réseau choisie.
            // Cela permet au socket d'envoyer des paquets de données au groupe multicast sur l'interface réseau spécifiée.
            multicastSocketEmitter.joinGroup(group, networkInterface);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void start() {
        while (true) {
            new Thread(new ServerWorker()).start();
        }
    }

    public static void main(String[] args) {
        String host = "239.1.1.1";
        int port = 20000;

        (new Server(port, host)).start();
    }
}