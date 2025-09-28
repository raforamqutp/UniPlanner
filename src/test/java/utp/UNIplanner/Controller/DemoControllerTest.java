package utp.UNIplanner.Controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import utp.UNIplanner.controller.DemoController;
import utp.UNIplanner.model.CursoResponse;
import utp.UNIplanner.service.DemoService;
import utp.UNIplanner.service.SeleccionService;

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
        CursoResponse mockResponse = new CursoResponse();
        mockResponse.setMessage("Found curso");
        when(demoService.getCursosPorNombre("math")).thenReturn(mockResponse);

        mockMvc.perform(get("/api/cursos/nombre/math"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Found curso"));
    }

    @Test
    void testBuscarCursosByCiclo() throws Exception {
        CursoResponse mockResponse = new CursoResponse();
        mockResponse.setMessage("Filtered by ciclo");
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
                .andExpect(jsonPath("$.message").value("Filtered by ciclo"));
    }

    @Test
    void testBuscarCursosByNombreAndDocente() throws Exception {
        CursoResponse mockResponse = new CursoResponse();
        mockResponse.setMessage("Filtered by nombre and docente");
        when(demoService.buscarCursosPaginado(
                Optional.of("algebra"),
                Optional.empty(),
                Optional.of("Smith"),
                Optional.empty(),
                0,
                10
        )).thenReturn(mockResponse);

        mockMvc.perform(get("/api/cursos/buscar")
                        .param("nombre", "algebra")
                        .param("docente", "Smith"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Filtered by nombre and docente"));
    }

    @Test
    void testBuscarCursosWithPagination() throws Exception {
        CursoResponse mockResponse = new CursoResponse();
        mockResponse.setMessage("Paged result");
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
                .andExpect(jsonPath("$.message").value("Paged result"));
    }
}
