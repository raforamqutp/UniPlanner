package utp.UNIplanner.model;

import java.util.List;
import utp.UNIplanner.model.Seccion;

public class Curso {
    private String id;
    private String nombre;
    private int ciclo;
    private List<Seccion> secciones;
    private List<String> prerequisites;

    public Curso() {
    }

    public Curso(String id, String nombre, int ciclo, List<Seccion> secciones, List<String> prerequisites) {
        this.id = id;
        this.nombre = nombre;
        this.ciclo = ciclo;
        this.secciones = secciones;
        this.prerequisites = prerequisites;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getCiclo() { return ciclo; }
    public void setCiclo(int ciclo) { this.ciclo = ciclo; }

    public List<Seccion> getSecciones() { return secciones; }
    public void setSecciones(List<Seccion> secciones) { this.secciones = secciones; }
    
    public List<String> getPrerequisites() { return prerequisites; }
    public void setPrerequisites(List<String> prerequisites) { this.prerequisites = prerequisites; }
}