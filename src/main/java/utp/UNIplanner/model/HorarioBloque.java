package utp.UNIplanner.model;

import java.time.LocalTime;

public class HorarioBloque {
    private String dia;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private String nombre;

    public HorarioBloque() {}

    public HorarioBloque(String dia, LocalTime horaInicio, LocalTime horaFin, String nombre) {
        this.dia = dia;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.nombre = nombre;
    }

    public String getDia() { return dia; }
    public void setDia(String dia) { this.dia = dia; }

    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }

    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}
