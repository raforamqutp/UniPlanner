package utp.UNIplanner.model;

import java.util.List;

public class Seccion {
    private String seccion;
    private String docente;
    private List<String> horario;

    public Seccion() {
    }

    public Seccion(String seccion, String docente, List<String> horario) {
        this.seccion = seccion;
        this.docente = docente;
        this.horario = horario;
    }

    public String getSeccion() { return seccion; }
    public void setSeccion(String seccion) { this.seccion = seccion; }

    public String getDocente() { return docente; }
    public void setDocente(String docente) { this.docente = docente; }

    public List<String> getHorario() { return horario; }
    public void setHorario(List<String> horario) { this.horario = horario; }
}
