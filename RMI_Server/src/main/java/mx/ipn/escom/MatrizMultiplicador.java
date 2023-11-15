package mx.ipn.escom;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MatrizMultiplicador extends Remote {
    ResultadoMatriz multiplica_matrices(float[][] a, float[][] b, int id) throws RemoteException;
}
