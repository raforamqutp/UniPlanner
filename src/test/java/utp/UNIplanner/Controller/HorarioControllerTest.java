package utp.UNIplanner.Controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ui.Model;

import utp.UNIplanner.controller.HorarioController;
import utp.UNIplanner.model.*;
import utp.UNIplanner.service.HorarioService;
import utp.UNIplanner.service.SeleccionService;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HorarioControllerTest {

    @Mock
    private HorarioService horarioService;

    @Mock
    private SeleccionService seleccionService;

    @InjectMocks
    private HorarioController horarioController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testVerHorario() {
        // Arrange
        Model model = mock(Model.class);
        Seccion seccion = new Seccion("A01", "Prof. X", List.of("Lunes : 08:00 - 10:00"));
        SeleccionResponse seleccionResponse = new SeleccionResponse(List.of(seccion), Collections.emptyList());
        HorarioResponse horarioResponse = new HorarioResponse(List.of());

        when(seleccionService.obtenerSeleccionados()).thenReturn(seleccionResponse);
        when(horarioService.obtenerHorario()).thenReturn(horarioResponse);

        // Act
        String viewName = horarioController.verHorario(model);

        // Assert
        assertEquals("horario", viewName);
        verify(horarioService).construirHorarioDesdeSelecciones(List.of(seccion));
        verify(model).addAttribute(eq("bloques"), eq(horarioResponse.getBloques()));
    }

    @Test
    void testObtenerHorarioJSON() {
        // Arrange
        Seccion seccion = new Seccion("B01", "Prof. Y", List.of("Martes : 10:00 - 12:00"));
        SeleccionResponse seleccionResponse = new SeleccionResponse(List.of(seccion), Collections.emptyList());
        HorarioResponse horarioResponse = new HorarioResponse(List.of());

        when(seleccionService.obtenerSeleccionados()).thenReturn(seleccionResponse);
        when(horarioService.obtenerHorario()).thenReturn(horarioResponse);

        // Act
        HorarioResponse response = horarioController.obtenerHorarioJSON();

        // Assert
        assertNotNull(response);
        assertEquals(horarioResponse, response);
        verify(horarioService).construirHorarioDesdeSelecciones(List.of(seccion));
    }

    @Test
    void testAgregarBloque() {
        // Arrange
        HorarioBloque bloque = new HorarioBloque("Lunes", LocalTime.of(8, 0), LocalTime.of(10, 0), "Curso A");

        // Act
        horarioController.agregarBloque(bloque);

        // Assert
        verify(horarioService).agregarBloque(bloque);
    }

    @Test
    void testEliminarBloque() {
        // Arrange
        String dia = "Martes";
        String horaInicio = "10:00";
        String horaFin = "12:00";

        // Act
        horarioController.eliminarBloque(dia, horaInicio, horaFin);

        // Assert
        verify(horarioService).eliminarBloque(eq(dia), eq(LocalTime.parse(horaInicio)), eq(LocalTime.parse(horaFin)));
    }

    @Test
    void testLimpiarHorario() {
        // Act
        horarioController.limpiarHorario();

        // Assert
        verify(horarioService).limpiar();
        verify(seleccionService).limpiarSeleccion();
    }
}
