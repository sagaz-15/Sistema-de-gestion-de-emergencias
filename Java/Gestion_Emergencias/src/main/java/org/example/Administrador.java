package org.example;

import java.util.EnumSet;

/**
 * Usuario encargado de registrar tipos de emergencia, recursos,
 * prioridades y parámetros de asignación.
 */
public class Administrador extends Usuario {

    public Administrador(String nombre) {
        super(nombre, EnumSet.of(
                Permiso.VER_EMERGENCIAS,
                Permiso.VER_EMERGENCIAS_ATENDIDAS,
                Permiso.VER_RECURSOS,
                Permiso.VER_RECURSOS_DISPONIBLES,
                Permiso.REGISTRAR_TIPO,
                Permiso.CONFIGURAR_PRIORIDAD,
                Permiso.REGISTRAR_RECURSO,
                Permiso.CONFIGURAR_ASIGNACION));
    }

    @Override
    public String getRol() {
        return "Administrador";
    }
}