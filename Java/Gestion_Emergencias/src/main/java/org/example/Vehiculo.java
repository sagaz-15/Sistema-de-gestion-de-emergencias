package org.example;

// Recurso de tipo vehículo, identificado por su placa.
public class Vehiculo extends Recurso {
    private String placa;

    public Vehiculo(int id, String nombre, String placa) {
        super(id, nombre);
        this.placa = placa;
    }

    @Override
    public String descripcion() {
        return "Vehículo: " + getNombre()
                + " | Placa: " + placa
                + " | Disponible: " + isDisponible();
    }
}