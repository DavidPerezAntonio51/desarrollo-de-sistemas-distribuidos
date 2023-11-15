package mx.ipn.escom;

import javax.net.ssl.SSLServerSocketFactory;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    public static void main(String[] args) {
        System.setProperty("javax.net.debug", "ssl");
        System.setProperty("java.security.debug", "certpath");
        System.setProperty("javax.net.ssl.keyStore", "D:\\Ubuntu\\Diseño de Sistemas Distribuidos\\ClavesSSH\\keystore_servidor.jks");
        System.setProperty("javax.net.ssl.keyStorePassword", "1234567890");
        try {
            SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            ServerSocket serverSocket = factory.createServerSocket(5000);
            Socket conexion = serverSocket.accept();
            DataInputStream entrada = new DataInputStream(conexion.getInputStream());
            DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
            double x = entrada.readDouble();
            System.out.println(x);
            Thread.sleep(1500);
            conexion.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
