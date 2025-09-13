package utp.UNIplanner.service;

import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import utp.UNIplanner.model.Curso;
import utp.UNIplanner.model.CursoResponse;

@Service // Marca esta clase como un servicio de Spring (para inyección de dependencias)
public class DemoService {

    private CursoResponse data; // Contendrá los datos cargados desde el archivo JSON

    @PostConstruct // Método que se ejecuta automáticamente después de crear la instancia del servicio
    public void loadData() {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream is = getClass().getResourceAsStream("/data/cursos.json")) {
            // Carga el contenido del JSON a un objeto CursoResponse
            this.data = mapper.readValue(is, CursoResponse.class);
        } catch (IOException e) {
            // Lanza excepción si ocurre un error al leer el archivo
            throw new RuntimeException("Error al cargar cursos.json", e);
        }
    }

    // Retorna todos los cursos cargados del JSON
    public CursoResponse getDemoCursos() {
        return data;
    }

    // Filtra y retorna los cursos que pertenecen a un ciclo específico
    public CursoResponse getCursosPorCiclo(int ciclo) {
        List<Curso> filtrados = data.getCursos().stream()
                .filter(c -> c.getCiclo() == ciclo)
                .toList();
        return new CursoResponse(filtrados);
    }

    // Filtra y retorna los cursos cuyo nombre contiene el texto proporcionado (ignorando mayúsculas/minúsculas)
    public CursoResponse getCursosPorNombre(String nombre) {
        String nombreLower = nombre.toLowerCase();
        List<Curso> filtrados = data.getCursos().stream()
                .filter(c -> c.getNombre().toLowerCase().contains(nombreLower))
                .toList();
        return new CursoResponse(filtrados);
    }
}
