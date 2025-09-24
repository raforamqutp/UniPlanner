package utp.UNIplanner.service;

import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import utp.UNIplanner.model.Curso;
import utp.UNIplanner.model.CursoResponse;
import utp.UNIplanner.model.Seccion;

@Service // Indica que esta clase es un servicio de Spring y debe ser gestionada por el contenedor de dependencias.
public class DemoService {

    private CursoResponse data; // Esta variable almacenará los datos de los cursos cargados desde un archivo JSON.
    private List<Seccion> seleccionActual = new ArrayList()<>();


    @PostConstruct // Este método se ejecuta automáticamente después de que el servicio es inicializado.
    public void loadData() {
        ObjectMapper mapper = new ObjectMapper(); // Usamos ObjectMapper para convertir el JSON en objetos Java.
        
        try (InputStream is = getClass().getResourceAsStream("/data/cursos.json")) { 
            // Abre el archivo JSON y lo lee como un InputStream desde el classpath.
            this.data = mapper.readValue(is, CursoResponse.class); // Mapea el JSON a un objeto CursoResponse.
        } catch (IOException e) {
            // Si hay un error al leer el archivo JSON, lanzamos una excepción con un mensaje de error.
            throw new RuntimeException("Error al cargar cursos.json", e);
        }
    }

    // Este método retorna todos los cursos cargados desde el JSON
    public CursoResponse getDemoCursos() {
        return data; // Devuelve el objeto CursoResponse que contiene todos los cursos.
    }

    // Este método retorna los cursos filtrados por un ciclo específico
    public CursoResponse getCursosPorCiclo(int ciclo) {
        List<Curso> filtrados = data.getCursos().stream()
                .filter(c -> c.getCiclo() == ciclo) // Filtra los cursos que coinciden con el ciclo pasado como parámetro.
                .toList();
        return new CursoResponse(filtrados); // Devuelve un nuevo CursoResponse con los cursos filtrados.
    }

    // Este método filtra los cursos cuyo nombre contiene el texto especificado (sin importar mayúsculas/minúsculas)
    public CursoResponse getCursosPorNombre(String nombre) {
        String nombreLower = nombre.toLowerCase(); // Convertimos el nombre a minúsculas para hacer la comparación insensible a mayúsculas/minúsculas.
        
        List<Curso> filtrados = data.getCursos().stream()
                .filter(c -> c.getNombre().toLowerCase().contains(nombreLower)) // Filtra los cursos cuyo nombre contiene el texto.
                .toList();
        return new CursoResponse(filtrados); // Devuelve un nuevo CursoResponse con los cursos filtrados por nombre.
    }

    // Este método realiza una búsqueda avanzada, permitiendo filtrar los cursos por nombre, ciclo, docente y horario.
    public CursoResponse buscarCursos(
            Optional<String> nombre, // Parámetro opcional para el nombre del curso.
            Optional<Integer> ciclo, // Parámetro opcional para el ciclo del curso.
            Optional<String> docente, // Parámetro opcional para el nombre del docente.
            Optional<String> horario) { // Parámetro opcional para el horario del curso.

        // Filtra los cursos según los parámetros proporcionados, permitiendo búsquedas parciales.
        List<Curso> filtrados = data.getCursos().stream()
            .filter(c -> nombre.map(n -> c.getNombre().toLowerCase().contains(n.toLowerCase())).orElse(true)) // Filtra por nombre si está presente.
            .filter(c -> ciclo.map(ci -> c.getCiclo() == ci).orElse(true)) // Filtra por ciclo si está presente.
            .filter(c -> docente.map(d -> 
                    c.getSecciones().stream()
                        .anyMatch(s -> s.getDocente().toLowerCase().contains(d.toLowerCase())) // Filtra por docente si está presente.
            ).orElse(true))
            .filter(c -> horario.map(h -> 
                    c.getSecciones().stream()
                        .anyMatch(s -> s.getHorario().stream().anyMatch(hr -> hr.contains(h))) // Filtra por horario si está presente.
            ).orElse(true))
            .toList();

        return new CursoResponse(filtrados); // Devuelve un nuevo CursoResponse con los cursos filtrados.
    }
    
    // Este método realiza una búsqueda avanzada con soporte para paginación.
    public CursoResponse buscarCursosPaginado(
            Optional<String> nombre,
            Optional<Integer> ciclo,
            Optional<String> docente,
            Optional<String> horario,
            int page, // Página de los resultados.
            int size) { // Número de resultados por página.

        // Primero filtramos los cursos según los parámetros proporcionados.
        List<Curso> filtrados = buscarCursos(nombre, ciclo, docente, horario).getCursos();

        // Calculamos los índices de inicio y fin para la paginación.
        int fromIndex = Math.min(page * size, filtrados.size()); // Calcula el índice de inicio de la página.
        int toIndex = Math.min(fromIndex + size, filtrados.size()); // Calcula el índice final de la página.

        // Devuelve los cursos correspondientes a la página solicitada.
        return new CursoResponse(filtrados.subList(fromIndex, toIndex));
    }
}
