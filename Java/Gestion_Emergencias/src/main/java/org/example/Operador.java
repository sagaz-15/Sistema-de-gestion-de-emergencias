package org.example;

// Usuario que registra emergencias, asigna recursos y actualiza sus estados.
public class Operador extends Usuario {

    public Operador(String nombre) {
        super(nombre);
    }

    @Override
    public String getRol() {
        return "Operador";
    }
}