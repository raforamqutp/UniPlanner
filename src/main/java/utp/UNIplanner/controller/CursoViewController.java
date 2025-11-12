package utp.UNIplanner.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import utp.UNIplanner.model.CursoResponse;
import utp.UNIplanner.service.DemoService;

@Controller
public class CursoViewController {

    private final DemoService demoService;

    public CursoViewController(DemoService demoService) {
        this.demoService = demoService;
    }
    
    // Redirigir la raíz "/" hacia "/cursos"
    @GetMapping("/")
    public String redirectToCursos() {
        return "redirect:/cursos";
    }

    // Mostrar todos los cursos
    @GetMapping("/cursos")
    public String verCursos(Model model) {
        model.addAttribute("titulo", "Todos los Cursos");
        model.addAttribute("cursos", demoService.getDemoCursos().getCursos());
        return "cursos";
    }

    // Mostrar cursos por ciclo
    @GetMapping("/cursos/ciclo/{ciclo}")
    public String verCursosPorCiclo(@PathVariable int ciclo, Model model) {
        model.addAttribute("titulo", "Cursos del Ciclo " + ciclo);
        model.addAttribute("cursos", demoService.getCursosPorCiclo(ciclo).getCursos());
        return "cursos";
    }

    // Mostrar cursos por nombre
    @GetMapping("/cursos/nombre/{nombre}")
    public String verCursosPorNombre(@PathVariable String nombre, Model model) {
        model.addAttribute("titulo", "Cursos que coinciden con: \"" + nombre + "\"");
        model.addAttribute("cursos", demoService.getCursosPorNombre(nombre).getCursos());
        return "cursos";
    }

    // Búsqueda avanzada con filtros
    @GetMapping("/cursos/buscar")
    public String buscarCursos(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Integer ciclo,
            @RequestParam(required = false) String docente,
            @RequestParam(required = false) String horario,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        CursoResponse response = demoService.buscarCursosPaginado(
                Optional.ofNullable(nombre),
                Optional.ofNullable(ciclo),
                Optional.ofNullable(docente),
                Optional.ofNullable(horario),
                page,
                size
        );

        model.addAttribute("titulo", "Resultados de Búsqueda");
        model.addAttribute("cursos", response.getCursos());
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        return "cursos";
    }
    
    @GetMapping("/cursos/avance")
    public String verAvanceDemo(Model model) {
        model.addAttribute("cursosPorCiclo", demoService.getCursosAgrupadosPorCiclo());
        return "cursos-avance-demo";
    }
}