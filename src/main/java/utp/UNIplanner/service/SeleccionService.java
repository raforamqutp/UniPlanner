package utp.UNIplanner.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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

    // Lógica de choques mejorda
    private List<String> detectarChoques(List<Seccion> secciones) {
        List<String> conflictos = new ArrayList<>();
        Map<String, Set<String>> horarioMap = new HashMap<>();
        
        for (Seccion seccion : secciones) {
            for (String horarioStr : seccion.getHorario()) {
                String[] partes = horarioStr.split(" : ");
                if (partes.length == 2) {
                    String claveHorario = partes[0].trim() + "_" + partes[1].trim();
                    if (horarioMap.containsKey(claveHorario)) {
                        horarioMap.get(claveHorario).add(seccion.getSeccion());
                    } else {
                        Set<String> seccionesEnHorario = new HashSet<>();
                        seccionesEnHorario.add(seccion.getSeccion());
                        horarioMap.put(claveHorario, seccionesEnHorario);
                    }
                }
            }
        }
        
        // Identificar conflictos
        horarioMap.forEach((horario, seccionesEnHorario) -> {
            if (seccionesEnHorario.size() > 1) {
                conflictos.add("Conflicto en horario " + horario.replace("_", " : ") + 
                              " entre secciones: " + String.join(", ", seccionesEnHorario));
            }
        });
        
        return conflictos;
    }
}