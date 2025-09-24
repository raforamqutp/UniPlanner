package utp.UNIplanner.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import utp.UNIplanner.model.CursoResponse;
import utp.UNIplanner.service.DemoService;
import utp.UNIplanner.service.SeleccionService;

@RestController
@RequestMapping("/api/cursos")
public class DemoController {

    private final DemoService demoService;
    private final SeleccionService seleccionService;

    public DemoController(DemoService demoService, SeleccionService seleccionService) {
        this.demoService = demoService;
        this.seleccionService = seleccionService;
    }

    @GetMapping("/demo")
    public CursoResponse getDemo() {
        return demoService.getDemoCursos();
    }

    @GetMapping("/ciclo/{ciclo}")
    public CursoResponse getCursosPorCiclo(@PathVariable int ciclo) {
        return demoService.getCursosPorCiclo(ciclo);
    }

    @GetMapping("/nombre/{nombre}")
    public CursoResponse getCursosPorNombre(@PathVariable String nombre) {
        return demoService.getCursosPorNombre(nombre);
    }

    @GetMapping("/buscar")
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

    @PostMapping("/seleccion")
    public SeleccionResponse seleccionar(@RequestBody List<String> codigos) {
        return seleccionService.seleccionarSecciones(codigos);
    }

    @GetMapping("/seleccion")
    public SeleccionResponse obtenerSeleccionados() {
        return seleccionService.obtenerSeleccionados();
    }

    @DeleteMapping("/seleccion")
    public void limpiarSeleccion() {
        seleccionService.limpiarSeleccion();
    }
}