package org.example;

/**
 * Define la categoría de una emergencia y su nivel de prioridad.
 */
public class TipoEmergencia {
    private String nombre;
    private int prioridad;

    public TipoEmergencia(String nombre, int prioridad) {
        this.nombre = nombre;
        setPrioridad(prioridad);
    }

    public String getNombre() {
        return nombre;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(int prioridad) {
        if (prioridad < 1) {
            throw new IllegalArgumentException(
                    "La prioridad debe ser mayor que cero.");
        }
        this.prioridad = prioridad;
    }

    @Override
    public String toString() {
        return nombre + " (prioridad " + prioridad + ")";
    }
}