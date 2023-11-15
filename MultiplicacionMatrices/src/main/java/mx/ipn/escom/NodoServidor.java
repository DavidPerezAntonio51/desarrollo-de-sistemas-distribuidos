package mx.ipn.escom;

import java.io.*;
import java.net.*;

public class NodoServidor {
    private int puerto;

    public NodoServidor(int puerto) {
        this.puerto = puerto;
    }

    public void iniciar() {
        try (ServerSocket serverSocket = new ServerSocket(puerto)) {
            System.out.println("Servidor iniciado en el puerto " + puerto);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Nueva conexión recibida desde " + socket.getInetAddress().getHostName());

                Thread thread = new Thread(() -> {
                    try {
                        DataInputStream entrada = new DataInputStream(socket.getInputStream());
                        DataOutputStream salida = new DataOutputStream(socket.getOutputStream());

                        // Leer el ID y las cuatro matrices
                        int id = entrada.readInt();
                        int n = entrada.readInt();
                        int m = entrada.readInt();
                        double[][] A = new double[n][m];
                        double[][] BT1 = new double[n][m];
                        double[][] BT2 = new double[n][m];
                        double[][] BT3 = new double[n][m];
                        for (int i = 0; i < n; i++) {
                            for (int j = 0; j < m; j++) {
                                A[i][j] = entrada.readDouble();
                            }
                        }
                        for (int i = 0; i < n; i++) {
                            for (int j = 0; j < m; j++) {
                                BT1[i][j] = entrada.readDouble();
                            }
                        }
                        for (int i = 0; i < n; i++) {
                            for (int j = 0; j < m; j++) {
                                BT2[i][j] = entrada.readDouble();
                            }
                        }
                        for (int i = 0; i < n; i++) {
                            for (int j = 0; j < m; j++) {
                                BT3[i][j] = entrada.readDouble();
                            }
                        }

                        // Initialize matrices C1, C2 and C3
                        double[][] C1 = new double[n][n];
                        double[][] C2 = new double[n][n];
                        double[][] C3 = new double[n][n];

// Multiply A by BT1, BT2 and BT3 transposed using fila x fila multiplication
                        for (int i = 0; i < n; i++) {
                            for (int j = 0; j < n; j++) {
                                double sum1 = 0;
                                double sum2 = 0;
                                double sum3 = 0;
                                for (int k = 0; k < m; k++) {
                                    sum1 += A[i][k] * BT1[j][k];
                                    sum2 += A[i][k] * BT2[j][k];
                                    sum3 += A[i][k] * BT3[j][k];
                                }
                                C1[i][j] = sum1;
                                C2[i][j] = sum2;
                                C3[i][j] = sum3;
                            }
                        }

                        // Enviar los resultados
                        salida.writeUTF("Operación completada");
                        salida.writeInt(id);
                        salida.writeInt(n);
                        salida.writeInt(n);
                        for (int i = 0; i < n; i++) {
                            for (int j = 0; j < n; j++) {
                                salida.writeDouble(C1[i][j]);
                            }
                        }
                        for (int i = 0; i < n; i++) {
                            for (int j = 0; j < n; j++) {
                                salida.writeDouble(C2[i][j]);
                            }
                        }
                        for (int i = 0; i < n; i++) {
                            for (int j = 0; j < n; j++) {
                                salida.writeDouble(C3[i][j]);
                            }
                        }
                        socket.close();
                        System.out.println("Conexión cerrada");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}