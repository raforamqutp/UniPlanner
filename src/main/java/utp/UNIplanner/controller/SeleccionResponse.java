package utp.UNIplanner.controller;

import java.util.List;

import utp.UNIplanner.model.Seccion;

public class SeleccionResponse {
    private List<Seccion> seleccionados;
    private List<String> mensajes;

    public SeleccionResponse() {
    }

    public SeleccionResponse(List<Seccion> seleccionados, List<String> mensajes) {
        this.seleccionados = seleccionados;
        this.mensajes = mensajes;
    }

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
}
