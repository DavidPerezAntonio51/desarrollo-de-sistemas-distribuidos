package mx.ipn.escom;

import java.io.Serializable;

public class ResultadoMatriz implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private float[][] matrizC;
    private int filas;
    private int columnas;

    public ResultadoMatriz(int id, float[][] matrizC, int filas, int columnas) {
        this.id = id;
        this.matrizC = matrizC;
        this.filas = filas;
        this.columnas = columnas;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float[][] getMatrizC() {
        return matrizC;
    }

    public void setMatrizC(float[][] matrizC) {
        this.matrizC = matrizC;
    }

    public int getFilas() {
        return filas;
    }

    public void setFilas(int filas) {
        this.filas = filas;
    }

    public int getColumnas() {
        return columnas;
    }

    public void setColumnas(int columnas) {
        this.columnas = columnas;
    }
}
