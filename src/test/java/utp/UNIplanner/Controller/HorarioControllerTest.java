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
        // Inicializa los mocks antes de cada test
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testVerHorario() {
        // Simulamos el modelo de Spring
        Model model = mock(Model.class);

        // Mock de una sección seleccionada por el usuario
        Seccion seccion = new Seccion("A01", "Prof. X", List.of("Lunes : 08:00 - 10:00"));

        // Simulamos que el servicio de selección devuelve esa sección
        SeleccionResponse seleccionResponse = new SeleccionResponse(List.of(seccion), Collections.emptyList());

        // Y que el servicio de horario devuelve una respuesta vacía (para este test)
        HorarioResponse horarioResponse = new HorarioResponse(List.of());

        when(seleccionService.obtenerSeleccionados()).thenReturn(seleccionResponse);
        when(horarioService.obtenerHorario()).thenReturn(horarioResponse);

        // Ejecutamos el método del controlador
        String viewName = horarioController.verHorario(model);

        // Verificamos que la vista sea la correcta
        assertEquals("horario", viewName);

        // Aseguramos que se haya construido el horario con las secciones seleccionadas
        verify(horarioService).construirHorarioDesdeSelecciones(List.of(seccion));

        // Y que los bloques se hayan añadido al modelo
        verify(model).addAttribute(eq("bloques"), eq(horarioResponse.getBloques()));
    }

    @Test
    void testObtenerHorarioJSON() {
        // Creamos un mock de una sección seleccionada
        Seccion seccion = new Seccion("B01", "Prof. Y", List.of("Martes : 10:00 - 12:00"));

        // Mockeamos la respuesta de selección y de horario
        SeleccionResponse seleccionResponse = new SeleccionResponse(List.of(seccion), Collections.emptyList());
        HorarioResponse horarioResponse = new HorarioResponse(List.of());

        when(seleccionService.obtenerSeleccionados()).thenReturn(seleccionResponse);
        when(horarioService.obtenerHorario()).thenReturn(horarioResponse);

        // Ejecutamos el método que retorna el horario como JSON
        HorarioResponse response = horarioController.obtenerHorarioJSON();

        // Verificamos que la respuesta no sea nula y coincida con lo que mockeamos
        assertNotNull(response);
        assertEquals(horarioResponse, response);

        // Confirmamos que se construyó el horario usando las secciones seleccionadas
        verify(horarioService).construirHorarioDesdeSelecciones(List.of(seccion));
    }

    @Test
    void testAgregarBloque() {
        // Creamos un bloque manualmente (como si lo añadiera el usuario)
        HorarioBloque bloque = new HorarioBloque("Lunes", LocalTime.of(8, 0), LocalTime.of(10, 0), "Curso A");

        // Llamamos al controlador para agregar el bloque
        horarioController.agregarBloque(bloque);

        // Verificamos que el servicio de horario realmente lo haya agregado
        verify(horarioService).agregarBloque(bloque);
    }

    @Test
    void testEliminarBloque() {
        // Simulamos los datos del bloque a eliminar
        String dia = "Martes";
        String horaInicio = "10:00";
        String horaFin = "12:00";

        // Ejecutamos el método del controlador
        horarioController.eliminarBloque(dia, horaInicio, horaFin);

        // Verificamos que se haya llamado al método del servicio con los valores correctos
        verify(horarioService).eliminarBloque(eq(dia), eq(LocalTime.parse(horaInicio)), eq(LocalTime.parse(horaFin)));
    }

    @Test
    void testLimpiarHorario() {
        // Llamamos al controlador para limpiar todo
        horarioController.limpiarHorario();

        // Verificamos que ambos servicios hayan sido limpiados
        verify(horarioService).limpiar();
        verify(seleccionService).limpiarSeleccion();
    }
}
