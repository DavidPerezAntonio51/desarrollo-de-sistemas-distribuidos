package mx.ipn.escom;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Panel extends JPanel {
    private List<PoligonoReg> poligonos;

    public static int DIBUJA_ALEATORIOS = 1;
    public static int DIBUJA_POR_AREA = 2;
    private int tipo_dibujo;
    private int index;

    public Panel(List<PoligonoReg> poligonos, int tipo_dibujo) {
        super();
        this.poligonos = poligonos;
        this.tipo_dibujo = tipo_dibujo;
    }

    public Panel() {
        super();
    }

    @Override
    public void paintComponent(Graphics g) {
        g.setColor(Color.blue);
        PoligonoReg poligono = poligonos.get(index);
        int MAX_WIDTH = super.getWidth();
        int MAX_HEIGHT = super.getHeight();
        Coordenada[] coordenadas = poligono.getVertices();
        switch (tipo_dibujo) {
            case 1:
                //sacamos un poligono de la lista
                double radio = poligono.getRadio();
                int centroX = (int) (Math.random() * (MAX_WIDTH - 2 * radio) + radio);
                int centroY = (int) (Math.random() * (MAX_HEIGHT - 2 * radio) + radio);
                Polygon polygon = new Polygon();
                for (int i = 0; i < coordenadas.length; i++) {
                    polygon.addPoint((int) coordenadas[i].abcisa(), (int) coordenadas[i].ordenada());
                }
                polygon.translate(centroX, centroY);
                g.drawPolygon(polygon);

                break;
            case 2:
                int centroX1 = super.getWidth() / 2;
                int centroY1 = super.getHeight() / 2;
                Polygon polygon2 = new Polygon();
                System.out.println(poligono.getArea());
                for (int i = 0; i < coordenadas.length; i++) {
                    polygon2.addPoint((int) coordenadas[i].abcisa(), (int) coordenadas[i].ordenada());
                }
                polygon2.translate(centroX1, centroY1);
                g.drawPolygon(polygon2);
                break;
        }
    }

    public void siguientePoligono() {
        index++;
        if (index >= poligonos.size()) {
            index = 0;
        }
        repaint();
    }

    public void cambiarDibujo() {

    }
}