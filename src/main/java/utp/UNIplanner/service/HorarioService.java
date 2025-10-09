package utp.UNIplanner.service;

import org.springframework.stereotype.Service;
import utp.UNIplanner.model.HorarioBloque;
import utp.UNIplanner.model.HorarioResponse;

import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class HorarioService {

    private final List<HorarioBloque> bloques = new CopyOnWriteArrayList<>();

    public HorarioService() {
        bloques.add(new HorarioBloque("Lunes", LocalTime.of(8, 0), LocalTime.of(9, 0), "Curso Prueba 1"));
        bloques.add(new HorarioBloque("Martes", LocalTime.of(10, 0), LocalTime.of(11, 30), "Curso Prueba 2"));
    }

    public HorarioResponse obtenerHorario() {
        return new HorarioResponse(new ArrayList<>(bloques));
    }

    public void agregarBloque(HorarioBloque bloque) {
        bloques.add(bloque);
    }

    public void eliminarBloque(String dia, LocalTime inicio, LocalTime fin) {
        bloques.removeIf(b -> b.getDia().equalsIgnoreCase(dia)
                && b.getHoraInicio().equals(inicio)
                && b.getHoraFin().equals(fin));
    }

    public void limpiar() {
        bloques.clear();
    }
}
