package org.example;

import java.util.ArrayList;
import java.util.List;

/**
 * Núcleo del sistema: guarda los datos y aplica las reglas de negocio.
 * Aquí se validan los permisos por rol, no solo en la interfaz gráfica.
 */
public class SistemaEmergencias {
    private List<Emergencia> emergencias;
    private List<TipoEmergencia> tipos;
    private List<Recurso> recursos;
    private int siguienteIdEmergencia;
    private int siguienteIdRecurso;
    private ParametrosAsignacion parametros;

    public SistemaEmergencias() {
        emergencias = new ArrayList<>();
        tipos = new ArrayList<>();
        recursos = new ArrayList<>();
        siguienteIdEmergencia = 1;
        siguienteIdRecurso = 1;
        parametros = new ParametrosAsignacion();

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

    // ================== ADMINISTRADOR ==================

    public void registrarTipo(Usuario usuario, String nombre, int prioridad) {
        exigir(usuario, Permiso.REGISTRAR_TIPO);
        tipos.add(new TipoEmergencia(nombre, prioridad));
    }

    // Permite cambiar la prioridad de un tipo ya registrado.
    public void cambiarPrioridad(Usuario usuario, String nombre, int prioridad) {
        exigir(usuario, Permiso.CONFIGURAR_PRIORIDAD);

        for (TipoEmergencia tipo : tipos) {
            if (tipo.getNombre().equalsIgnoreCase(nombre)) {
                tipo.setPrioridad(prioridad);
                return;
            }
        }
        throw new IllegalArgumentException(
                "No existe el tipo de emergencia \"" + nombre + "\".");
    }

    public void registrarRecurso(Usuario usuario, String clase,
                                 String nombre, String detalle) {
        exigir(usuario, Permiso.REGISTRAR_RECURSO);
        int id = siguienteIdRecurso++;

        switch (clase) {
            case "Vehículo" -> recursos.add(new Vehiculo(id, nombre, detalle));
            case "Personal" -> recursos.add(new Personal(id, nombre, detalle));
            case "Equipo" -> recursos.add(new Equipo(id, nombre, detalle));
            default -> throw new IllegalArgumentException(
                    "Tipo de recurso desconocido: " + clase);
        }
    }

    // El administrador ajusta las reglas de asignación en tiempo de ejecución.
    public void configurarParametros(Usuario usuario,
                                     int maxRecursosPorEmergencia,
                                     boolean cambiarEstadoAlAsignar,
                                     boolean permitirFinalizarSinRecursos) {
        exigir(usuario, Permiso.CONFIGURAR_ASIGNACION);
        parametros = new ParametrosAsignacion(
                maxRecursosPorEmergencia,
                cambiarEstadoAlAsignar,
                permitirFinalizarSinRecursos);
    }

    public ParametrosAsignacion getParametros() {
        return parametros;
    }

    // ==================== OPERADOR ====================

    public void registrarEmergencia(Usuario usuario, String descripcion,
                                    String ubicacion, TipoEmergencia tipo) {
        exigir(usuario, Permiso.REGISTRAR_EMERGENCIA);
        emergencias.add(new Emergencia(
                siguienteIdEmergencia++,
                descripcion, ubicacion, tipo));
    }

    public void asignarRecurso(Usuario usuario, int idEmergencia, int idRecurso) {
        exigir(usuario, Permiso.ASIGNAR_RECURSO);

        Emergencia emergencia = buscarEmergencia(idEmergencia);
        Recurso recurso = buscarRecurso(idRecurso);

        if (!recurso.isDisponible()) {
            throw new IllegalArgumentException(
                    "El recurso no está disponible.");
        }

        if (emergencia.getEstado().esFinal()) {
            throw new IllegalArgumentException(
                    "La emergencia ya fue finalizada.");
        }

        if (emergencia.getRecursos().size()
                >= parametros.getMaxRecursosPorEmergencia()) {
            throw new IllegalArgumentException(
                    "La emergencia alcanzó el máximo de "
                            + parametros.getMaxRecursosPorEmergencia()
                            + " recursos asignados.");
        }

        emergencia.asignarRecurso(recurso);
        recurso.setDisponible(false);

        if (parametros.isCambiarEstadoAlAsignar()
                && emergencia.getEstado() == Estado.PENDIENTE) {
            emergencia.actualizarEstado(Estado.EN_ATENCION);
        }
    }

    public void actualizarEstado(Usuario usuario, int id, Estado estado) {
        exigir(usuario, Permiso.ACTUALIZAR_ESTADO);
        buscarEmergencia(id).actualizarEstado(estado);
    }

    // Permite dejar constancia de la atención sin cerrar la emergencia.
    public void registrarAtencion(Usuario usuario, int id, String detalle) {
        exigir(usuario, Permiso.REGISTRAR_ATENCION);
        buscarEmergencia(id).registrarAtencion(detalle);
    }

    public void finalizarAtencion(Usuario usuario, int id, String detalle) {
        exigir(usuario, Permiso.FINALIZAR_ATENCION);

        Emergencia emergencia = buscarEmergencia(id);

        if (emergencia.getEstado().esFinal()) {
            throw new IllegalArgumentException(
                    "La emergencia ya fue finalizada.");
        }

        if (!parametros.isPermitirFinalizarSinRecursos()
                && emergencia.getRecursos().isEmpty()) {
            throw new IllegalArgumentException(
                    "No se puede finalizar una emergencia sin recursos asignados.");
        }

        emergencia.registrarAtencion(detalle);
        emergencia.marcarFinalizada();

        for (Recurso recurso : emergencia.getRecursos()) {
            recurso.setDisponible(true);
        }
    }

    // ==================== CONSULTAS ====================

    public String listarEmergencias() {
        return listarEmergenciasPorEstado(null);
    }

    // Muestra solo las emergencias que ya fueron atendidas.
    public String listarEmergenciasAtendidas() {
        return listarEmergenciasPorEstado(Estado.FINALIZADA);
    }

    public String listarEmergenciasPendientes() {
        return listarEmergenciasPorEstado(Estado.PENDIENTE);
    }

    // Si filtro es null devuelve todas.
    private String listarEmergenciasPorEstado(Estado filtro) {
        StringBuilder texto = new StringBuilder();
        int encontradas = 0;

        for (Emergencia emergencia : emergencias) {
            if (filtro != null && emergencia.getEstado() != filtro) {
                continue;
            }
            texto.append(emergencia).append("\n\n");
            encontradas++;
        }

        return encontradas == 0
                ? "No hay emergencias para mostrar."
                : texto.toString();
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

    // Muestra únicamente los recursos libres para asignar.
    public String listarRecursosDisponibles() {
        return listarRecursos(true);
    }

    public List<Recurso> getRecursosDisponibles() {
        List<Recurso> disponibles = new ArrayList<>();

        for (Recurso recurso : recursos) {
            if (recurso.isDisponible()) {
                disponibles.add(recurso);
            }
        }
        return disponibles;
    }

    // ==================== INTERNOS ====================

    // Punto único de control de acceso por rol.
    private void exigir(Usuario usuario, Permiso permiso) {
        if (usuario == null) {
            throw new SecurityException("No hay usuario en sesión.");
        }
        if (!usuario.puede(permiso)) {
            throw new SecurityException(
                    "El rol " + usuario.getRol() + " no tiene permiso para "
                            + permiso.getDescripcion() + ".");
        }
    }

    private Emergencia buscarEmergencia(int id) {
        for (Emergencia emergencia : emergencias) {
            if (emergencia.getId() == id) {
                return emergencia;
            }
        }
        throw new IllegalArgumentException(
                "No existe la emergencia con ID " + id + ".");
    }

    private Recurso buscarRecurso(int id) {
        for (Recurso recurso : recursos) {
            if (recurso.getId() == id) {
                return recurso;
            }
        }
        throw new IllegalArgumentException(
                "No existe el recurso con ID " + id + ".");
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
}