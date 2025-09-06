package utp.UNIplanner.model;

import java.util.List;
import utp.UNIplanner.model.Seccion;

public class Curso {
    private String nombre;
    private int ciclo;
    private List<Seccion> secciones;

    public Curso(String nombre, int ciclo, List<Seccion> secciones) {
        this.nombre = nombre;
        this.ciclo = ciclo;
        this.secciones = secciones;
    }

    public String getNombre() { return nombre; }
    public int getCiclo() { return ciclo; }
    public List<Seccion> getSecciones() { return secciones; }
}