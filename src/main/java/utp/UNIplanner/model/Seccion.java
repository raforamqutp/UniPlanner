package utp.UNIplanner.model;

import java.util.List;

public class Seccion {
    private String seccion;
    private String docente;
    private List<String> horario;

    public Seccion(String seccion, String docente, List<String> horario) {
        this.seccion = seccion;
        this.docente = docente;
        this.horario = horario;
    }

    public String getSeccion() { return seccion; }
    public String getDocente() { return docente; }
    public List<String> getHorario() { return horario; }
}
