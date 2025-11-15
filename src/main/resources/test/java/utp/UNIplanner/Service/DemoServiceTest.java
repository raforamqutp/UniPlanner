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
        assertFalse(response.getCursos().isEmpty(), "Debería encontrar cursos del ciclo 1");
        for (Curso c : response.getCursos()) {
            assertEquals(1, c.getCiclo(), "Todos deben pertenecer al ciclo 1");
        }
    }

    @Test
    void testGetCursosPorNombreExacto() {
        // Buscamos un nombre exacto que SÍ existe
        CursoResponse response = demoService.getCursosPorNombre("Matemática I"); 
        assertFalse(response.getCursos().isEmpty());
        assertEquals("Matemática I", response.getCursos().get(0).getNombre());
    }

    @Test
    void testGetCursosPorNombreSubstring() {
        CursoResponse response = demoService.getCursosPorNombre("inte"); // "integrador"
        assertFalse(response.getCursos().isEmpty());
        assertTrue(response.getCursos().stream()
                .anyMatch(c -> c.getNombre().toLowerCase().contains("inte")));
    }

    @Test
    void testBuscarCursosPorDocente() {
        // Buscamos "Ticona" (RAMIREZ TICONA,JUAN) en lugar de "García"
        CursoResponse response = demoService.buscarCursos(
                Optional.empty(),
                Optional.empty(),
                Optional.of("Ticona"), 
                Optional.empty()
        );
        assertFalse(response.getCursos().isEmpty(), "Debería encontrar cursos por docente 'Ticona'");
        assertTrue(response.getCursos().stream()
                .allMatch(c -> c.getSecciones().stream()
                        .anyMatch(s -> s.getDocente().toLowerCase().contains("ticona"))));
    }

    @Test
    void testBuscarCursosPorHorario() {
        // Buscamos "08:45" (Sección 7272) en lugar de "08:00"
        CursoResponse response = demoService.buscarCursos(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.of("08:45") 
        );
        assertFalse(response.getCursos().isEmpty(), "Debería encontrar cursos con horario '08:45'");
        assertTrue(response.getCursos().stream()
                .allMatch(c -> c.getSecciones().stream()
                        .anyMatch(s -> s.getHorario().stream().anyMatch(h -> h.contains("08:45")))));
    }

    @Test
    void testBuscarCursosCombinado() {
        // Buscamos "integrador" (Curso integrador I) en ciclo 6 con docente "nieto"
        CursoResponse response = demoService.buscarCursos(
                Optional.of("integrador"),
                Optional.of(6),
                Optional.of("nieto"), // (NIETO VALENCIA,RENE ALONSO)
                Optional.empty()
        );
        
        assertFalse(response.getCursos().isEmpty(), "Debería encontrar la combinación");
        for (Curso c : response.getCursos()) {
            assertEquals(6, c.getCiclo());
            assertTrue(c.getNombre().toLowerCase().contains("integrador"));
            assertTrue(c.getSecciones().stream()
                .anyMatch(s -> s.getDocente().toLowerCase().contains("nieto")));
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