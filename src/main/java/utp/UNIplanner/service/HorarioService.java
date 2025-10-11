package utp.UNIplanner.service;

import org.springframework.stereotype.Service;
import utp.UNIplanner.model.HorarioBloque;
import utp.UNIplanner.model.HorarioResponse;
import utp.UNIplanner.model.Seccion;
import utp.UNIplanner.model.Curso;

import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class HorarioService {

    private final List<HorarioBloque> bloques = new CopyOnWriteArrayList<>();
    private final DemoService demoService;

    public HorarioService(DemoService demoService) {
        this.demoService = demoService;
    }

    public HorarioResponse obtenerHorario() {
        return new HorarioResponse(new ArrayList<>(bloques));
    }

    public void construirHorarioDesdeSelecciones(List<Seccion> secciones) {
        bloques.clear();
        System.out.println("=== CONSTRUYENDO HORARIO DESDE SELECCIONES ===");
        System.out.println("Número de secciones recibidas: " + (secciones != null ? secciones.size() : 0));
        
        if (secciones != null) {
            for (Seccion seccion : secciones) {
                System.out.println("Procesando sección: " + seccion.getSeccion() + " - " + seccion.getDocente());
                System.out.println("Horarios: " + seccion.getHorario());
                agregarSeccionAlHorario(seccion);
            }
        }
        System.out.println("Total de bloques creados: " + bloques.size());
        System.out.println("=== FIN CONSTRUCCIÓN HORARIO ===");
    }

    private void agregarSeccionAlHorario(Seccion seccion) {
        String nombreCurso = buscarNombreCursoPorSeccion(seccion.getSeccion());
        
        for (String horarioStr : seccion.getHorario()) {
            HorarioBloque bloque = parseHorarioString(horarioStr, seccion, nombreCurso);
            if (bloque != null && !existeBloque(bloque)) {
                bloques.add(bloque);
            }
        }
    }

    private String buscarNombreCursoPorSeccion(String codigoSeccion) {
        for (Curso curso : demoService.getDemoCursos().getCursos()) {
            for (Seccion seccion : curso.getSecciones()) {
                if (seccion.getSeccion().equals(codigoSeccion)) {
                    return curso.getNombre();
                }
            }
        }
        return "Curso no encontrado";
    }

    private boolean existeBloque(HorarioBloque nuevoBloque) {
        return bloques.stream().anyMatch(bloque -> 
            bloque.getDia().equals(nuevoBloque.getDia()) &&
            bloque.getHoraInicio().equals(nuevoBloque.getHoraInicio()) &&
            bloque.getHoraFin().equals(nuevoBloque.getHoraFin()) &&
            Objects.equals(bloque.getNombre(), nuevoBloque.getNombre())
        );
    }

    private HorarioBloque parseHorarioString(String horarioStr, Seccion seccion, String nombreCurso) {
        try {
            System.out.println("Parseando horario: " + horarioStr);
            
            String[] partes = horarioStr.split(" : ");
            if (partes.length != 2) {
                System.err.println("Formato de horario inválido: " + horarioStr);
                return null;
            }
            
            String dia = normalizarDia(partes[0].trim());
            String[] horarios = partes[1].split(" - ");
            if (horarios.length != 2) {
                System.err.println("Formato de tiempo inválido: " + horarioStr);
                return null;
            }
            
            LocalTime horaInicio = LocalTime.parse(horarios[0].trim());
            LocalTime horaFin = LocalTime.parse(horarios[1].trim());

            String nombreCompleto = nombreCurso + " - " + seccion.getDocente();
            
            HorarioBloque bloque = new HorarioBloque();
            bloque.setDia(dia);
            bloque.setHoraInicio(horaInicio);
            bloque.setHoraFin(horaFin);
            bloque.setNombre(nombreCompleto);
            bloque.setCodigoSeccion(seccion.getSeccion());
            bloque.setDocente(seccion.getDocente());
            bloque.setNombreCurso(nombreCurso);
            
            System.out.println("Bloque creado: " + dia + " " + horaInicio + "-" + horaFin + " - " + nombreCompleto);
            return bloque;
        } catch (Exception e) {
            System.err.println("Error parseando horario: " + horarioStr + " - " + e.getMessage());
            return null;
        }
    }

    private String normalizarDia(String dia) {
        switch (dia.toLowerCase()) {
            case "lunes": return "Lunes";
            case "martes": return "Martes";
            case "miercoles": 
            case "miércoles": return "Miercoles";
            case "jueves": return "Jueves";
            case "viernes": return "Viernes";
            case "sabado": 
            case "sábado": return "Sabado";
            case "domingo": return "Domingo";
            default: return dia;
        }
    }

    public void agregarBloque(HorarioBloque bloque) {
        if (!existeBloque(bloque)) {
            bloques.add(bloque);
        }
    }

    public void eliminarBloque(String dia, LocalTime inicio, LocalTime fin) {
        bloques.removeIf(b -> b.getDia().equalsIgnoreCase(dia)
                && b.getHoraInicio().equals(inicio)
                && b.getHoraFin().equals(fin));
    }

    public void limpiar() {
        bloques.clear();
    }
}