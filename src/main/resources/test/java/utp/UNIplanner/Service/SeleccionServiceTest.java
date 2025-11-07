package utp.UNIplanner.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import utp.UNIplanner.model.Curso;
import utp.UNIplanner.model.CursoResponse;
import utp.UNIplanner.model.Seccion;
import utp.UNIplanner.model.SeleccionResponse;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SeleccionServiceTest {

    @Mock
    private DemoService demoService;

    @InjectMocks
    private SeleccionService seleccionService;

    // Datos de prueba
    private Seccion seccionA1_martes_8_10;
    private Seccion seccionA2_martes_9_11;
    private Seccion seccionA3_martes_10_12;
    private Seccion seccionB1_lunes_14_16;
    private Seccion seccionC1_martes_8_10_exacto;

    @BeforeEach
    void setUp() {
        seccionA1_martes_8_10 = new Seccion("A1", "Docente A", List.of("Martes : 08:00 - 10:00"));
        seccionA2_martes_9_11 = new Seccion("A2", "Docente A", List.of("Martes : 09:00 - 11:00"));
        seccionA3_martes_10_12 = new Seccion("A3", "Docente A", List.of("Martes : 10:00 - 12:00"));
        seccionB1_lunes_14_16 = new Seccion("B1", "Docente B", List.of("Lunes : 14:00 - 16:00"));
        seccionC1_martes_8_10_exacto = new Seccion("C1", "Docente C", List.of("Martes : 08:00 - 10:00"));

        Curso cursoA = new Curso("CURSO_A", 1, List.of(seccionA1_martes_8_10, seccionA2_martes_9_11, seccionA3_martes_10_12));
        Curso cursoB = new Curso("CURSO_B", 2, List.of(seccionB1_lunes_14_16));
        Curso cursoC = new Curso("CURSO_C", 3, List.of(seccionC1_martes_8_10_exacto));

        when(demoService.getDemoCursos()).thenReturn(new CursoResponse(List.of(cursoA, cursoB, cursoC)));
    }

    @Test
    void testSeleccionarSecciones_SinConflicto() {
        SeleccionResponse response = seleccionService.seleccionarSecciones(List.of("A1", "B1"));

        assertTrue(response.isSuccess(), "Debería tener éxito (true)");
        assertTrue(response.getMensajes().isEmpty(), "No debería haber mensajes de error");
        assertEquals(2, response.getSeleccionados().size(), "Deberían guardarse 2 secciones");

        assertEquals(2, seleccionService.obtenerSeleccionados().getSeleccionados().size());
    }

    @Test
    void testSeleccionarSecciones_DetectaTraslape() {
        SeleccionResponse response = seleccionService.seleccionarSecciones(List.of("A1", "A2"));

        assertFalse(response.isSuccess(), "Debería fallar (false)");
        assertEquals(1, response.getMensajes().size(), "Debería haber 1 mensaje de conflicto");
        assertTrue(response.getMensajes().get(0).contains("se traslapa con"), "El mensaje debe indicar traslape");
        
        assertEquals(0, seleccionService.obtenerSeleccionados().getSeleccionados().size());
    }

    @Test
    void testSeleccionarSecciones_DetectaConflictoExacto() {
        SeleccionResponse response = seleccionService.seleccionarSecciones(List.of("A1", "C1"));

        assertFalse(response.isSuccess());
        assertEquals(1, response.getMensajes().size());
        assertTrue(response.getMensajes().get(0).contains("Conflicto de horario el Martes"));

        assertEquals(0, seleccionService.obtenerSeleccionados().getSeleccionados().size());
    }

    @Test
    void testSeleccionarSecciones_SinConflictoEnBorde() {
        SeleccionResponse response = seleccionService.seleccionarSecciones(List.of("A1", "A3"));

        assertTrue(response.isSuccess(), "No debería haber conflicto en el borde");
        assertTrue(response.getMensajes().isEmpty());
        assertEquals(2, response.getSeleccionados().size());
    }

    @Test
    void testSeleccionarSecciones_ConflictConSeccionYaAgregada() {
        SeleccionResponse response1 = seleccionService.seleccionarSecciones(List.of("A1"));
        assertTrue(response1.isSuccess());
        assertEquals(1, seleccionService.obtenerSeleccionados().getSeleccionados().size());

        SeleccionResponse response2 = seleccionService.seleccionarSecciones(List.of("A2"));

        assertFalse(response2.isSuccess(), "Debería detectar conflicto con la sección existente");
        assertEquals(1, response2.getMensajes().size());
        
        assertEquals(1, seleccionService.obtenerSeleccionados().getSeleccionados().size());
        assertEquals("A1", seleccionService.obtenerSeleccionados().getSeleccionados().get(0).getSeccion());
    }

    @Test
    void testLimpiarSeleccion() {
        seleccionService.seleccionarSecciones(List.of("B1"));
        assertEquals(1, seleccionService.obtenerSeleccionados().getSeleccionados().size());

        seleccionService.limpiarSeleccion();

        assertEquals(0, seleccionService.obtenerSeleccionados().getSeleccionados().size());
        assertTrue(seleccionService.obtenerSeleccionados().getSeleccionados().isEmpty());
    }

    @Test
    void testSeleccionarSeccionInexistente() {
        SeleccionResponse response = seleccionService.seleccionarSecciones(List.of("XXXX"));
        
        assertFalse(response.isSuccess());
        assertEquals(1, response.getMensajes().size());
        assertTrue(response.getMensajes().get(0).contains("No se encontró la sección con código: XXXX"));
    }
}