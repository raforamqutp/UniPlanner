package utp.UNIplanner.controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import utp.UNIplanner.model.CursoResponse;
import utp.UNIplanner.service.DemoService;


@RestController
public class DemoController {

    private final DemoService demoService;

    public DemoController(DemoService demoService) {
        this.demoService = demoService;
    }

    @GetMapping("/demo")
    public CursoResponse getDemo() {
        return demoService.getDemoCursos();
    }

    @GetMapping("/cursos/ciclo/{ciclo}")
    public CursoResponse getCursosPorCiclo(@PathVariable int ciclo) {
        return demoService.getCursosPorCiclo(ciclo);
    }

    @GetMapping("/cursos/nombre/{nombre}")
    public CursoResponse getCursosPorNombre(@PathVariable String nombre) {
        return demoService.getCursosPorNombre(nombre);
    }
}