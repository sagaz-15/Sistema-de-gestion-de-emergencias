package org.example;

/**
 * Parámetros que el administrador configura y que rigen el flujo de asignación de recursos.
 */
public class ParametrosAsignacion {
    private int maxRecursosPorEmergencia;
    private boolean cambiarEstadoAlAsignar;
    private boolean permitirFinalizarSinRecursos;

    public ParametrosAsignacion() {
        this(3, true, false);
    }

    public ParametrosAsignacion(int maxRecursosPorEmergencia,
                               boolean cambiarEstadoAlAsignar,
                               boolean permitirFinalizarSinRecursos) {
        setMaxRecursosPorEmergencia(maxRecursosPorEmergencia);
        this.cambiarEstadoAlAsignar = cambiarEstadoAlAsignar;
        this.permitirFinalizarSinRecursos = permitirFinalizarSinRecursos;
    }

    public int getMaxRecursosPorEmergencia() {
        return maxRecursosPorEmergencia;
    }

    public void setMaxRecursosPorEmergencia(int maxRecursosPorEmergencia) {
        if (maxRecursosPorEmergencia < 1) {
            throw new IllegalArgumentException(
                    "El máximo de recursos por emergencia debe ser al menos 1.");
        }
        this.maxRecursosPorEmergencia = maxRecursosPorEmergencia;
    }

    public boolean isCambiarEstadoAlAsignar() {
        return cambiarEstadoAlAsignar;
    }

    public void setCambiarEstadoAlAsignar(boolean cambiarEstadoAlAsignar) {
        this.cambiarEstadoAlAsignar = cambiarEstadoAlAsignar;
    }

    public boolean isPermitirFinalizarSinRecursos() {
        return permitirFinalizarSinRecursos;
    }

    public void setPermitirFinalizarSinRecursos(boolean permitir) {
        this.permitirFinalizarSinRecursos = permitir;
    }

    public String descripcion() {
        return "Máx. recursos por emergencia: " + maxRecursosPorEmergencia
                + " | Cambio de estado al asignar: " + (cambiarEstadoAlAsignar ? "sí" : "no")
                + " | Finalizar sin recursos: " + (permitirFinalizarSinRecursos ? "sí" : "no");
    }

    @Override
    public String toString() {
        return descripcion();
    }
}