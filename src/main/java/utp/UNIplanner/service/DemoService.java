package utp.UNIplanner.service;

import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import utp.UNIplanner.model.Curso;
import utp.UNIplanner.model.CursoResponse;
import utp.UNIplanner.model.Seccion;

@Service
public class DemoService {

    public CursoResponse getDemoCursos() {
        Seccion s1 = new Seccion(
            "11335",
            "GONZALES SAJI,FREDDY ORLANDO",
            Arrays.asList("Martes : 20:15 - 21:45", "Viernes : 20:15 - 21:45")
        );

        Seccion s2 = new Seccion(
            "11288",
            "GONZALES SAJI,FREDDY ORLANDO",
            Arrays.asList("Martes : 14:00 - 15:30", "Viernes : 14:00 - 15:30")
        );

        Seccion s3 = new Seccion(
            "7272",
            "NIETO VALENCIA,RENE ALONSO",
            Arrays.asList("Martes : 08:45 - 10:15", "Jueves : 08:45 - 10:15")
        );

        Curso c1 = new Curso(
            "CURSO INTEGRADOR I: SISTEMAS - SOFTWARE",
            6,
            Arrays.asList(s1, s2, s3)
        );

        return new CursoResponse(Arrays.asList(c1));
    }
}