package utp.UNIplanner.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import utp.UNIplanner.model.CursoResponse;
import utp.UNIplanner.model.SeleccionResponse;
import utp.UNIplanner.service.DemoService;
import utp.UNIplanner.service.SeleccionService;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class DemoControllerTest {

    @Mock
    private DemoService demoService;

    @Mock
    private SeleccionService seleccionService;
 
    @InjectMocks
    private DemoController demoController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(demoController).build();
    }

    @Test
    void testGetDemo() throws Exception {
        CursoResponse mockResponse = new CursoResponse();
        when(demoService.getDemoCursos()).thenReturn(mockResponse);

        mockMvc.perform(get("/api/cursos/demo"))
                .andExpect(status().isOk());

        verify(demoService).getDemoCursos();
    }

    @Test
    void testGetCursosPorCiclo() throws Exception {
        CursoResponse mockResponse = new CursoResponse();
        when(demoService.getCursosPorCiclo(3)).thenReturn(mockResponse);

        mockMvc.perform(get("/api/cursos/ciclo/3"))
                .andExpect(status().isOk());

        verify(demoService).getCursosPorCiclo(3);
    }

    @Test
    void testGetCursosPorNombre() throws Exception {
        CursoResponse mockResponse = new CursoResponse();
        when(demoService.getCursosPorNombre("Matemáticas")).thenReturn(mockResponse);

        mockMvc.perform(get("/api/cursos/nombre/Matemáticas"))
                .andExpect(status().isOk());

        verify(demoService).getCursosPorNombre("Matemáticas");
    }

    @Test
    void testBuscarCursos() throws Exception {
        CursoResponse mockResponse = new CursoResponse();
        when(demoService.buscarCursosPaginado(
                any(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(mockResponse);

        mockMvc.perform(get("/api/cursos/buscar")
                        .param("nombre", "Física")
                        .param("ciclo", "2")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk());

        verify(demoService).buscarCursosPaginado(
                Optional.of("Física"),
                Optional.of(2),
                Optional.empty(),
                Optional.empty(),
                0,
                5
        );
    }

    @Test
    void testSeleccionar() throws Exception {
        SeleccionResponse mockResponse = new SeleccionResponse();
        mockResponse.setSuccess(true);

        when(seleccionService.seleccionarSecciones(anyList()))
                .thenReturn(mockResponse);

        mockMvc.perform(post("/api/cursos/seleccion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[\"CURS101\", \"CURS102\"]"))
                .andExpect(status().isOk());

        verify(seleccionService).seleccionarSecciones(Arrays.asList("CURS101", "CURS102"));
    }

    @Test
    void testObtenerSeleccionados() throws Exception {
        SeleccionResponse mockResponse = new SeleccionResponse();
        when(seleccionService.obtenerSeleccionados()).thenReturn(mockResponse);

        mockMvc.perform(get("/api/cursos/seleccion"))
                .andExpect(status().isOk());

        verify(seleccionService).obtenerSeleccionados();
    }

    @Test
    void testLimpiarSeleccion() throws Exception {
        mockMvc.perform(delete("/api/cursos/seleccion"))
                .andExpect(status().isOk());

        verify(seleccionService).limpiarSeleccion();
    }
}
