package utp.UNIplanner.model;

import java.util.List;

public class SeleccionResponse {
    private List<Seccion> seleccionados;
    private List<String> mensajes;
    private boolean success;

    public SeleccionResponse() {
    }

    public SeleccionResponse(List<Seccion> seleccionados, List<String> mensajes) {
        this.seleccionados = seleccionados;
        this.mensajes = mensajes;
        this.success = mensajes == null || mensajes.isEmpty();
    }

    // Getters y Setters
    public List<Seccion> getSeleccionados() {
        return seleccionados;
    }

    public void setSeleccionados(List<Seccion> seleccionados) {
        this.seleccionados = seleccionados;
    }

    public List<String> getMensajes() {
        return mensajes;
    }

    public void setMensajes(List<String> mensajes) {
        this.mensajes = mensajes;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}