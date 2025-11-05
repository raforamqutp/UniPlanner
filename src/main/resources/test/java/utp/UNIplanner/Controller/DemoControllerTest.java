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
    private MockMvc mockMvc; // MockMvc nos permite simular peticiones HTTP sin levantar el servidor real

    @MockBean
    private DemoService demoService; // Mock del servicio principal que usa el controlador

    @MockBean
    private SeleccionService seleccionService; // Mock adicional (no usado directamente aquí, pero requerido por el controller)

    @Test
    void testGetCursosPorNombre() throws Exception {
        // Seteamos un mock de respuesta simulando que se encontró un curso
        CursoResponse mockResponse = new CursoResponse();
        mockResponse.setMessage("Found curso");
        when(demoService.getCursosPorNombre("math")).thenReturn(mockResponse);

        // Ejecutamos la petición GET y validamos la respuesta esperada
        mockMvc.perform(get("/api/cursos/nombre/math"))
                .andExpect(status().isOk()) // esperamos HTTP 200 OK
                .andExpect(jsonPath("$.message").value("Found curso")); // verificamos el campo 'message' del JSON
    }

    @Test
    void testBuscarCursosByCiclo() throws Exception {
        // Simulamos la búsqueda de cursos filtrados por ciclo (ejemplo: ciclo 3)
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

        // Llamada al endpoint con el parámetro 'ciclo'
        mockMvc.perform(get("/api/cursos/buscar")
                        .param("ciclo", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Filtered by ciclo"));
    }

    @Test
    void testBuscarCursosByNombreAndDocente() throws Exception {
        // Caso donde se filtra por nombre del curso y docente al mismo tiempo
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

        // Simulamos request con parámetros de filtro múltiples
        mockMvc.perform(get("/api/cursos/buscar")
                        .param("nombre", "algebra")
                        .param("docente", "Smith"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Filtered by nombre and docente"));
    }

    @Test
    void testBuscarCursosWithPagination() throws Exception {
        // Test específico para validar la paginación (page y size)
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

        // Llamamos al endpoint con parámetros de paginación personalizados
        mockMvc.perform(get("/api/cursos/buscar")
                        .param("page", "2")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Paged result"));
    }
}
