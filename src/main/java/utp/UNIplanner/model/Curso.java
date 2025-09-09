package utp.UNIplanner.model;

import java.util.List;
import utp.UNIplanner.model.Seccion;

public class Curso {
    private String nombre;
    private int ciclo;
    private List<Seccion> secciones;

    public Curso() {
    }

    public Curso(String nombre, int ciclo, List<Seccion> secciones) {
        this.nombre = nombre;
        this.ciclo = ciclo;
        this.secciones = secciones;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getCiclo() { return ciclo; }
    public void setCiclo(int ciclo) { this.ciclo = ciclo; }

    public List<Seccion> getSecciones() { return secciones; }
    public void setSecciones(List<Seccion> secciones) { this.secciones = secciones; }
}