package utp.UNIplanner.model;

import java.util.List;

public class HorarioResponse {
    private List<HorarioBloque> bloques;

    public HorarioResponse() {}
    public HorarioResponse(List<HorarioBloque> bloques) {
        this.bloques = bloques;
    }

    public List<HorarioBloque> getBloques() { return bloques; }
    public void setBloques(List<HorarioBloque> bloques) { this.bloques = bloques; }
}
