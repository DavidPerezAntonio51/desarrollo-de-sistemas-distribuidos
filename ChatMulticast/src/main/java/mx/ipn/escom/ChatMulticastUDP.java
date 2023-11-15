package mx.ipn.escom;

/**
 * Hello world!
 */

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class ChatMulticastUDP {

    private static final String MULTICAST_GROUP_IP = "239.0.0.0";
    private static final int PORT = 50000;
    private static final int MAX_PACKET_SIZE = 255; // Tamaño máximo del mensaje

    private String username;
    private InetAddress multicastGroup;
    private MulticastSocket multicastSocket;

    public ChatMulticastUDP(String username) throws IOException {
        this.username = username;
        this.multicastGroup = InetAddress.getByName(MULTICAST_GROUP_IP);
        this.multicastSocket = new MulticastSocket(PORT);
        InetSocketAddress grupo = new InetSocketAddress(Inet4Address.getByName(MULTICAST_GROUP_IP),PORT);
        NetworkInterface networkInterface = NetworkInterface.getByName("Ethernet");
        this.multicastSocket.joinGroup(grupo,networkInterface);
    }

    private void startReceivingMessages() {
        Thread thread = new Thread(() -> {
            try {
                while (true) {
                    byte[] buffer = new byte[MAX_PACKET_SIZE];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    this.multicastSocket.receive(packet);
                    String receivedMessage = new String(packet.getData(), "CP850");
                    String senderUsername = receivedMessage.substring(0, receivedMessage.indexOf("--->"));
                    String message = receivedMessage.substring(senderUsername.length() + 4);
                    System.out.println("\n" + senderUsername + ": " + message);
                    System.out.print("Escribe tu mensaje: ");
                }
            } catch (IOException ex) {
                System.err.println("Error al recibir mensaje multicast: " + ex.getMessage());
            }
        });
        thread.start();
    }

    public void startSendingMessages() {
        try {
            DatagramPacket packet;
            String message;
            byte[] buffer;
            System.out.print("Escribe tu mensaje: ");
            while (true) {
                message = this.username + "--->" + readLine();
                buffer = message.getBytes();
                packet = new DatagramPacket(buffer, buffer.length, this.multicastGroup, PORT);
                this.multicastSocket.send(packet);
            }
        } catch (IOException ex) {
            System.err.println("Error al enviar mensaje multicast: " + ex.getMessage());
        }
    }

    private static String readLine() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Debe proporcionar el nombre de usuario como argumento.");
            System.exit(1);
        }
        ChatMulticastUDP chat = new ChatMulticastUDP(args[0]);
        chat.startReceivingMessages();
        chat.startSendingMessages();
    }
}
