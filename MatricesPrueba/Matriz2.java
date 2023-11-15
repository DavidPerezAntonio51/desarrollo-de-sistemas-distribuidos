import java.util.Arrays;

public class Matriz2 {
    public static void main(String[] args) {
        int N = 12; // Tamaño de la matriz cuadrada (cambiar según sea necesario)

        double A[][] = new double[N][N];
        double B[][] = new double[N][N];
        // Initialize matrices A and B
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                A[i][j] = 2 * i + j;
                B[i][j] = 3 * i - j;
            }
        }

        // Transpose B in place
        for (int i = 0; i < N; i++) {
            for (int j = i+1; j < N; j++) {
                double temp = B[i][j];
                B[i][j] = B[j][i];
                B[j][i] = temp;
            }
        }

        double[][] result = new double[N][N]; // Matriz resultante

        // Multiplicación de matrices
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                double sum = 0;
                for (int k = 0; k < N; k++) {
                    sum +=A[i][k] * B[j][k];
                }
                result[i][j] = sum;
            }
        }

        // Imprimir la matriz resultante
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                System.out.print(result[i][j] + " ");
            }
            System.out.println();
        }
        showChecksum(result,N);
    }

    private static void showChecksum(double[][] C, int MATRIX_SIZE) {
        int n = MATRIX_SIZE;
        double sum = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                sum += C[i][j];
            }
        }
        System.out.println("Checksum: " + sum);
    }
}
