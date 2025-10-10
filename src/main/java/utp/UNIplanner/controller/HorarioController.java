package utp.UNIplanner.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import utp.UNIplanner.model.HorarioBloque;
import utp.UNIplanner.model.HorarioResponse;
import utp.UNIplanner.service.HorarioService;

import java.time.LocalTime;

@Controller
@RequestMapping("/horario")
public class HorarioController {

    private final HorarioService horarioService;

    public HorarioController(HorarioService horarioService) {
        this.horarioService = horarioService;
    }

    // Vista HTML (MVC)
    @GetMapping
    public String verHorario(Model model) {
        model.addAttribute("bloques", horarioService.obtenerHorario().getBloques());
        return "horario"; // templates/horario.html
    }

    // REST endpoints
    @GetMapping("/api")
    @ResponseBody
    public HorarioResponse obtenerHorarioJSON() {
        return horarioService.obtenerHorario();
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
    }
}
