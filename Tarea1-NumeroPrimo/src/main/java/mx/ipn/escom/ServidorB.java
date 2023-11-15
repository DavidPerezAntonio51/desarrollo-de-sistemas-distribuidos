package mx.ipn.escom;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorB {
    private static final int NUM_SERVIDORES = 3;

    public static void main(String[] args) {
        if (args.length < NUM_SERVIDORES) {
            System.out.println("Debe especificar los puertos de los 3 servidores");
            return;
        }

        try {
            ServerSocket serverSocket = new ServerSocket(5000);
            System.out.println("ServidorB iniciado en el puerto " + serverSocket.getLocalPort());

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Nueva conexión entrante");
                Service service = new Service(socket,args);
                new Thread(service).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class Service implements Runnable {
        private final Socket clientSocket;
        private final String[] args;

        public Service(Socket clieSocket, String[] args) {
            this.clientSocket = clieSocket;
            this.args = args;
        }

        @Override
        public void run() {
            try {
                DataInputStream entrada = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream salida = new DataOutputStream(clientSocket.getOutputStream());

                long numero = entrada.readLong();

                ServidoresThread[] servidores = new ServidoresThread[NUM_SERVIDORES];

                long k = numero / 3;
                for (int i = 0; i < NUM_SERVIDORES; i++) {
                    Socket dir_serverSocket = new Socket("localhost", Integer.parseInt(this.args[i]));

                    long inicio, fin;

                    if (i == 0) {
                        inicio = 2;
                        fin = k;
                    } else if (i == 1) {
                        inicio = k + 1;
                        fin = 2 * k;
                    } else {
                        inicio = 2 * k + 1;
                        fin = numero - 1;
                    }

                    servidores[i] = new ServidoresThread(numero, inicio, fin, dir_serverSocket);
                    servidores[i].start();
                }

                boolean divisible = false;
                for (int i = 0; i < NUM_SERVIDORES; i++) {
                    servidores[i].join();
                    divisible |= servidores[i].isResultado();
                }

                if (divisible) {
                    salida.writeUTF("NO ES PRIMO");
                } else {
                    salida.writeUTF("ES PRIMO");
                }

                salida.close();
                entrada.close();
                clientSocket.close();
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static class ServidoresThread extends Thread {
        private final long numero;
        private final long inicio;
        private final long fin;
        private final Socket socketServer;
        private boolean resultado = false;

        public ServidoresThread(long numero, long inicio, long fin, Socket socketServer) {
            this.numero = numero;
            this.inicio = inicio;
            this.fin = fin;
            this.socketServer = socketServer;
        }

        @Override
        public void run() {
            try {
                DataOutputStream salida = new DataOutputStream(socketServer.getOutputStream());
                DataInputStream entrada = new DataInputStream(socketServer.getInputStream());

                salida.writeLong(numero);
                salida.writeLong(inicio);
                salida.writeLong(fin);

                String resultado = entrada.readUTF();
                if(resultado.equals("DIVIDE")){
                    this.resultado = true;
                } else if (resultado.equals("NO DIVIDE")) {
                    this.resultado = false;
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public boolean isResultado() {
            return resultado;
        }
    }
}
