package utp.UNIplanner.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import utp.UNIplanner.controller.HorarioController;
import utp.UNIplanner.model.HorarioBloque;
import utp.UNIplanner.model.HorarioResponse;
import utp.UNIplanner.model.Seccion;
import utp.UNIplanner.model.SeleccionResponse;
import utp.UNIplanner.service.HorarioService;
import utp.UNIplanner.service.SeleccionService;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HorarioControllerTest.class)
class HorarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HorarioService horarioService;

    @MockBean
    private SeleccionService seleccionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testVerHorario_DevuelveVistaConBloques() throws Exception {
        // Simular la respuesta del SeleccionService
        Seccion seccionMock = new Seccion("12345", "Docente Mock", List.of("Lunes : 08:00 - 10:00"));
        SeleccionResponse seleccionResponse = new SeleccionResponse(List.of(seccionMock), Collections.emptyList());
        when(seleccionService.obtenerSeleccionados()).thenReturn(seleccionResponse);

        // Simular la respuesta del HorarioService
        HorarioBloque bloqueMock = new HorarioBloque("Lunes", LocalTime.of(8, 0), LocalTime.of(10, 0), "Curso Mock");
        HorarioResponse horarioResponse = new HorarioResponse(List.of(bloqueMock));
        when(horarioService.obtenerHorario()).thenReturn(horarioResponse);

        mockMvc.perform(get("/horario"))
                .andExpect(status().isOk()) // Espera un 200 OK
                .andExpect(view().name("horario")) // Espera que se devuelva la vista "horario.html"
                .andExpect(model().attributeExists("bloques")) // Espera que el modelo contenga "bloques"
                .andExpect(model().attribute("bloques", List.of(bloqueMock))); // Verifica el contenido

        // --- Verify ---
        verify(seleccionService).obtenerSeleccionados();
        verify(horarioService).construirHorarioDesdeSelecciones(List.of(seccionMock));
        verify(horarioService).obtenerHorario();
    }

    @Test
    void testObtenerHorarioJSON_DevuelveJSON() throws Exception {
        // Simular la respuesta del SeleccionService (esta vez vacía para variar)
        SeleccionResponse seleccionResponse = new SeleccionResponse(Collections.emptyList(), Collections.emptyList());
        when(seleccionService.obtenerSeleccionados()).thenReturn(seleccionResponse);
        
        // Simular la respuesta del HorarioService
        HorarioBloque bloqueMock = new HorarioBloque("Martes", LocalTime.of(10, 0), LocalTime.of(12, 0), "Curso JSON");
        HorarioResponse horarioResponse = new HorarioResponse(List.of(bloqueMock));
        when(horarioService.obtenerHorario()).thenReturn(horarioResponse);

        mockMvc.perform(get("/horario/api"))
                .andExpect(status().isOk()) // Espera un 200 OK
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.bloques").isArray())
                .andExpect(jsonPath("$.bloques[0].nombre").value("Curso JSON"))
                .andExpect(jsonPath("$.bloques[0].dia").value("Martes"));
        
        // --- Verify ---
        verify(horarioService).construirHorarioDesdeSelecciones(Collections.emptyList());
    }

    @Test
    void testAgregarBloque_LlamaAlServicio() throws Exception {
        HorarioBloque bloqueNuevo = new HorarioBloque("Miércoles", LocalTime.of(14, 0), LocalTime.of(16, 0), "Curso Agregado");
        
        String bloqueJson = objectMapper.writeValueAsString(bloqueNuevo);

        mockMvc.perform(post("/horario/api")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bloqueJson))
                .andExpect(status().isOk()); // Espera un 200 OK

        verify(horarioService).agregarBloque(any(HorarioBloque.class));
    }

    @Test
    void testEliminarBloque_LlamaAlServicioConParams() throws Exception {
        // --- Setup ---
        String dia = "Jueves";
        String horaInicio = "09:00";
        String horaFin = "11:00";

        mockMvc.perform(delete("/horario/api")
                .param("dia", dia)
                .param("horaInicio", horaInicio)
                .param("horaFin", horaFin))
                .andExpect(status().isOk());

        // --- Verify ---
        verify(horarioService).eliminarBloque(
                eq(dia),
                eq(LocalTime.parse(horaInicio)),
                eq(LocalTime.parse(horaFin))
        );
    }

    @Test
    void testLimpiarHorario_LlamaAmbosServicios() throws Exception {
        mockMvc.perform(delete("/horario/api/clear"))
                .andExpect(status().isOk());

        // --- Verify ---
        verify(horarioService).limpiar();
        verify(seleccionService).limpiarSeleccion();
    }
}