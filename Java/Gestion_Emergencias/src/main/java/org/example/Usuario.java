package org.example;

import java.util.Collections;
import java.util.Set;

/**
 * Clase base abstracta que representa a cualquier usuario del sistema.
 * Cada usuario tiene su propio conjunto de permisos.
 */
public abstract class Usuario {
    private String nombre;
    private final Set<Permiso> permisos;

    protected Usuario(String nombre, Set<Permiso> permisos) {
        this.nombre = nombre;
        this.permisos = permisos;
    }

    public String getNombre() {
        return nombre;
    }

    public Set<Permiso> getPermisos() {
        return Collections.unmodifiableSet(permisos);
    }

    public boolean puede(Permiso permiso) {
        return permisos.contains(permiso);
    }

    public abstract String getRol();
}