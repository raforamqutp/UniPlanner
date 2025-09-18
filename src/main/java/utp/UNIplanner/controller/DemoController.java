package utp.UNIplanner.controller;
import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
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
    
    @GetMapping("/cursos/buscar")
    public CursoResponse buscarCursos(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Integer ciclo,
            @RequestParam(required = false) String docente,
            @RequestParam(required = false) String horario,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return demoService.buscarCursosPaginado(
                Optional.ofNullable(nombre),
                Optional.ofNullable(ciclo),
                Optional.ofNullable(docente),
                Optional.ofNullable(horario),
                page,
                size
        );
    }

}