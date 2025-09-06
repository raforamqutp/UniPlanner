package utp.UNIplanner.model;

import java.util.List;

public class CursoResponse {
    private List<Curso> cursos;

    public CursoResponse(List<Curso> cursos) {
        this.cursos = cursos;
    }

    public List<Curso> getCursos() { return cursos; }
}