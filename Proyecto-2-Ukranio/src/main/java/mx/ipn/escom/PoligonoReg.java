package mx.ipn.escom;

public class PoligonoReg extends Poligono implements Comparable<PoligonoReg>{
    private int num_vertices;
    private double area;
    private double alfa;
    private double radio;
    public PoligonoReg(int num_vertices){
        super(num_vertices);
        /*calculamos alfa*/
        alfa = 360.0 / num_vertices;
        this.num_vertices = num_vertices;
        int centroX = 0;
        int centroY = 0;
        /*tamaño de la circunferencia aleatoria  entre 25 y 150*/
        radio = 25 + (Math.random() * 125);
        for (int i = 0; i < num_vertices; i++) {
            int x = (int)(centroX + radio * Math.cos(i * 2 * Math.PI / num_vertices - Math.PI / 2));
            int y = (int)(centroY + radio * Math.sin(i * 2 * Math.PI / num_vertices - Math.PI / 2));
            super.añadeVertice(x,y);
        }
        double anguloRad = Math.toRadians(alfa);
        //calculamos el apotema
        double apotema = radio * Math.cos(anguloRad/2);
        //calculamos el perimetro
        double perimetro = num_vertices * Math.sqrt(2 * radio * apotema);
        //calculamos el area
        area = perimetro * apotema / 2;
    }

    public double getArea() {
        return area;
    }

    @Override
    public int compareTo(PoligonoReg o) {
        return Double.compare(this.area, o.getArea());
    }

    public double getRadio() {
        return radio;
    }
}
