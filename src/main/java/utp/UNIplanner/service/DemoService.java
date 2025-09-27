package utp.UNIplanner.service;

import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

import utp.UNIplanner.model.Curso;
import utp.UNIplanner.model.CursoResponse;
import utp.UNIplanner.model.Seccion;

@Service
public class DemoService {

    private CursoResponse data;
    private List<Seccion> seleccionActual = new ArrayList<>();

    // Indexados Hashmap
    private Map<Integer, List<Curso>> indexByCiclo = new HashMap<>();
    private Map<String, List<Curso>> indexByNombre = new HashMap<>();
    private Map<String, List<Curso>> indexByDocente = new HashMap<>();

    @PostConstruct
    public void loadData() {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream is = getClass().getResourceAsStream("/data/cursos.json")) {
            this.data = mapper.readValue(is, CursoResponse.class);
            buildIndexes();
        } catch (IOException e) {
            throw new RuntimeException("Error al cargar cursos.json", e);
        }
    }

    private void buildIndexes() {
        for (Curso c : data.getCursos()) {
            // ciclo index
            indexByCiclo.computeIfAbsent(c.getCiclo(), k -> new ArrayList<>()).add(c);

            // nombre index (en lowercase)
            String keyNombre = c.getNombre().toLowerCase();
            indexByNombre.computeIfAbsent(keyNombre, k -> new ArrayList<>()).add(c);

            // docente index
            for (Seccion s : c.getSecciones()) {
                String docenteKey = s.getDocente().toLowerCase();
                indexByDocente.computeIfAbsent(docenteKey, k -> new ArrayList<>()).add(c);
            }
        }
    }

    public CursoResponse getDemoCursos() {
        return data;
    }

    public CursoResponse getCursosPorCiclo(int ciclo) {
        // Original "streaming filter"
        /*
        List<Curso> filtrados = data.getCursos().stream()
                .filter(c -> c.getCiclo() == ciclo)
                .toList();
        return new CursoResponse(filtrados);
        */
        return new CursoResponse(indexByCiclo.getOrDefault(ciclo, Collections.emptyList()));
    }

    public CursoResponse getCursosPorNombre(String nombre) {
        String nombreLower = nombre.toLowerCase();

        // Original "streaming filter"
        /*
        List<Curso> filtrados = data.getCursos().stream()
                .filter(c -> c.getNombre().toLowerCase().contains(nombreLower))
                .toList();
        return new CursoResponse(filtrados);
        */

        // Optimizado para map y coincidencia exacta
        if (indexByNombre.containsKey(nombreLower)) {
            return new CursoResponse(indexByNombre.get(nombreLower));
        } else {
            List<Curso> filtrados = data.getCursos().stream()
                    .filter(c -> c.getNombre().toLowerCase().contains(nombreLower))
                    .toList();
            return new CursoResponse(filtrados);
        }
    }

    public CursoResponse buscarCursos(
            Optional<String> nombre,
            Optional<Integer> ciclo,
            Optional<String> docente,
            Optional<String> horario) {

        // Forma original "streaming":
        /*
        List<Curso> filtrados = data.getCursos().stream()
            .filter(c -> nombre.map(n -> c.getNombre().toLowerCase().contains(n.toLowerCase())).orElse(true))
            .filter(c -> ciclo.map(ci -> c.getCiclo() == ci).orElse(true))
            .filter(c -> docente.map(d ->
                    c.getSecciones().stream()
                        .anyMatch(s -> s.getDocente().toLowerCase().contains(d.toLowerCase()))
            ).orElse(true))
            .filter(c -> horario.map(h ->
                    c.getSecciones().stream()
                        .anyMatch(s -> s.getHorario().stream().anyMatch(hr -> hr.contains(h)))
            ).orElse(true))
            .toList();
        return new CursoResponse(filtrados);
        */

        // Alternativa usando indexes preindexados
        Set<Curso> result = new HashSet<>(data.getCursos());

        if (ciclo.isPresent()) {
            result.retainAll(indexByCiclo.getOrDefault(ciclo.get(), Collections.emptyList()));
        }
        if (nombre.isPresent()) {
            String key = nombre.get().toLowerCase();
            // substring search requires fallback to scan
            Set<Curso> byName = indexByNombre.containsKey(key)
                    ? new HashSet<>(indexByNombre.get(key))
                    : data.getCursos().stream()
                          .filter(c -> c.getNombre().toLowerCase().contains(key))
                          .collect(Collectors.toSet());
            result.retainAll(byName);
        }
        if (docente.isPresent()) {
            String key = docente.get().toLowerCase();
            Set<Curso> byDoc = indexByDocente.entrySet().stream()
                    .filter(e -> e.getKey().contains(key))
                    .flatMap(e -> e.getValue().stream())
                    .collect(Collectors.toSet());
            result.retainAll(byDoc);
        }
        if (horario.isPresent()) {
            String key = horario.get();
            Set<Curso> byHorario = data.getCursos().stream()
                    .filter(c -> c.getSecciones().stream()
                            .anyMatch(s -> s.getHorario().stream().anyMatch(hr -> hr.contains(key))))
                    .collect(Collectors.toSet());
            result.retainAll(byHorario);
        }

        return new CursoResponse(new ArrayList<>(result));
    }

    public CursoResponse buscarCursosPaginado(
            Optional<String> nombre,
            Optional<Integer> ciclo,
            Optional<String> docente,
            Optional<String> horario,
            int page,
            int size) {

        List<Curso> filtrados = buscarCursos(nombre, ciclo, docente, horario).getCursos();
        int fromIndex = Math.min(page * size, filtrados.size());
        int toIndex = Math.min(fromIndex + size, filtrados.size());
        return new CursoResponse(filtrados.subList(fromIndex, toIndex));
    }
}
