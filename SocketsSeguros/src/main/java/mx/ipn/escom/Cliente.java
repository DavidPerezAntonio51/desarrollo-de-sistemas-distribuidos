package mx.ipn.escom;

import javax.net.ssl.SSLSocketFactory;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Cliente {
    public static void main(String[] args) {
        System.setProperty("javax.net.debug", "ssl");
        System.setProperty("java.security.debug", "certpath");
        System.setProperty("javax.net.ssl.trustStore","D:\\Ubuntu\\Diseño de Sistemas Distribuidos\\ClavesSSH\\keystore_cliente.jks");
        System.setProperty("javax.net.ssl.trustStorePassword","1234567890");
        try {
            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            Socket conexion = factory.createSocket("localhost",5000);
            DataInputStream entrada = new DataInputStream(conexion.getInputStream());
            DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
            salida.writeDouble(1234567890.1234567890);
            Thread.sleep(100);
            conexion.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
