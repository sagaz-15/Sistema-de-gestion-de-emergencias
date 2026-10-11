package org.example;

import java.util.ArrayList;
import java.util.List;

// Núcleo del sistema: guarda los datos y aplica las reglas de asignación de recursos.
public class SistemaEmergencias {
    private List<Emergencia> emergencias;
    private List<TipoEmergencia> tipos;
    private List<Recurso> recursos;
    private int siguienteIdEmergencia;
    private int siguienteIdRecurso;

    public SistemaEmergencias() {
        emergencias = new ArrayList<>();
        tipos = new ArrayList<>();
        recursos = new ArrayList<>();
        siguienteIdEmergencia = 1;
        siguienteIdRecurso = 1;

        // Datos iniciales para facilitar el uso.
        tipos.add(new TipoEmergencia("Incendio", 1));
        tipos.add(new TipoEmergencia("Accidente", 2));
        tipos.add(new TipoEmergencia("Emergencia médica", 1));

        recursos.add(new Vehiculo(
                siguienteIdRecurso++, "Ambulancia", "ABC-123"));
        recursos.add(new Vehiculo(
                siguienteIdRecurso++, "Camión de bomberos", "DEF-456"));
        recursos.add(new Personal(
                siguienteIdRecurso++, "Carlos Pérez", "Paramédico"));
        recursos.add(new Personal(
                siguienteIdRecurso++, "Laura Gómez", "Bombera"));
        recursos.add(new Equipo(
                siguienteIdRecurso++, "Botiquín", "Primeros auxilios"));
    }

    public void registrarTipo(String nombre, int prioridad) {
        tipos.add(new TipoEmergencia(nombre, prioridad));
    }

    public void registrarRecurso(String clase, String nombre,
                                 String detalle) {
        int id = siguienteIdRecurso++;

        switch (clase) {
            case "Vehículo":
                recursos.add(new Vehiculo(id, nombre, detalle));
                break;
            case "Personal":
                recursos.add(new Personal(id, nombre, detalle));
                break;
            case "Equipo":
                recursos.add(new Equipo(id, nombre, detalle));
                break;
        }
    }

    public void registrarEmergencia(String descripcion, String ubicacion,
                                    TipoEmergencia tipo) {
        emergencias.add(new Emergencia(
                siguienteIdEmergencia++,
                descripcion, ubicacion, tipo));
    }

    public void asignarRecurso(int idEmergencia, int idRecurso) {
        Emergencia emergencia = buscarEmergencia(idEmergencia);
        Recurso recurso = buscarRecurso(idRecurso);

        if (emergencia == null || recurso == null) {
            throw new IllegalArgumentException(
                    "La emergencia o el recurso no existe.");
        }

        if (!recurso.isDisponible()) {
            throw new IllegalArgumentException(
                    "El recurso no está disponible.");
        }

        if (emergencia.getEstado().equals("Finalizada")) {
            throw new IllegalArgumentException(
                    "La emergencia ya fue finalizada.");
        }

        emergencia.asignarRecurso(recurso);
        recurso.setDisponible(false);
        emergencia.actualizarEstado("En atención");
    }

    public void actualizarEstado(int id, String estado) {
        Emergencia emergencia = buscarEmergencia(id);

        if (emergencia == null) {
            throw new IllegalArgumentException(
                    "No existe esa emergencia.");
        }

        if (emergencia.getEstado().equals("Finalizada")) {
            throw new IllegalArgumentException(
                    "La emergencia ya fue finalizada.");
        }

        emergencia.actualizarEstado(estado);
    }

    public void finalizarAtencion(int id, String detalle) {
        Emergencia emergencia = buscarEmergencia(id);

        if (emergencia == null) {
            throw new IllegalArgumentException(
                    "No existe esa emergencia.");
        }

        if (emergencia.getEstado().equals("Finalizada")) {
            throw new IllegalArgumentException(
                    "La emergencia ya fue finalizada.");
        }

        emergencia.registrarAtencion(detalle);
        emergencia.actualizarEstado("Finalizada");

        for (Recurso recurso : emergencia.getRecursos()) {
            recurso.setDisponible(true);
        }
    }

    private Emergencia buscarEmergencia(int id) {
        for (Emergencia emergencia : emergencias) {
            if (emergencia.getId() == id) {
                return emergencia;
            }
        }
        return null;
    }

    private Recurso buscarRecurso(int id) {
        for (Recurso recurso : recursos) {
            if (recurso.getId() == id) {
                return recurso;
            }
        }
        return null;
    }

    public List<TipoEmergencia> getTipos() {
        return tipos;
    }

    public List<Emergencia> getEmergencias() {
        return emergencias;
    }

    public List<Recurso> getRecursos() {
        return recursos;
    }

    public String listarEmergencias() {
        StringBuilder texto = new StringBuilder();

        if (emergencias.isEmpty()) {
            return "No hay emergencias registradas.";
        }

        for (Emergencia emergencia : emergencias) {
            texto.append(emergencia).append("\n\n");
        }

        return texto.toString();
    }

    public String listarRecursos(boolean soloDisponibles) {
        StringBuilder texto = new StringBuilder();

        for (Recurso recurso : recursos) {
            if (!soloDisponibles || recurso.isDisponible()) {
                // Polimorfismo: se ejecuta la descripción
                // correspondiente al tipo concreto de recurso.
                texto.append("ID: ").append(recurso.getId())
                        .append(" | ")
                        .append(recurso.descripcion())
                        .append("\n");
            }
        }

        if (texto.length() == 0) {
            return "No hay recursos para mostrar.";
        }

        return texto.toString();
    }
}