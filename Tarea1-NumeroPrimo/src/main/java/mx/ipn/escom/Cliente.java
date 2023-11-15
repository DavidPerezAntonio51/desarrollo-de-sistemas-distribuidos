package mx.ipn.escom;

import java.io.*;
import java.net.*;

public class Cliente {
    public static void main(String[] args) throws IOException {
        BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            System.out.println("Introduce un número entero para verificar si es primo (introduce un valor negativo para salir): ");
            String entradaUsuario = teclado.readLine();
            long numero = Long.parseLong(entradaUsuario);

            if (numero < 0) {
                System.out.println("Saliendo...");
                break;
            }

            Socket socket = new Socket("localhost", 5000);
            DataOutputStream salida = new DataOutputStream(socket.getOutputStream());
            DataInputStream entrada = new DataInputStream(socket.getInputStream());

            salida.writeLong(numero);
            String respuesta = entrada.readUTF();

            System.out.println("Respuesta del servidor: " + respuesta);

            socket.close();
        }
    }
}

