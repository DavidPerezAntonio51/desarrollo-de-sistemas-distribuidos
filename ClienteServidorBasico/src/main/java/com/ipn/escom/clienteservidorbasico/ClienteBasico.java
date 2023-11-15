package com.ipn.escom.clienteservidorbasico;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;

public class ClienteBasico {
    public static void main(String[] args) {
        try {
            Socket conexion = new Socket("localhost", 5000);

            DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
            DataInputStream entrada = new DataInputStream(conexion.getInputStream());

            salida.writeInt(123);
            salida.writeDouble(123.123);
            salida.write("Hola".getBytes());

            byte[] buffer = new byte[4];
            read(entrada,buffer,0,4);
            System.out.println(new String(buffer,"UTF-8"));

            ByteBuffer b = ByteBuffer.allocate(5*8);
            b.putDouble(1.1);
            b.putDouble(1.2);
            b.putDouble(1.3);
            b.putDouble(1.4);
            b.putDouble(1.5);
            byte[] a = b.array();
            salida.write(a);
            Thread.sleep(1000);
            conexion.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
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
