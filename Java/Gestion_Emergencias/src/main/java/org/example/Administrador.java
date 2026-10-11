package org.example;

// Usuario encargado de registrar tipos de emergencia, recursos y prioridades.
public class Administrador extends Usuario {

    public Administrador(String nombre) {
        super(nombre);
    }

    @Override
    public String getRol() {
        return "Administrador";
    }
}