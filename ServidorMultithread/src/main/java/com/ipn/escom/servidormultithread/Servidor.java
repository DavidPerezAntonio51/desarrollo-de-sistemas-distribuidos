package com.ipn.escom.servidormultithread;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

/**
 *
 * @author DEZKS
 */
public class Servidor {
    public static void main(String[] args) {
        try {
            ServerSocket servidor = new ServerSocket(5000);
            for (;;){
                Socket conexion = servidor.accept();
                Worker w = new Worker(conexion);
                w.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    static class Worker extends Thread{
        Socket conexion;
        Worker(Socket conexion){
            this.conexion = conexion;
        }
        @Override
        public void run() {
            try {
                DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
                DataInputStream entrada = new DataInputStream(conexion.getInputStream());

                int n = entrada.readInt();
                System.out.println(n);

                double x = entrada.readDouble();
                System.out.println(x);

                byte[] buffer = new byte[4];
                read(entrada, buffer, 0, 4);
                System.out.println(new String(buffer, "UTF-8"));

                salida.write("HOLA".getBytes());

                byte[] a = new byte[5 * 8];
                read(entrada, a, 0, 5 * 8);

                ByteBuffer b = ByteBuffer.wrap(a);
                for (int i = 0; i < 5; i++) {
                    System.out.println(b.getDouble());
                }
                conexion.close();
            }catch (IOException ex){

            }
        }
        static void read(DataInputStream d, byte b[], int posicion, int longitud) throws IOException {
            while (longitud>0){
                int n = d.read(b,posicion,longitud);
                posicion+=n;
                longitud-=n;
            }
        }
    }
}
