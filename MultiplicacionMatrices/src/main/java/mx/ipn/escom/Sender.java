package mx.ipn.escom;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Sender extends Thread {
    private final int MATRIX_SIZE;
    private final String serverIp;
    private final int serverPort;
    private final int node;
    private final double[][] Ax;
    private final double[][] B1;
    private final double[][] B2;
    private final double[][] B3;
    private double[][][] C;


    public Sender(int matrix_size, String serverIp, int serverPort, int node, double[][] ax, double[][] b1, double[][] b2, double[][] b3) {
        MATRIX_SIZE = matrix_size;
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        this.node = node;
        Ax = ax;
        B1 = b1;
        B2 = b2;
        B3 = b3;
        System.out.println("Nodo " + node + " en ip:" + serverIp + " puerto: " + serverPort);
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket(serverIp, serverPort);
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            DataInputStream dis = new DataInputStream(socket.getInputStream());

            // Send message header
            dos.writeInt(node);
            dos.writeInt(MATRIX_SIZE / 3);
            dos.writeInt(MATRIX_SIZE);

            sendMatrix(MATRIX_SIZE / 3, MATRIX_SIZE, Ax, dos);
            sendMatrix(MATRIX_SIZE / 3, MATRIX_SIZE, B1, dos);
            sendMatrix(MATRIX_SIZE / 3, MATRIX_SIZE, B2, dos);
            sendMatrix(MATRIX_SIZE / 3, MATRIX_SIZE, B3, dos);

            // Wait for response
            String response = dis.readUTF();
            int id = dis.readInt();
            int n1 = dis.readInt();
            int n2 = dis.readInt();
            System.out.println("Response from " + node + ": " + response);
            System.out.println("id: " + id);
            System.out.println("n1: " + n1);
            System.out.println("n2: " + n2);
            C = new double[3][n1][n2];
            for (int k = 0; k < 3; k++) {
                C[k] = receiveMatrix(n1, n2, dis);
            }

            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMatrix(int n1, int n2, double[][] matrix, DataOutputStream dos) throws IOException {
        // Write the matrix values
        for (int i = 0; i < n1; i++) {
            for (int j = 0; j < n2; j++) {
                dos.writeDouble(matrix[i][j]);
            }
        }
    }

    private double[][] receiveMatrix(int n1, int n2, DataInputStream dis) throws IOException {

        // Read the matrix values
        double[][] matrix = new double[n1][n2];
        for (int i = 0; i < n1; i++) {
            for (int j = 0; j < n2; j++) {
                matrix[i][j] = dis.readDouble();
            }
        }

        return matrix;
    }

    public double[][] getC(int C) {
        return this.C[C];
    }
}
