package mx.ipn.escom;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class MatrizMultiplicadorImpl extends UnicastRemoteObject implements MatrizMultiplicador {
    protected MatrizMultiplicadorImpl() throws RemoteException {
        super();
    }

    @Override
    public ResultadoMatriz multiplica_matrices(float[][] a, float[][] bTranspuesta, int id) throws RemoteException {
        int filasA = a.length;
        int columnasA = a[0].length;
        int filasBTranspuesta = bTranspuesta.length;
        int columnasB = bTranspuesta[0].length;

        float[][] matrizC = new float[filasA][filasBTranspuesta];

        for (int i = 0; i < filasA; i++) {
            for (int j = 0; j < filasBTranspuesta; j++) {
                int sumaProducto = 0;
                for (int k = 0; k < columnasA; k++) {
                    sumaProducto += a[i][k] * bTranspuesta[j][k];
                }
                matrizC[i][j] = sumaProducto;
            }
        }

        return new ResultadoMatriz(id, matrizC, filasA, filasBTranspuesta);
    }
}
