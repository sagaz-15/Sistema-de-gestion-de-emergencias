package org.example;

// Clase base abstracta de todo recurso asignable a una emergencia.
public abstract class Recurso {
    private int id;
    private String nombre;
    private boolean disponible;

    public Recurso(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
        this.disponible = true;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    // Cada tipo de recurso presenta su propia información.
    public abstract String descripcion();
}