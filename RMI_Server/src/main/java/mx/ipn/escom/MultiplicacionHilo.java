package mx.ipn.escom;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

public class MultiplicacionHilo extends Thread {
    private String serverUrl;
    private int hiloId;
    private float[][] matrizA;
    private float[][] matrizBTranspuesta;
    private List<ResultadoMatriz> resultados;

    public MultiplicacionHilo(int hiloId, float[][] matrizA, float[][] matrizBTranspuesta, String serverUrl) {
        this.hiloId = hiloId;
        this.matrizA = matrizA;
        this.matrizBTranspuesta = matrizBTranspuesta;
        this.serverUrl = serverUrl;
        this.resultados = new ArrayList<>();
    }

    @Override
    public void run() {
        try {
            // Localizar el registro RMI
            Registry registry = LocateRegistry.getRegistry(serverUrl, 1099);

            // Obtener la instancia del objeto remoto
            MatrizMultiplicador multiplicador = (MatrizMultiplicador) registry.lookup("multiplica_matrices");

            // ... Resto del método run() ...
            int filasA = matrizA.length;
            int columnasB = matrizBTranspuesta.length;
            int divisionA = filasA / 3;
            int divisionB = columnasB / 9;

            // Dividir matrizA en 3 partes y matrizBTranspuesta en 9 partes
            for (int i = 0; i < 3; i++) {
                float[][] subMatrizA = new float[divisionA][];
                System.arraycopy(matrizA, i * divisionA, subMatrizA, 0, divisionA);

                for (int j = 0; j < 9; j++) {
                    float[][] subMatrizB = new float[divisionB][];
                    System.arraycopy(matrizBTranspuesta, j * divisionB, subMatrizB, 0, divisionB);

                    try {
                        // Llamar al método multiplica_matrices()
                        int id = hiloId * 100 + i * 10 + j;
                        ResultadoMatriz resultado = multiplicador.multiplica_matrices(subMatrizA, subMatrizB, id);

                        // Almacenar el resultado
                        resultados.add(resultado);
                    } catch (RemoteException e) {
                        System.err.println("Error en el hilo " + hiloId + ": " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error en el hilo " + hiloId + ": " + e.getMessage());
        }
    }

    public List<ResultadoMatriz> getResultados() {
        return resultados;
    }
}
