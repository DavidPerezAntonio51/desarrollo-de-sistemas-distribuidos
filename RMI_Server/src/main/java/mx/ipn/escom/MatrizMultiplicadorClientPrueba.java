package mx.ipn.escom;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class MatrizMultiplicadorClientPrueba {
    public static void main(String[] args) {
        try {

            // Localizar el registro RMI
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            // Obtener la instancia del objeto remoto
            MatrizMultiplicador multiplicador = (MatrizMultiplicador) registry.lookup("multiplica_matrices");

            // Crear dos matrices de ejemplo
            float[][] matrizA = {
                    {1, 2,3}
            };

            float[][] matrizB = {
                    {7, 9,11}
            };

            // Llamar al método remoto multiplica_matrices()
            int id = 1;
            ResultadoMatriz resultado = multiplicador.multiplica_matrices(matrizA, matrizB, id);

            // Imprimir el resultado
            System.out.println("ID: " + resultado.getId());
            System.out.println("Filas: " + resultado.getFilas());
            System.out.println("Columnas: " + resultado.getColumnas());
            System.out.println("Matriz resultante: ");
            float[][] matrizC = resultado.getMatrizC();
            for (int i = 0; i < resultado.getFilas(); i++) {
                for (int j = 0; j < resultado.getColumnas(); j++) {
                    System.out.print(matrizC[i][j] + " ");
                }
                System.out.println();
            }
        } catch (Exception e) {
            System.err.println("Error en el cliente RMI: " + e.getMessage());
        }
    }
}
