package mx.ipn.escom;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorA {

    public static void main(String[] args) throws IOException {
        int port = 8080;

        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Servidor inciado en el puerto... " + port);
        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Nueva conexión entrante");
            Worker worker = new Worker(clientSocket);
            new Thread(worker).start();
        }
    }

    private static class Worker implements Runnable {
        private Socket socket;

        public Worker(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                DataInputStream entrada = new DataInputStream(socket.getInputStream());
                DataOutputStream salida = new DataOutputStream(socket.getOutputStream());

                long numero = entrada.readLong();
                long numeroInicial = entrada.readLong();
                long numeroFinal = entrada.readLong();

                boolean divide = false;

                for (long n = numeroInicial; n <= numeroFinal; n++) {
                    if (numero % n == 0) {
                        divide = true;
                        System.out.println("Numero: "+numero + " con n = " + n + " resultando: " + numero % n);
                        break;
                    }
                }

                if (divide) {
                    System.out.println("DIVIDE");
                    salida.writeUTF("DIVIDE");
                } else {
                    System.out.println("NO DIVIDE");
                    salida.writeUTF("NO DIVIDE");
                }

                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
