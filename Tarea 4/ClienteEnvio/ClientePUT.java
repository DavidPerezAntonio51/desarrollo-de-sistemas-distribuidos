import java.io.*;
import java.net.*;
import java.security.KeyStore;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class ClientePUT {

    public static void main(String[] args) throws Exception{
        String[] enabledProtocols = { "TLSv1.2" };
        String[] enabledCipherSuites = { "TLS_RSA_WITH_AES_128_CBC_SHA256" };
        // Leer los parámetros de entrada
        String ipServidor = args[0];
        int puerto = Integer.parseInt(args[1]);
        String nombreArchivo = args[2];

        // Leer el archivo del disco local
        File archivo = new File(nombreArchivo);
        byte[] archivoBytes = new byte[(int) archivo.length()];
        try (FileInputStream fis = new FileInputStream(archivo)) {
            fis.read(archivoBytes);
        } catch (IOException e) {
            System.out.println("Error al leer el archivo");
            return;
        }

        // Cargar el truststore con el certificado del servidor
        String truststorePath = "keystore_cliente.jks";
        char[] truststorePassword = "1234567890".toCharArray();
        KeyStore truststore = KeyStore.getInstance("JKS");
        truststore.load(new FileInputStream(truststorePath), truststorePassword);

        // Crear un TrustManagerFactory para verificar el certificado del servidor
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(truststore);

        // Crear un SSLContext con los TrustManagers configurados
        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
        sslContext.init(null, tmf.getTrustManagers(), null);

        // Crear un SSLSocketFactory a partir del SSLContext
        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

        // Crear un socket seguro y conectarse al servidor
        SSLSocket socket = (SSLSocket)sslSocketFactory.createSocket(ipServidor, puerto);
        socket.setEnabledProtocols(enabledProtocols);
        socket.setEnabledCipherSuites(enabledCipherSuites);


        // Conectar con el servidor usando sockets normales
        try {
            // Enviar la petición PUT y los datos del archivo
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF("PUT");
            out.writeUTF(nombreArchivo);
            out.writeLong(archivoBytes.length);
            int bytesRead = 0;
            while (bytesRead < archivoBytes.length) {
                int bytesToRead = Math.min(archivoBytes.length - bytesRead, 4096);
                out.write(archivoBytes, bytesRead, bytesToRead);
                bytesRead += bytesToRead;
            }

            // Esperar la respuesta del servidor
            DataInputStream in = new DataInputStream(socket.getInputStream());
            String respuesta = in.readUTF();

            // Desplegar el mensaje correspondiente
            if (respuesta.equals("OK")) {
                System.out.println("El archivo fue recibido por el servidor con éxito");
            } else {
                System.out.println("El servidor no pudo escribir el archivo en el disco local");
            }

        } catch (UnknownHostException e) {
            System.out.println("Host desconocido: " + ipServidor);
        } catch (IOException e) {
            System.out.println("Error al conectar con el servidor");
        }

    }

}
