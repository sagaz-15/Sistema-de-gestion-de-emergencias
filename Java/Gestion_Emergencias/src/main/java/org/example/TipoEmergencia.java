package org.example;

// Define la categoría de una emergencia y su nivel de prioridad.
public class TipoEmergencia {
    private String nombre;
    private int prioridad;

    public TipoEmergencia(String nombre, int prioridad) {
        this.nombre = nombre;
        this.prioridad = prioridad;
    }

    public String getNombre() {
        return nombre;
    }

    public int getPrioridad() {
        return prioridad;
    }

    @Override
    public String toString() {
        return nombre + " (prioridad " + prioridad + ")";
    }
}