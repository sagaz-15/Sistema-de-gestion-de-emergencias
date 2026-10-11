package org.example;

import java.util.ArrayList;
import java.util.List;

// Modelo de una emergencia: guarda su estado, sus recursos asignados y la atención final.
public class Emergencia {
    private int id;
    private String descripcion;
    private String ubicacion;
    private TipoEmergencia tipo;
    private String estado;
    private List<Recurso> recursos;
    private String atencion;

    public Emergencia(int id, String descripcion, String ubicacion,
                      TipoEmergencia tipo) {
        this.id = id;
        this.descripcion = descripcion;
        this.ubicacion = ubicacion;
        this.tipo = tipo;
        this.estado = "Pendiente";
        this.recursos = new ArrayList<>();
        this.atencion = "";
    }

    public int getId() {
        return id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public TipoEmergencia getTipo() {
        return tipo;
    }

    public String getEstado() {
        return estado;
    }

    public List<Recurso> getRecursos() {
        return recursos;
    }

    public void asignarRecurso(Recurso recurso) {
        recursos.add(recurso);
    }

    public void actualizarEstado(String estado) {
        this.estado = estado;
    }

    public void registrarAtencion(String atencion) {
        this.atencion = atencion;
    }

    @Override
    public String toString() {
        return "Emergencia #" + id
                + " | " + tipo.getNombre()
                + " | " + descripcion
                + " | Ubicación: " + ubicacion
                + " | Prioridad: " + tipo.getPrioridad()
                + " | Estado: " + estado
                + " | Recursos asignados: " + recursos.size()
                + (atencion.isEmpty() ? "" : " | Atención: " + atencion);
    }
}