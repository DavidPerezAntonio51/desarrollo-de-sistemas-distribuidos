import java.io.*;
import java.security.KeyStore;
import java.security.SecureRandom;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class MultiThreadedServer implements Runnable {
    SSLSocket clientSocket;

    public MultiThreadedServer(SSLSocket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public static void main(String[] args) throws Exception {
        String[] enabledProtocols = { "TLSv1.2" };
        String[] enabledCipherSuites = { "TLS_RSA_WITH_AES_128_CBC_SHA256" };
        
        // Configurar el socket seguro
        String keyStorePath = "keystore_servidor.jks";
        char[] keyStorePassword = "1234567890".toCharArray();
        
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(new FileInputStream(keyStorePath), keyStorePassword);
        
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, keyStorePassword);
        
        TrustManagerFactory trustManagerFactory = TrustManagerFactory
        .getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);

        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());
        
        SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
        SSLServerSocket serverSocket = ((SSLServerSocket) sslServerSocketFactory.createServerSocket(8000));
        
        System.out.println("Server running on port 8000");
        while (true) {
            SSLSocket clientSocket = (SSLSocket) serverSocket.accept();
            // Establecer la conexión
            
            clientSocket.setEnabledProtocols(enabledProtocols);
            clientSocket.setEnabledCipherSuites(enabledCipherSuites);
            MultiThreadedServer server = new MultiThreadedServer(clientSocket);
            Thread serverThread = new Thread(server);
            serverThread.start();
        }
    }

    @Override
    public void run() {
        try {
            DataInputStream inFromClient = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream outToClient = new DataOutputStream(clientSocket.getOutputStream());

            String requestType = inFromClient.readUTF(); // Lee el tipo de petición
            String fileName = inFromClient.readUTF(); // Lee el nombre del archivo

            if (requestType.equals("GET")) {
                try {
                    FileInputStream fileInput = new FileInputStream(fileName);
                    outToClient.writeUTF("OK"); // Envía la respuesta OK
                    outToClient.writeLong(fileInput.getChannel().size()); // Envía el tamaño del archivo

                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = fileInput.read(buffer)) != -1) {
                        outToClient.write(buffer, 0, bytesRead); // Envía el contenido del archivo
                    }

                    fileInput.close();
                } catch (FileNotFoundException e) {
                    outToClient.writeUTF("ERROR"); // Envía la respuesta ERROR si el archivo no se puede leer
                }
            } else if (requestType.equals("PUT")) {
                try {
                    FileOutputStream fileOutput = new FileOutputStream(fileName);
                    long fileSize = inFromClient.readLong(); // Lee el tamaño del archivo

                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    long totalBytesRead = 0;
                    while (totalBytesRead < fileSize && (bytesRead = inFromClient.read(buffer, 0,
                            (int) Math.min(buffer.length, fileSize - totalBytesRead))) != -1) {
                        fileOutput.write(buffer, 0, bytesRead); // Escribe el contenido del archivo en el disco
                        totalBytesRead += bytesRead;
                    }

                    fileOutput.close();
                    if (totalBytesRead == fileSize) {
                        outToClient.writeUTF("OK"); // Envía la respuesta OK si se pudo escribir el archivo en el disco
                    } else {
                        outToClient.writeUTF("ERROR"); // Envía la respuesta ERROR si no se pudo escribir el archivo en
                                                       // el disco
                    }
                } catch (IOException e) {
                    outToClient.writeUTF("ERROR"); // Envía la respuesta ERROR si no se pudo escribir el archivo en el
                                                   // disco
                }
            }

            inFromClient.close();
            outToClient.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
