package utp.UNIplanner.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Service;

import utp.UNIplanner.model.SeleccionResponse;
import utp.UNIplanner.model.Seccion;

@Service
public class SeleccionService {

    private final DemoService demoService;
    private final List<Seccion> seleccionados = new CopyOnWriteArrayList<>();

    public SeleccionService(DemoService demoService) {
        this.demoService = demoService;
    }

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
        List<String> conflictos = detectarChoques(total);
        mensajes.addAll(conflictos);

        if (conflictos.isEmpty()) {
            // Solo agregar si no hay conflictos
            seleccionados.addAll(nuevas);
        }

        return new SeleccionResponse(new ArrayList<>(seleccionados), mensajes);
    }

    public SeleccionResponse obtenerSeleccionados() {
        return new SeleccionResponse(new ArrayList<>(seleccionados), Collections.emptyList());
    }

    public void limpiarSeleccion() {
        seleccionados.clear();
    }

    private List<String> detectarChoques(List<Seccion> secciones) {
        List<String> conflictos = new ArrayList<>();

        for (int i = 0; i < secciones.size(); i++) {
            for (int j = i + 1; j < secciones.size(); j++) {
                Seccion a = secciones.get(i);
                Seccion b = secciones.get(j);
                for (String h1 : a.getHorario()) {
                    for (String h2 : b.getHorario()) {
                        if (h1.equals(h2)) {
                            conflictos.add("Choque entre " + a.getSeccion() + " y " + 
                                         b.getSeccion() + " en: " + h1);
                        }
                    }
                }
            }
        }
        return conflictos;
    }
}