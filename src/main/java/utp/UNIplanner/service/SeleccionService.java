package utp.UNIplanner.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Service;

import utp.UNIplanner.controller.SeleccionResponse;
import utp.UNIplanner.model.Seccion;


@Service
public class SeleccionService {

    private final DemoService demoService;
    private final List<Seccion> seleccionados = new CopyOnWriteArrayList<>();

    public SeleccionService(DemoService demoService) {
        this.demoService = demoService;
    }

    // Seleccionar secciones por sus códigos
    public SeleccionResponse seleccionarSecciones(List<String> codigos) {
        List<Seccion> nuevas = new ArrayList<>();
        List<String> mensajes = new ArrayList<>();

        // Buscar secciones en los cursos existentes
        for (String codigo : codigos) {
            Optional<Seccion> seccion = demoService.getDemoCursos().getCursos().stream()
                    .flatMap(c -> c.getSecciones().stream())
                    .filter(s -> s.getSeccion().equals(codigo))
                    .findFirst();

            if (seccion.isPresent()) {
                nuevas.add(seccion.get());
            } else {
                mensajes.add("No se encontró la sección con código: " + codigo);
            }
        }

        // Detectar choques de horario entre los ya seleccionados + nuevos
        List<Seccion> total = new ArrayList<>(seleccionados);
        total.addAll(nuevas);
        mensajes.addAll(detectarChoques(total));

        // Agregar igualmente las secciones nuevas a seleccionados
        seleccionados.addAll(nuevas);

        return new SeleccionResponse(new ArrayList<>(seleccionados), mensajes);
    }

    // Retorna todos los seleccionados actuales
    public SeleccionResponse obtenerSeleccionados() {
        return new SeleccionResponse(new ArrayList<>(seleccionados), Collections.emptyList());
    }

    // Limpia la selección
    public void limpiarSeleccion() {
        seleccionados.clear();
    }

    // Detección simple de choques basada en igualdad exacta de cadenas de horario
    private List<String> detectarChoques(List<Seccion> secciones) {
        List<String> conflictos = new ArrayList<>();

        for (int i = 0; i < secciones.size(); i++) {
            for (int j = i + 1; j < secciones.size(); j++) {
                Seccion a = secciones.get(i);
                Seccion b = secciones.get(j);
                for (String h1 : a.getHorario()) {
                    for (String h2 : b.getHorario()) {
                        if (h1.equals(h2)) {
                            conflictos.add("Choque entre sección " +
                                    a.getSeccion() +
                                    " y sección " +
                                    b.getSeccion() +
                                    " en horario: " + h1);
                        }
                    }
                }
            }
        }
        return conflictos;
    }
}