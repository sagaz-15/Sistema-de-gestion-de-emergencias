package org.example;

import java.util.EnumSet;
/**
 * Usuario que registra emergencias, consulta recursos,
 * asigna recursos, actualiza estados y finaliza atenciones.
 */
public class Operador extends Usuario {

    public Operador(String nombre) {
        super(nombre, EnumSet.of(
                Permiso.VER_EMERGENCIAS,
                Permiso.VER_EMERGENCIAS_ATENDIDAS,
                Permiso.VER_RECURSOS,
                Permiso.VER_RECURSOS_DISPONIBLES,
                Permiso.REGISTRAR_EMERGENCIA,
                Permiso.ASIGNAR_RECURSO,
                Permiso.ACTUALIZAR_ESTADO,
                Permiso.REGISTRAR_ATENCION,
                Permiso.FINALIZAR_ATENCION));
    }

    @Override
    public String getRol() {
        return "Operador";
    }
}