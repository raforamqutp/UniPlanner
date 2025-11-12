package utp.UNIplanner.service;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import utp.UNIplanner.model.Curso;
import utp.UNIplanner.model.CursoResponse;
import utp.UNIplanner.model.SeleccionResponse;
import utp.UNIplanner.model.Seccion;

@Service
public class SeleccionService {

    private final DemoService demoService;
    private final List<Seccion> seleccionados = new CopyOnWriteArrayList<>();

    private record Intervalo(
        String dia,
        LocalTime inicio,
        LocalTime fin,
        String seccionId,
        String horarioRaw
    ) {
        public boolean seTraslapaCon(Intervalo otro) {
            return this.inicio.isBefore(otro.fin) && otro.inicio.isBefore(this.fin);
        }
    }


    public SeleccionService(DemoService demoService) {
        this.demoService = demoService;
    }

    // Ahorea reemplaza en vez de añadir
    public SeleccionResponse seleccionarSecciones(List<String> codigos) {
        // 'codigos' AHORA ES LA LISTA COMPLETA Y DEFINITIVA
        List<Seccion> nuevas = new ArrayList<>();
        List<String> mensajes = new ArrayList<>();

        // 1. Buscar secciones en los cursos existentes
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

        // 2. Detectar choques de horario SOLO en la nueva lista
        List<String> conflictos = detectarChoques(nuevas);
        mensajes.addAll(conflictos);

        // 3. Si no hay conflictos, REEMPLAZAR la lista antigua
        if (conflictos.isEmpty()) {
            this.seleccionados.clear(); // BORRAR LA LISTA ANTIGUA
            this.seleccionados.addAll(nuevas); // AÑADIR LA NUEVA LISTA COMPLETA
        }

        // Devolvemos la lista actual (la nueva si no hubo error, o la vieja si hubo error)
        return new SeleccionResponse(new ArrayList<>(this.seleccionados), mensajes);
    }

    public SeleccionResponse obtenerSeleccionados() {
        return new SeleccionResponse(new ArrayList<>(seleccionados), Collections.emptyList());
    }

    public void limpiarSeleccion() {
        seleccionados.clear();
    }

    private Optional<Intervalo> parseHorarioString(String horarioStr, String seccionId) {
        try {
            String[] partesDiaHora = horarioStr.split(" : ");
            if (partesDiaHora.length != 2) return Optional.empty();

            String dia = normalizarDia(partesDiaHora[0].trim());
            String[] partesTiempos = partesDiaHora[1].split(" - ");
            if (partesTiempos.length != 2) return Optional.empty();

            LocalTime inicio = LocalTime.parse(partesTiempos[0].trim());
            LocalTime fin = LocalTime.parse(partesTiempos[1].trim());

            if (inicio.equals(fin) || inicio.isAfter(fin)) {
                return Optional.empty();
            }

            return Optional.of(new Intervalo(dia, inicio, fin, seccionId, horarioStr));
        
        } catch (DateTimeParseException e) {
            System.err.println("Error parseando horario: " + horarioStr);
            return Optional.empty();
        }
    }

    private String normalizarDia(String dia) {
        switch (dia.toLowerCase()) {
            case "lunes": return "Lunes";
            case "martes": return "Martes";
            case "miercoles":
            case "miércoles": return "Miercoles";
            case "jueves": return "Jueves";
            case "viernes": return "Viernes";
            case "sabado":
            case "sábado": return "Sabado";
            case "domingo": return "Domingo";
            default: return dia;
        }
    }

    private List<String> detectarChoques(List<Seccion> secciones) {
        List<String> conflictos = new ArrayList<>();
        
        List<Intervalo> todosLosIntervalos = new ArrayList<>();
        for (Seccion seccion : secciones) {
            for (String horarioStr : seccion.getHorario()) {
                parseHorarioString(horarioStr, seccion.getSeccion())
                    .ifPresent(todosLosIntervalos::add);
            }
        }

        Map<String, List<Intervalo>> mapaPorDia = todosLosIntervalos.stream()
            .collect(Collectors.groupingBy(Intervalo::dia));

        for (Map.Entry<String, List<Intervalo>> entry : mapaPorDia.entrySet()) {
            String dia = entry.getKey();
            List<Intervalo> intervalosDelDia = entry.getValue();

            if (intervalosDelDia.size() < 2) {
                continue;}
            for (int i = 0; i < intervalosDelDia.size(); i++) {
                for (int j = i + 1; j < intervalosDelDia.size(); j++) {
                    
                    Intervalo a = intervalosDelDia.get(i);
                    Intervalo b = intervalosDelDia.get(j);

                    if (a.seTraslapaCon(b)) {
                        String msg = String.format(
                            "Conflicto de horario el %s: Sección %s (%s) se traslapa con Sección %s (%s)",
                            dia,
                            a.seccionId(), a.horarioRaw(),
                            b.seccionId(), b.horarioRaw()
                        );
                        conflictos.add(msg);
                    }
                }
            }
        }
        return conflictos;
    }
}