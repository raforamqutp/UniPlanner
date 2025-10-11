package utp.UNIplanner.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import utp.UNIplanner.model.HorarioBloque;
import utp.UNIplanner.model.HorarioResponse;
import utp.UNIplanner.model.Seccion;
import utp.UNIplanner.service.HorarioService;
import utp.UNIplanner.service.SeleccionService;

import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/horario")
public class HorarioController {

    private final HorarioService horarioService;
    private final SeleccionService seleccionService;

    public HorarioController(HorarioService horarioService, SeleccionService seleccionService) {
        this.horarioService = horarioService;
        this.seleccionService = seleccionService;
    }

    // Vista HTML (MVC)
    @GetMapping
    public String verHorario(Model model) {
        System.out.println("=== CARGANDO PÁGINA HORARIO ===");
        
        // Sincronizar horario con selecciones actuales
        var seleccionResponse = seleccionService.obtenerSeleccionados();
        List<Seccion> seleccionados = seleccionResponse.getSeleccionados();
        
        System.out.println("Selecciones obtenidas: " + (seleccionados != null ? seleccionados.size() : 0));
        if (seleccionados != null) {
            seleccionados.forEach(s -> 
                System.out.println("Sección: " + s.getSeccion() + " - Horarios: " + s.getHorario())
            );
        }
        
        horarioService.construirHorarioDesdeSelecciones(seleccionados);
        
        HorarioResponse horario = horarioService.obtenerHorario();
        System.out.println("Bloques en horario: " + horario.getBloques().size());
        
        model.addAttribute("bloques", horario.getBloques());
        return "horario";
    }

    // REST endpoints
    @GetMapping("/api")
    @ResponseBody
    public HorarioResponse obtenerHorarioJSON() {
        System.out.println("=== LLAMADA A /horario/api ===");
        
        // Sincronizar antes de devolver
        var seleccionResponse = seleccionService.obtenerSeleccionados();
        List<Seccion> seleccionados = seleccionResponse.getSeleccionados();
        
        System.out.println("Selecciones para API: " + (seleccionados != null ? seleccionados.size() : 0));
        horarioService.construirHorarioDesdeSelecciones(seleccionados);
        
        HorarioResponse response = horarioService.obtenerHorario();
        System.out.println("Bloques devueltos por API: " + response.getBloques().size());
        
        return response;
    }

    @PostMapping("/api")
    @ResponseBody
    public void agregarBloque(@RequestBody HorarioBloque bloque) {
        horarioService.agregarBloque(bloque);
    }

    @DeleteMapping("/api")
    @ResponseBody
    public void eliminarBloque(@RequestParam String dia,
                               @RequestParam String horaInicio,
                               @RequestParam String horaFin) {
        horarioService.eliminarBloque(
                dia,
                LocalTime.parse(horaInicio),
                LocalTime.parse(horaFin)
        );
    }

    @DeleteMapping("/api/clear")
    @ResponseBody
    public void limpiarHorario() {
        horarioService.limpiar();
        seleccionService.limpiarSeleccion();
    }
}