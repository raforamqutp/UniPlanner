package utp.UNIplanner.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import utp.UNIplanner.service.DemoService;

@Controller
public class CursoViewController {

    private final DemoService demoService;

    public CursoViewController(DemoService demoService) {
        this.demoService = demoService;
    }

    // Para ostrar todos los cursos
    @GetMapping("/cursos")
    public String verCursos(Model model) {
        model.addAttribute("titulo", "Todos los Cursos");
        model.addAttribute("cursos", demoService.getDemoCursos().getCursos());
        return "cursos";
    }

    // Para mostrar cursos por ciclo
    @GetMapping("/datos/cursos/ciclo/{ciclo}")
    public String verCursosPorCiclo(@PathVariable int ciclo, Model model) {
        model.addAttribute("titulo", "Cursos del Ciclo " + ciclo);
        model.addAttribute("cursos", demoService.getCursosPorCiclo(ciclo).getCursos());
        return "cursos";
    }

    // Para mostrar cursos por nombre
    @GetMapping("/datos/cursos/nombre/{nombre}")
    public String verCursosPorNombre(@PathVariable String nombre, Model model) {
        model.addAttribute("titulo", "Cursos que coinciden con: \"" + nombre + "\"");
        model.addAttribute("cursos", demoService.getCursosPorNombre(nombre).getCursos());
        return "cursos";
    }
}
