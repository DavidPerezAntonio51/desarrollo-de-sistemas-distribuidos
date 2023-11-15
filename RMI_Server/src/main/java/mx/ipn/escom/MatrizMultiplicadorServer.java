package mx.ipn.escom;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Hello world!
 *
 */
public class MatrizMultiplicadorServer
{
    public static void main( String[] args )
    {
        try {

            // Crear una instancia del objeto remoto
            MatrizMultiplicadorImpl multiplicador = new MatrizMultiplicadorImpl();

            // Crear el registro RMI y vincular el objeto remoto
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("multiplica_matrices", multiplicador);

            System.out.println("Servidor RMI listo...");
        } catch (Exception e) {
            System.err.println("Error en el servidor RMI: " + e.getMessage());
        }
    }
}
