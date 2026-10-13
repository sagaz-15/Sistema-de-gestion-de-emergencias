package org.example;

/**
 * Operaciones que el sistema controla por rol.
 * Se evalúan en el modelo (SistemaEmergencias), no solo en la interfaz.
 */
public enum Permiso {
    VER_EMERGENCIAS("ver emergencias"),
    VER_EMERGENCIAS_ATENDIDAS("ver emergencias atendidas"),
    VER_RECURSOS("ver recursos"),
    VER_RECURSOS_DISPONIBLES("ver recursos disponibles"),
    REGISTRAR_TIPO("registrar tipos de emergencia"),
    CONFIGURAR_PRIORIDAD("configurar prioridades"),
    REGISTRAR_RECURSO("registrar recursos"),
    CONFIGURAR_ASIGNACION("configurar parámetros de asignación"),
    REGISTRAR_EMERGENCIA("registrar emergencias"),
    ASIGNAR_RECURSO("asignar recursos"),
    ACTUALIZAR_ESTADO("actualizar estados"),
    REGISTRAR_ATENCION("registrar atención"),
    FINALIZAR_ATENCION("finalizar atención");

    private final String descripcion;

    Permiso(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}