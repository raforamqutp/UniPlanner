package utp.UNIplanner.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import utp.UNIplanner.controller.DemoController;
import utp.UNIplanner.model.Curso;
import utp.UNIplanner.model.CursoResponse;
import utp.UNIplanner.service.DemoService;
import utp.UNIplanner.service.SeleccionService;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DemoController.class)
class DemoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DemoService demoService;

    @MockBean
    private SeleccionService seleccionService;

    @Test
    void testGetCursosPorNombre() throws Exception {
        // 1. Crear una respuesta simulada con datos reales
        Curso cursoMock = new Curso("100000I0N2", "Matemática I", 1, Collections.emptyList(), Collections.emptyList());
        CursoResponse mockResponse = new CursoResponse(List.of(cursoMock));
        
        // 2. Simular el servicio
        when(demoService.getCursosPorNombre("math")).thenReturn(mockResponse);

        // 3. Verificar el JSON path correcto
        mockMvc.perform(get("/api/cursos/nombre/math"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cursos").isArray()) // Verificar que 'cursos' existe
                .andExpect(jsonPath("$.cursos[0].nombre").value("Matemática I")); // Verificar el dato
    }

    @Test
    void testBuscarCursosByCiclo() throws Exception {
        Curso cursoMock = new Curso("100000I0N6", "Cálculo I", 3, Collections.emptyList(), Collections.emptyList());
        CursoResponse mockResponse = new CursoResponse(List.of(cursoMock));
        
        when(demoService.buscarCursosPaginado(
                Optional.empty(),
                Optional.of(3),
                Optional.empty(),
                Optional.empty(),
                0,
                10
        )).thenReturn(mockResponse);

        mockMvc.perform(get("/api/cursos/buscar")
                        .param("ciclo", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cursos[0].ciclo").value(3)); // Verificar el ciclo
    }

    @Test
    void testBuscarCursosByNombreAndDocente() throws Exception {
        Curso cursoMock = new Curso("100000S61T", "Herramientas de desarrollo", 7, Collections.emptyList(), Collections.emptyList());
        CursoResponse mockResponse = new CursoResponse(List.of(cursoMock));
        
        when(demoService.buscarCursosPaginado(
                Optional.of("Herramientas"),
                Optional.empty(),
                Optional.of("Ticona"),
                Optional.empty(),
                0,
                10
        )).thenReturn(mockResponse);

        mockMvc.perform(get("/api/cursos/buscar")
                        .param("nombre", "Herramientas")
                        .param("docente", "Ticona"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cursos[0].nombre").value("Herramientas de desarrollo"));
    }

    @Test
    void testBuscarCursosWithPagination() throws Exception {
        // (Este test estaba conceptualmente bien, solo se asegura que la respuesta sea un arreglo)
        CursoResponse mockResponse = new CursoResponse(Collections.emptyList()); // Página 2 puede estar vacía
        when(demoService.buscarCursosPaginado(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                2,
                5
        )).thenReturn(mockResponse);

        mockMvc.perform(get("/api/cursos/buscar")
                        .param("page", "2")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cursos").isArray()); // resultado paginado
    }
}