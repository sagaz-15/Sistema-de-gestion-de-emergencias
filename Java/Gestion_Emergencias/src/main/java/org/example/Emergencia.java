package org.example;

import java.util.ArrayList;
import java.util.List;

/**
 * Modelo de una emergencia: guarda su estado, sus recursos asignados y la atención registrada.
 */
public class Emergencia {
    private int id;
    private String descripcion;
    private String ubicacion;
    private TipoEmergencia tipo;
    private Estado estado;
    private List<Recurso> recursos;
    private String atencion;

    public Emergencia(int id, String descripcion, String ubicacion,
                      TipoEmergencia tipo) {
        this.id = id;
        this.descripcion = descripcion;
        this.ubicacion = ubicacion;
        this.tipo = tipo;
        this.estado = Estado.PENDIENTE;
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

    // Permite reclasificar una emergencia que aún no fue atendida.
    public void setTipo(TipoEmergencia tipo) {
        if (estado.esFinal()) {
            throw new IllegalStateException(
                    "No se puede reclasificar una emergencia finalizada.");
        }
        this.tipo = tipo;
    }

    public Estado getEstado() {
        return estado;
    }

    public List<Recurso> getRecursos() {
        return recursos;
    }

    public String getAtencion() {
        return atencion;
    }

    public void asignarRecurso(Recurso recurso) {
        recursos.add(recurso);
    }

    // Cambia el estado validando la transición contra el enum Estado.
    public void actualizarEstado(Estado estado) {
        if (this.estado.esFinal()) {
            throw new IllegalStateException(
                    "La emergencia ya fue finalizada.");
        }
        if (this.estado == estado) {
            throw new IllegalStateException(
                    "La emergencia ya está en estado " + estado.getEtiqueta() + ".");
        }
        if (!this.estado.puedeTransitarA(estado)) {
            throw new IllegalStateException(
                    "No se puede pasar de " + this.estado.getEtiqueta()
                            + " a " + estado.getEtiqueta() + ".");
        }
        this.estado = estado;
    }

    // Cierre de la atención: salta la tabla de transiciones y queda en estado final.
    public void registrarAtencion(String atencion) {
        if (estado.esFinal()) {
            throw new IllegalStateException(
                    "La atención ya fue registrada.");
        }
        this.atencion = atencion;
    }

    // Cierre de la atención: pasa directo a estado final liberando la tabla de transiciones.
    public void marcarFinalizada() {
        if (estado.esFinal()) {
            throw new IllegalStateException(
                    "La emergencia ya fue finalizada.");
        }
        this.estado = Estado.FINALIZADA;
    }

    public boolean estaAtendida() {
        return estado.esFinal();
    }

    @Override
    public String toString() {
        return "Emergencia #" + id
                + " | " + tipo.getNombre()
                + " | " + descripcion
                + " | Ubicación: " + ubicacion
                + " | Prioridad: " + tipo.getPrioridad()
                + " | Estado: " + estado.getEtiqueta()
                + " | Recursos asignados: " + recursos.size()
                + (atencion.isEmpty() ? "" : " | Atención: " + atencion);
    }
}