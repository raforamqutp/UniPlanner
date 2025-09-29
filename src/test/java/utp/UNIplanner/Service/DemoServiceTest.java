package utp.UNIplanner.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import utp.UNIplanner.model.Curso;
import utp.UNIplanner.model.CursoResponse;
import utp.UNIplanner.service.DemoService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DemoServiceTest {

    private DemoService demoService;

    @BeforeEach
    void setUp() {
        demoService = new DemoService();
        demoService.loadData(); // carga cursos.json y construye los índices
    }

    @Test
    void testLoadData() {
        CursoResponse response = demoService.getDemoCursos();
        assertNotNull(response);
        assertFalse(response.getCursos().isEmpty(), "Debe cargar cursos.json");
    }

    @Test
    void testGetCursosPorCiclo() {
        CursoResponse response = demoService.getCursosPorCiclo(1);
        for (Curso c : response.getCursos()) {
            assertEquals(1, c.getCiclo(), "Todos deben pertenecer al ciclo 1");
        }
    }

    @Test
    void testGetCursosPorNombreExacto() {
        // asume que existe un curso llamado "Integrador"
        CursoResponse response = demoService.getCursosPorNombre("Integrador");
        assertFalse(response.getCursos().isEmpty());
        assertTrue(response.getCursos().stream()
                .anyMatch(c -> c.getNombre().equalsIgnoreCase("Integrador")));
    }

    @Test
    void testGetCursosPorNombreSubstring() {
        CursoResponse response = demoService.getCursosPorNombre("inte");
        assertFalse(response.getCursos().isEmpty());
        assertTrue(response.getCursos().stream()
                .anyMatch(c -> c.getNombre().toLowerCase().contains("inte")));
    }

    @Test
    void testBuscarCursosPorDocente() {
        CursoResponse response = demoService.buscarCursos(
                Optional.empty(),
                Optional.empty(),
                Optional.of("García"),
                Optional.empty()
        );
        assertTrue(response.getCursos().stream()
                .allMatch(c -> c.getSecciones().stream()
                        .anyMatch(s -> s.getDocente().toLowerCase().contains("tico"))));
    }

    @Test
    void testBuscarCursosPorHorario() {
        CursoResponse response = demoService.buscarCursos(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.of("08:00")
        );
        assertTrue(response.getCursos().stream()
                .allMatch(c -> c.getSecciones().stream()
                        .anyMatch(s -> s.getHorario().stream().anyMatch(h -> h.contains("08:00")))));
    }

    @Test
    void testBuscarCursosCombinado() {
        CursoResponse response = demoService.buscarCursos(
                Optional.of("inte"),
                Optional.of(1),
                Optional.of("Ticona"),
                Optional.empty()
        );
        for (Curso c : response.getCursos()) {
            assertEquals(1, c.getCiclo());
            assertTrue(c.getNombre().toLowerCase().contains("inte"));
        }
    }

    @Test
    void testBuscarCursosPaginado() {
        CursoResponse response = demoService.buscarCursosPaginado(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                0,
                2
        );
        assertTrue(response.getCursos().size() <= 2);
    }
}
