package utp.UNIplanner.Controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import utp.UNIplanner.controller.CursoViewController;
import utp.UNIplanner.model.Curso;
import utp.UNIplanner.model.CursoResponse;
import utp.UNIplanner.service.DemoService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
class CursoViewControllerTest {

    @Mock
    private DemoService demoService;

    @InjectMocks
    private CursoViewController cursoViewController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(cursoViewController).build();
    }

    @Test
    void testRedirectToCursos() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/cursos"));
    }

    @Test
    void testVerCursos() throws Exception {
        Curso curso = new Curso();
        curso.setNombre("Curso de Prueba");
        List<Curso> cursos = Arrays.asList(curso);
        CursoResponse cursoResponse = new CursoResponse(cursos);

        when(demoService.getDemoCursos()).thenReturn(cursoResponse);

        mockMvc.perform(get("/cursos"))
                .andExpect(status().isOk())
                .andExpect(view().name("cursos"))
                .andExpect(model().attribute("titulo", "Todos los Cursos"))
                .andExpect(model().attribute("cursos", cursos));
    }

    @Test
    void testVerCursosPorCiclo() throws Exception {
        int ciclo = 1;
        Curso curso = new Curso();
        curso.setNombre("Curso de Ciclo 1");
        List<Curso> cursos = Arrays.asList(curso);
        CursoResponse cursoResponse = new CursoResponse(cursos);

        when(demoService.getCursosPorCiclo(ciclo)).thenReturn(cursoResponse);

        mockMvc.perform(get("/cursos/ciclo/{ciclo}", ciclo))
                .andExpect(status().isOk())
                .andExpect(view().name("cursos"))
                .andExpect(model().attribute("titulo", "Cursos del Ciclo " + ciclo))
                .andExpect(model().attribute("cursos", cursos));
    }

    @Test
    void testVerCursosPorNombre() throws Exception {
        String nombre = "Curso de Prueba";
        Curso curso = new Curso();
        curso.setNombre(nombre);
        List<Curso> cursos = Arrays.asList(curso);
        CursoResponse cursoResponse = new CursoResponse(cursos);

        when(demoService.getCursosPorNombre(nombre)).thenReturn(cursoResponse);

        mockMvc.perform(get("/cursos/nombre/{nombre}", nombre))
                .andExpect(status().isOk())
                .andExpect(view().name("cursos"))
                .andExpect(model().attribute("titulo", "Cursos que coinciden con: \"" + nombre + "\""))
                .andExpect(model().attribute("cursos", cursos));
    }

    @Test
    void testBuscarCursos() throws Exception {
        String nombre = "Curso de Prueba";
        int ciclo = 1;
        String docente = "Docente Prueba";
        String horario = "Lunes";
        Curso curso = new Curso();
        curso.setNombre(nombre);
        List<Curso> cursos = Arrays.asList(curso);
        CursoResponse cursoResponse = new CursoResponse(Collections.emptyList()); 

        when(demoService.buscarCursosPaginado(
                Optional.of(nombre),
                Optional.of(ciclo),
                Optional.of(docente),
                Optional.of(horario),
                0,
                10
        )).thenReturn(cursoResponse);

        mockMvc.perform(get("/cursos/buscar")
                        .param("nombre", nombre)
                        .param("ciclo", String.valueOf(ciclo))
                        .param("docente", docente)
                        .param("horario", horario)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(view().name("cursos"))
                .andExpect(model().attribute("titulo", "Resultados de Búsqueda"))
                .andExpect(model().attribute("cursos", Collections.emptyList()));
    }

    @Test
    void testVerAvanceDemo() throws Exception {
         mockMvc.perform(get("/cursos/avance"))
            .andExpect(status().isOk())
            .andExpect(view().name("cursos-avance-demo"));
    }
}