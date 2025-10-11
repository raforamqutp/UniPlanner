package utp.UNIplanner.model;

import java.time.LocalTime;

public class HorarioBloque {
    private String dia;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private String nombre;
    private String codigoSeccion;
    private String docente;
    private String nombreCurso;

    public HorarioBloque() {}

    public HorarioBloque(String dia, LocalTime horaInicio, LocalTime horaFin, String nombre) {
        this.dia = dia;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.nombre = nombre;
    }

    // Getters y Setters
    public String getDia() { return dia; }
    public void setDia(String dia) { this.dia = dia; }

    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }

    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCodigoSeccion() { return codigoSeccion; }
    public void setCodigoSeccion(String codigoSeccion) { this.codigoSeccion = codigoSeccion; }

    public String getDocente() { return docente; }
    public void setDocente(String docente) { this.docente = docente; }

    public String getNombreCurso() { return nombreCurso; }
    public void setNombreCurso(String nombreCurso) { this.nombreCurso = nombreCurso; }
}