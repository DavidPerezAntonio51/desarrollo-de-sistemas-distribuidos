package mx.ipn.escom;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

public class MatrizMultiplicadorClient {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Uso: MatrizMultiplicadorClient <N> <M> [URL servidor RMI] [URL servidor RMI] [URL servidor RMI]");
            System.exit(1);
        }
        String[] serverUrls = new String[3];
        for (int i = 0; i < 3; i++) {
            if (args.length > 3 + i) {
                serverUrls[i] = args[3 + i];
            } else {
                serverUrls[i] = "localhost";
            }
        }

        String rmiServerUrl = args.length > 2 ? args[2] : "localhost";

        int N = Integer.parseInt(args[0]);
        int M = Integer.parseInt(args[1]);

        float[][] matrizA = new float[N][M];
        float[][] matrizBTranspuesta = new float[N][M];

        // Inicializar matriz A
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                matrizA[i][j] = 2 * i + 3 * j;
            }
        }

        // Inicializar matriz B transpuesta
        inicializarMatrizBTranspuesta(matrizBTranspuesta, M, N);

        System.out.println("Matriz A: ");
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                System.out.print(matrizA[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println("Matriz B: ");
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                System.out.print(matrizBTranspuesta[i][j] + " ");
            }
            System.out.println();
        }


        // Resto del código del cliente (conexión RMI, llamada al método remoto, etc.)
        try {

            // Localizar el registro RMI
            Registry registry = LocateRegistry.getRegistry(rmiServerUrl, 1099);

            // Obtener la instancia del objeto remoto
            MatrizMultiplicador multiplicador = (MatrizMultiplicador) registry.lookup("multiplica_matrices");

            // Crear y ejecutar los hilos
            int numHilos = 3;
            MultiplicacionHilo[] hilos = new MultiplicacionHilo[numHilos];
            for (int i = 0; i < numHilos; i++) {
                float[][] subMatrizA = new float[N / numHilos][];
                System.arraycopy(matrizA, i * N / numHilos, subMatrizA, 0, N / numHilos);
                hilos[i] = new MultiplicacionHilo(i + 1, subMatrizA, matrizBTranspuesta, serverUrls[i]);
                hilos[i].start();
            }

            // Esperar a que todos los hilos terminen
            for (MultiplicacionHilo hilo : hilos) {
                hilo.join();
            }

            // Ensamblar la matriz C
            float[][] matrizC = new float[N][N];
            for (MultiplicacionHilo hilo : hilos) {
                List<ResultadoMatriz> resultados = hilo.getResultados();
                for (ResultadoMatriz resultado : resultados) {
                    int id = resultado.getId();
                    int hiloId = id / 100;
                    int i = (id % 100) / 10;
                    int j = id % 10;
                    int filas = resultado.getFilas();
                    int columnas = resultado.getColumnas();
                    float[][] subMatrizC = resultado.getMatrizC();

                    for (int k = 0; k < filas; k++) {
                        for (int l = 0; l < columnas; l++) {
                            matrizC[((hiloId - 1) * N / numHilos) + (i * filas) + k][j * columnas + l] = subMatrizC[k][l];
                        }
                    }
                }
            }

            // Imprimir la matriz C resultante solo si el tamaño es N=9 y M=4
            imprimirMatrizC(matrizC, N, M);

            // Calcular y mostrar el checksum de la matriz
            calcularChecksum(matrizC, N);
        } catch (Exception e) {
            System.err.println("Error en el cliente RMI: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void inicializarMatrizBTranspuesta(float[][] matrizBTranspuesta, int filasB, int columnasB) {
        float[][] B = new float[filasB][columnasB];
        for (int i = 0; i < filasB; i++) {
            for (int j = 0; j < columnasB; j++) {
                float valor = 3 * i - 2 * j;
                B[i][j] = valor;
                System.out.print(B[i][j] + " ");
            }
            System.out.println();
        }
        // Calcular la matriz B transpuesta
        for (int i = 0; i < filasB; i++) {
            for (int j = 0; j < columnasB; j++) {
                matrizBTranspuesta[j][i] = B[i][j];
            }
        }
    }

    private static void imprimirMatrizC(float[][] matrizC, int N, int M) {
        if (N == 9 && M == 4) {
            System.out.println("Matriz resultante: ");
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    System.out.print(matrizC[i][j] + " ");
                }
                System.out.println();
            }
        } else {
            System.out.println("No se imprime la matriz C debido a que el tamaño no es N=9 y M=4.");
        }
    }

    private static void calcularChecksum(float[][] matrizC, int N) {
        float checksum = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                checksum += matrizC[i][j];
            }
        }
        System.out.println("Checksum de la matriz: " + checksum);
    }
}

