package utp.UNIplanner.model;

import java.util.List;

public class CursoResponse {
    private List<Curso> cursos;

    public CursoResponse() {
    }

    public CursoResponse(List<Curso> cursos) {
        this.cursos = cursos;
    }

    public List<Curso> getCursos() {
        return cursos;
    }

    public void setCursos(List<Curso> cursos) {
        this.cursos = cursos;
    }
}