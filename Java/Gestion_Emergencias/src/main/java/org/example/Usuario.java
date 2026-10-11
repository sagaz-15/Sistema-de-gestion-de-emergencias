package org.example;

// Clase base abstracta que representa a cualquier usuario del sistema.
public abstract class Usuario {
    private String nombre;

    public Usuario(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public abstract String getRol();
}