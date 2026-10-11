package org.example;

// Recurso de tipo equipo, identificado por la clase de equipo que es.
public class Equipo extends Recurso {
    private String tipoEquipo;

    public Equipo(int id, String nombre, String tipoEquipo) {
        super(id, nombre);
        this.tipoEquipo = tipoEquipo;
    }

    @Override
    public String descripcion() {
        return "Equipo: " + getNombre()
                + " | Tipo: " + tipoEquipo
                + " | Disponible: " + isDisponible();
    }
}