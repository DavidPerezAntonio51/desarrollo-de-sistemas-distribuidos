package mx.ipn.escom;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * Hello world!
 *
 */
public class Main extends JFrame
{
    public static void main( String[] args ) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese el número de poligonos a generar: ");
        int n_poligonos = scanner.nextInt();
        List<PoligonoReg> poligonos = generarPoligonos(n_poligonos);
        Panel p = new Panel(poligonos,Panel.DIBUJA_ALEATORIOS);
        Main ventana = new Main();
        ventana.setVisible(true);
        ventana.add(p);
        p.repaint();
        for (int i = 0; i < n_poligonos-1; i++) {
            p.siguientePoligono();

            Thread.sleep(3000);
        }

        Collections.sort(poligonos);
        ventana.dispose();

        ventana.remove(p);
        ventana.setVisible(true);
        Panel p1 = new Panel(poligonos,Panel.DIBUJA_POR_AREA);
        ventana.add(p1);
        for (int i = 0; i < n_poligonos; i++) {
            p1.siguientePoligono();
            Thread.sleep(3000);
        }
    }
    public static List<PoligonoReg> generarPoligonos(int n){
        /*6 numero maximo de vertices (6+3-1 = 8), 3 numero minimo de lados*/
        List<PoligonoReg>poligonos = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            poligonos.add(new PoligonoReg((int) (Math.random() * 6) + 3));
        }
        return poligonos;
    }
    public Main(){
        setSize(800, 600);
        setLocationByPlatform(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
