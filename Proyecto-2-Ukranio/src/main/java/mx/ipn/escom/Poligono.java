package mx.ipn.escom;

public class Poligono{
    private Coordenada[] vertices;
    private int Tam;
    public Poligono(int Size) {
        this.Tam = Size;
        vertices = new Coordenada[Size];
    }

    public void añadeVertice(double x, double y) {
        Coordenada vertice = new Coordenada(x, y);
        for (int i = 0; i < Tam; i++) {
            if (vertices[i] == null) {
                vertices[i] = vertice;
                break;
            }
        }
        System.out.println(vertice);
    }

    public Coordenada[] getVertices() {
        return vertices;
    }

    @Override
    public String toString() {
        System.out.println("\nPoligono con vertices: \n ");
        String cadena = "";
        String aux = "";
        for (int i = 0; i < Tam; i++) {
            aux = aux + "" + vertices[i] + "\n";
        }

        cadena = cadena + aux;

        return cadena;
    }

}
