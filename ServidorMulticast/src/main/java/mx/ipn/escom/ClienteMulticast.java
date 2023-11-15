package mx.ipn.escom;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;

public class ClienteMulticast {
    public static void main(String[] args) throws IOException {
        System.setProperty("java.net.preferIPv4Stack","true");
        MulticastSocket socket = new MulticastSocket(5000);
        InetSocketAddress grupo = new InetSocketAddress(Inet4Address.getByName("230.0.0.0"),5000);
        NetworkInterface networkInterface = NetworkInterface.getByName("Wi-Fi");
        socket.joinGroup(grupo,networkInterface);

        byte[]a = recibe_mensaje(socket,4);
        System.out.println(new String(a,"UTF-8"));

        byte[]buffer = recibe_mensaje(socket,5*8);
        ByteBuffer b = ByteBuffer.wrap(buffer);
        for (int i =0; i<5;i++)
            System.out.println(b.getDouble());
        socket.leaveGroup(grupo,networkInterface);
        socket.close();
    }
    static byte[] recibe_mensaje(MulticastSocket socket, int longitud_mensaje) throws IOException {
        byte[] buffer = new byte[longitud_mensaje];
        DatagramPacket paquete = new DatagramPacket(buffer,buffer.length);
        socket.receive(paquete);
        return paquete.getData();
    }
}
