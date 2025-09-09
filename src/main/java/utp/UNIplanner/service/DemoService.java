package utp.UNIplanner.service;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import utp.UNIplanner.model.Curso;
import utp.UNIplanner.model.CursoResponse;
import utp.UNIplanner.model.Seccion;

@Service
public class DemoService {

    private CursoResponse data;

    @PostConstruct
    public void loadData() {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream is = getClass().getResourceAsStream("/data/cursos.json")) {
            this.data = mapper.readValue(is, CursoResponse.class);
        } catch (IOException e) {
            throw new RuntimeException("Error al cargar cursos.json", e);
        }
    }

    public CursoResponse getDemoCursos() {
        return data;
    }

    public CursoResponse getCursosPorCiclo(int ciclo) {
        List<Curso> filtrados = data.getCursos().stream()
                .filter(c -> c.getCiclo() == ciclo)
                .toList();
        return new CursoResponse(filtrados);
    }

    public CursoResponse getCursosPorNombre(String nombre) {
        String nombreLower = nombre.toLowerCase();
        List<Curso> filtrados = data.getCursos().stream()
                .filter(c -> c.getNombre().toLowerCase().contains(nombreLower))
                .toList();
        return new CursoResponse(filtrados);
    }
}