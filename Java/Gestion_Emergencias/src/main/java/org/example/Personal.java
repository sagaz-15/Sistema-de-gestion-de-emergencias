package org.example;

// Recurso de tipo persona, identificado por el cargo que desempeña.
public class Personal extends Recurso {
    private String cargo;

    public Personal(int id, String nombre, String cargo) {
        super(id, nombre);
        this.cargo = cargo;
    }

    @Override
    public String descripcion() {
        return "Personal: " + getNombre()
                + " | Cargo: " + cargo
                + " | Disponible: " + isDisponible();
    }
}