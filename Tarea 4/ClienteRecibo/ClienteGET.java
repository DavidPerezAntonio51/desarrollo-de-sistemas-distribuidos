import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyStore;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class ClienteGET {

    public static void main(String[] args) throws Exception {
        String[] enabledProtocols = { "TLSv1.2" };
        String[] enabledCipherSuites = { "TLS_RSA_WITH_AES_128_CBC_SHA256" };
        if (args.length < 3) { 
            System.out.println("Usage: ClienteGET <ipServidor> <puerto> <nombreArchivo>");
            return;
        }

        String ipServidor = args[0];
        int puerto = Integer.parseInt(args[1]);
        String nombreArchivo = args[2];

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

        try {
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            // Enviar la petición GET y el nombre del archivo
            out.writeUTF("GET");
            out.writeUTF(nombreArchivo);

            // Leer la respuesta del servidor
            String respuesta = in.readUTF();

            if (respuesta.equals("OK")) {
                long fileSize = in.readLong(); // Lee el tamaño del archivo
                byte[] buffer = new byte[4096];
                int bytesRead;
                try (FileOutputStream fos = new FileOutputStream(nombreArchivo)) {
                    while (fileSize > 0
                            && (bytesRead = in.read(buffer, 0, (int) Math.min(buffer.length, fileSize))) != -1) {
                        fos.write(buffer, 0, bytesRead); // Escribe el contenido del archivo
                        fileSize -= bytesRead;
                    }
                }
                System.out.println("Archivo recibido con éxito.");
            } else if (respuesta.equals("ERROR")) {
                System.out.println("El archivo no existe en el servidor.");
            } else {
                System.out.println("Respuesta desconocida del servidor.");
            }
        }catch (UnknownHostException e) {
            System.out.println("Host desconocido: " + ipServidor);
        } catch (IOException e) {
            System.out.println("Error al conectar con el servidor");
        }
    }
}
