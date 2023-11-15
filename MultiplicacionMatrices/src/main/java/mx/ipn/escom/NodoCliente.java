package mx.ipn.escom;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

public class NodoCliente {
    private static final String DEFAULT_IP = "localhost";
    private static final int MATRIX_SIZE = 3000;

    private final String[] ips;
    private final int[] ports;
    private final double[][] A;
    private final double[][] B;
    private final int numNodes;


    public NodoCliente(int port1, int port2, int port3, String... ips) {
        this.ips = ips.length == 0 ? new String[]{DEFAULT_IP, DEFAULT_IP, DEFAULT_IP} : ips;
        this.ports = new int[]{port1, port2, port3};
        this.numNodes = this.ips.length;
        this.A = new double[MATRIX_SIZE][MATRIX_SIZE];
        this.B = new double[MATRIX_SIZE][MATRIX_SIZE];

        // Initialize matrices A and B
        for (int i = 0; i < MATRIX_SIZE; i++) {
            for (int j = 0; j < MATRIX_SIZE; j++) {
                A[i][j] = 2 * i + j;
                B[i][j] = 3 * i - j;
            }
        }

        // Transpose B in place
        for (int i = 0; i < MATRIX_SIZE; i++) {
            for (int j = i + 1; j < MATRIX_SIZE; j++) {
                double temp = B[i][j];
                B[i][j] = B[j][i];
                B[j][i] = temp;
            }
        }
    }

    public void iniciar() {
        // Divide matrices A and B into A1, A2, A3 and B1, B2, B3
        double[][] A1 = new double[MATRIX_SIZE / 3][MATRIX_SIZE];
        double[][] A2 = new double[MATRIX_SIZE / 3][MATRIX_SIZE];
        double[][] A3 = new double[MATRIX_SIZE / 3][MATRIX_SIZE];
        double[][] B1 = new double[MATRIX_SIZE / 3][MATRIX_SIZE];
        double[][] B2 = new double[MATRIX_SIZE / 3][MATRIX_SIZE];
        double[][] B3 = new double[MATRIX_SIZE / 3][MATRIX_SIZE];

        // Create matrices C1-C9
        double[][] C1 = new double[MATRIX_SIZE / 3][MATRIX_SIZE / 3];
        double[][] C2 = new double[MATRIX_SIZE / 3][MATRIX_SIZE / 3];
        double[][] C3 = new double[MATRIX_SIZE / 3][MATRIX_SIZE / 3];
        double[][] C4 = new double[MATRIX_SIZE / 3][MATRIX_SIZE / 3];
        double[][] C5 = new double[MATRIX_SIZE / 3][MATRIX_SIZE / 3];
        double[][] C6 = new double[MATRIX_SIZE / 3][MATRIX_SIZE / 3];
        double[][] C7 = new double[MATRIX_SIZE / 3][MATRIX_SIZE / 3];
        double[][] C8 = new double[MATRIX_SIZE / 3][MATRIX_SIZE / 3];
        double[][] C9 = new double[MATRIX_SIZE / 3][MATRIX_SIZE / 3];


        // Divide matrices A and B into A1, A2, A3 and B1, B2, B3
        for (int i = 0; i < MATRIX_SIZE / 3; i++) {
            for (int j = 0; j < MATRIX_SIZE; j++) {
                A1[i][j] = A[i][j];
                A2[i][j] = A[i + MATRIX_SIZE / 3][j];
                A3[i][j] = A[i + 2 * MATRIX_SIZE / 3][j];

                B1[i][j] = B[i][j];
                B2[i][j] = B[i + MATRIX_SIZE / 3][j];
                B3[i][j] = B[i + 2 * MATRIX_SIZE / 3][j];
            }
        }

        // Send matrices to each server node
        Sender[] hilos = new Sender[3];
        for (int i = 0; i < numNodes; i++) {
            String serverIp = ips[i];
            int serverPort = ports[i];
            int node = (i + 1);
            switch (node) {
                case 1:
                    hilos[i] = new Sender(MATRIX_SIZE, serverIp, serverPort, node, A1, B1, B2, B3);
                    break;
                case 2:
                    hilos[i] = new Sender(MATRIX_SIZE, serverIp, serverPort, node, A2, B1, B2, B3);
                    break;
                case 3:
                    hilos[i] = new Sender(MATRIX_SIZE, serverIp, serverPort, node, A3, B1, B2, B3);
                    break;
            }
            hilos[i].start();
        }
        for (int i = 0; i < numNodes; i++) {
            try {
                hilos[i].join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        int node_c = 1;
        for (int i = 0; i < numNodes; i++) {
            for (int j = 0; j < numNodes; j++) {
                switch (node_c) {
                    case 1:
                        C1 = hilos[i].getC(j);
                        break;
                    case 2:
                        C2 = hilos[i].getC(j);
                        break;
                    case 3:
                        C3 = hilos[i].getC(j);
                        break;
                    case 4:
                        C4 = hilos[i].getC(j);
                        break;
                    case 5:
                        C5 = hilos[i].getC(j);
                        break;
                    case 6:
                        C6 = hilos[i].getC(j);
                        break;
                    case 7:
                        C7 = hilos[i].getC(j);
                        break;
                    case 8:
                        C8 = hilos[i].getC(j);
                        break;
                    case 9:
                        C9 = hilos[i].getC(j);
                        break;
                }
                node_c++;
            }
        }
        double[][] C = new double[MATRIX_SIZE][MATRIX_SIZE];

// Copy C1, C2, C3 to the first row of C
        for (int i = 0; i < MATRIX_SIZE / 3; i++) {
            for (int j = 0; j < MATRIX_SIZE / 3; j++) {
                C[i][j] = C1[i][j];
                C[i][j + MATRIX_SIZE / 3] = C2[i][j];
                C[i][j + 2 * MATRIX_SIZE / 3] = C3[i][j];
            }
        }

// Copy C4, C5, C6 to the second row of C
        for (int i = 0; i < MATRIX_SIZE / 3; i++) {
            for (int j = 0; j < MATRIX_SIZE / 3; j++) {
                C[i + MATRIX_SIZE / 3][j] = C4[i][j];
                C[i + MATRIX_SIZE / 3][j + MATRIX_SIZE / 3] = C5[i][j];
                C[i + MATRIX_SIZE / 3][j + 2 * MATRIX_SIZE / 3] = C6[i][j];
            }
        }

// Copy C7, C8, C9 to the third row of C
        for (int i = 0; i < MATRIX_SIZE / 3; i++) {
            for (int j = 0; j < MATRIX_SIZE / 3; j++) {
                C[i + 2 * MATRIX_SIZE / 3][j] = C7[i][j];
                C[i + 2 * MATRIX_SIZE / 3][j + MATRIX_SIZE / 3] = C8[i][j];
                C[i + 2 * MATRIX_SIZE / 3][j + 2 * MATRIX_SIZE / 3] = C9[i][j];
            }
        }
        if (MATRIX_SIZE == 12) {
            showMatrixC(C);
        }
        showChecksum(C, MATRIX_SIZE);

    }

    private void showChecksum(double[][] C, int MATRIX_SIZE) {
        int n = MATRIX_SIZE;
        double sum = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                sum += C[i][j];
            }
        }
        System.out.println("Checksum: " + sum);
    }

    private void showMatrixC(double[][] C) {
        // Imprime cada fila de la matriz C
        for (int i = 0; i < MATRIX_SIZE; i++) {
            for (int j = 0; j < MATRIX_SIZE; j++) {
                System.out.print(C[i][j] + " ");
            }
            System.out.println(); // nueva línea después de imprimir cada fila
        }
    }

}
