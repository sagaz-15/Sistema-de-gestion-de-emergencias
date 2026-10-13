package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Ventana principal en Swing: login, menú según permisos y diálogos de cada acción.
 * Los botones se habilitan según lo que el usuario puede hacer (Usuario.puede),
 * y el modelo vuelve a validar cada operación.
 */
public class SistemaEmergenciasApp extends JFrame {
    private SistemaEmergencias sistema;
    private JTextArea area;
    private Usuario usuario;

    public SistemaEmergenciasApp() {
        sistema = new SistemaEmergencias();

        setTitle("Sistema de Gestión de Emergencias");
        setSize(800, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        mostrarInicio();
    }

    private void mostrarInicio() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));

        JLabel titulo = new JLabel(
                "SISTEMA DE GESTIÓN DE EMERGENCIAS",
                SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));

        JTextField nombre = new JTextField();
        JComboBox<String> rol = new JComboBox<>(
                new String[]{"Administrador", "Operador"});
        JButton entrar = new JButton("Iniciar sesión");

        panel.add(titulo);
        panel.add(nombre);
        panel.add(rol);
        panel.add(entrar);

        nombre.setBorder(BorderFactory.createTitledBorder("Nombre de usuario"));

        setContentPane(panel);

        entrar.addActionListener(e -> {
            if (nombre.getText().trim().isEmpty()) {
                mensaje("Escribe tu nombre de usuario.");
                return;
            }

            if (rol.getSelectedItem().equals("Administrador")) {
                usuario = new Administrador(nombre.getText().trim());
            } else {
                usuario = new Operador(nombre.getText().trim());
            }

            mostrarMenu();
        });

        revalidate();
        repaint();
    }

    private void mostrarMenu() {
        JPanel principal = new JPanel(new BorderLayout(10, 10));

        JLabel encabezado = new JLabel(
                "Usuario: " + usuario.getNombre()
                        + " | Rol: " + usuario.getRol());
        encabezado.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 13));

        JScrollPane scroll = new JScrollPane(area);
        JPanel botones = new JPanel(new FlowLayout());

        // Consultas: los dos roles pueden ver.
        botones.add(boton("Ver emergencias", this::mostrarEmergencias));
        botones.add(boton("Ver atendidas", this::mostrarAtendidas));
        botones.add(boton("Ver recursos", this::mostrarRecursos));
        botones.add(boton("Ver disponibles", this::mostrarDisponibles));

        // Se agrega cada botón solo si el usuario tiene ese permiso.
        if (usuario.puede(Permiso.REGISTRAR_TIPO)) {
            botones.add(boton("Crear tipo", this::crearTipo));
        }
        if (usuario.puede(Permiso.CONFIGURAR_PRIORIDAD)) {
            botones.add(boton("Cambiar prioridad", this::cambiarPrioridad));
        }
        if (usuario.puede(Permiso.REGISTRAR_RECURSO)) {
            botones.add(boton("Registrar recurso", this::crearRecurso));
        }
        if (usuario.puede(Permiso.CONFIGURAR_ASIGNACION)) {
            botones.add(boton("Parámetros de asignación", this::configurarAsignacion));
        }
        if (usuario.puede(Permiso.REGISTRAR_EMERGENCIA)) {
            botones.add(boton("Registrar emergencia", this::registrarEmergencia));
        }
        if (usuario.puede(Permiso.ASIGNAR_RECURSO)) {
            botones.add(boton("Asignar recurso", this::asignarRecurso));
        }
        if (usuario.puede(Permiso.ACTUALIZAR_ESTADO)) {
            botones.add(boton("Actualizar estado", this::actualizarEstado));
        }
        if (usuario.puede(Permiso.REGISTRAR_ATENCION)) {
            botones.add(boton("Registrar atención", this::registrarAtencion));
        }
        if (usuario.puede(Permiso.FINALIZAR_ATENCION)) {
            botones.add(boton("Finalizar atención", this::finalizarAtencion));
        }

        botones.add(boton("Cerrar sesión", this::mostrarInicio));

        principal.add(encabezado, BorderLayout.NORTH);
        principal.add(scroll, BorderLayout.CENTER);
        principal.add(botones, BorderLayout.SOUTH);

        setContentPane(principal);
        mostrarEmergencias();

        revalidate();
        repaint();
    }

    private JButton boton(String texto, Runnable accion) {
        JButton b = new JButton(texto);
        b.addActionListener(e -> accion.run());
        return b;
    }

    // ================== ADMINISTRADOR ==================

    private void crearTipo() {
        String nombre = pedir("Nombre del tipo de emergencia:");
        if (nombre == null) {
            return;
        }

        Integer prioridad = pedirPrioridad();
        if (prioridad == null) {
            return;
        }

        ejecutar(() -> {
            sistema.registrarTipo(usuario, nombre, prioridad);
            mensaje("Tipo de emergencia registrado.");
        });
    }

    private void cambiarPrioridad() {
        if (sistema.getTipos().isEmpty()) {
            mensaje("No hay tipos de emergencia registrados.");
            return;
        }

        String nombre = (String) JOptionPane.showInputDialog(
                this, "Tipos registrados:\n" + listarTipos(),
                "Cambiar prioridad", JOptionPane.QUESTION_MESSAGE,
                null, sistema.getTipos().stream()
                        .map(TipoEmergencia::getNombre).toArray(),
                sistema.getTipos().get(0).getNombre());

        if (nombre == null) {
            return;
        }

        Integer prioridad = pedirPrioridad();
        if (prioridad == null) {
            return;
        }

        ejecutar(() -> {
            sistema.cambiarPrioridad(usuario, nombre, prioridad);
            mensaje("Prioridad actualizada.");
        });
    }

    private void crearRecurso() {
        String[] clases = {"Vehículo", "Personal", "Equipo"};

        String clase = (String) JOptionPane.showInputDialog(
                this, "Selecciona el tipo de recurso:",
                "Registrar recurso", JOptionPane.QUESTION_MESSAGE,
                null, clases, clases[0]);

        if (clase == null) {
            return;
        }

        String nombre = pedir("Nombre del recurso:");
        if (nombre == null) {
            return;
        }

        String pregunta = switch (clase) {
            case "Vehículo" -> "Placa:";
            case "Personal" -> "Cargo:";
            default -> "Tipo de equipo:";
        };

        String detalle = pedir(pregunta);
        if (detalle == null) {
            return;
        }

        ejecutar(() -> {
            sistema.registrarRecurso(usuario, clase, nombre, detalle);
            mensaje("Recurso registrado correctamente.");
        });
    }

    private void configurarAsignacion() {
        ParametrosAsignacion p = sistema.getParametros();

        String entrada = JOptionPane.showInputDialog(this,
                "Máximo de recursos por emergencia (actual: "
                        + p.getMaxRecursosPorEmergencia() + "):");

        if (entrada == null) {
            return;
        }

        int max;
        try {
            max = Integer.parseInt(entrada.trim());
        } catch (NumberFormatException ex) {
            mensaje("Escribe un número válido.");
            return;
        }

        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Pasar la emergencia a \"En atención\" al asignar un recurso?",
                "Cambio de estado automático",
                JOptionPane.YES_NO_OPTION);

        if (opcion == JOptionPane.CLOSED_OPTION) {
            return;
        }

        int opcionFinal = JOptionPane.showConfirmDialog(this,
                "¿Permitir finalizar una emergencia sin recursos asignados?",
                "Finalización sin recursos",
                JOptionPane.YES_NO_OPTION);

        if (opcionFinal == JOptionPane.CLOSED_OPTION) {
            return;
        }

        boolean cambiarEstado = opcion == JOptionPane.YES_OPTION;
        boolean permitirSinRecursos = opcionFinal == JOptionPane.YES_OPTION;

        ejecutar(() -> {
            sistema.configurarParametros(usuario, max,
                    cambiarEstado, permitirSinRecursos);
            mensaje("Parámetros actualizados.");
        });
    }

    // ==================== OPERADOR ====================

    private void registrarEmergencia() {
        String descripcion = pedir("Descripción de la emergencia:");
        if (descripcion == null) {
            return;
        }

        String ubicacion = pedir("Ubicación de la emergencia:");
        if (ubicacion == null) {
            return;
        }

        if (sistema.getTipos().isEmpty()) {
            mensaje("El administrador debe crear un tipo de emergencia.");
            return;
        }

        TipoEmergencia tipo = (TipoEmergencia) JOptionPane.showInputDialog(
                this, "Selecciona el tipo de emergencia:",
                "Registrar emergencia", JOptionPane.QUESTION_MESSAGE,
                null, sistema.getTipos().toArray(),
                sistema.getTipos().get(0));

        if (tipo == null) {
            return;
        }

        ejecutar(() -> {
            sistema.registrarEmergencia(usuario, descripcion, ubicacion, tipo);
            mensaje("Emergencia registrada correctamente.");
            mostrarEmergencias();
        });
    }

    private void asignarRecurso() {
        if (sistema.getEmergencias().isEmpty()) {
            mensaje("No hay emergencias registradas.");
            return;
        }

        List<Recurso> disponibles = sistema.getRecursosDisponibles();

        if (disponibles.isEmpty()) {
            mensaje("No hay recursos disponibles.");
            return;
        }

        String valorEmergencia = JOptionPane.showInputDialog(
                this, "ID de la emergencia:\n" + sistema.listarEmergencias());

        if (valorEmergencia == null) {
            return;
        }

        String[] opciones = disponibles.stream()
                .map(r -> r.getId() + " - " + r.descripcion())
                .toArray(String[]::new);

        String seleccion = (String) JOptionPane.showInputDialog(
                this, "Selecciona un recurso disponible:",
                "Asignar recurso", JOptionPane.QUESTION_MESSAGE,
                null, opciones, opciones[0]);

        if (seleccion == null) {
            return;
        }

        try {
            int idEmergencia = Integer.parseInt(valorEmergencia.trim());
            int idRecurso = Integer.parseInt(seleccion.split(" - ")[0]);
            sistema.asignarRecurso(usuario, idEmergencia, idRecurso);
            mensaje("Recurso asignado correctamente.");
            mostrarEmergencias();
        } catch (NumberFormatException ex) {
            mensaje("Escribe un ID de emergencia válido.");
        } catch (IllegalArgumentException | SecurityException ex) {
            mensaje(ex.getMessage());
        }
    }

    private void actualizarEstado() {
        if (sistema.getEmergencias().isEmpty()) {
            mensaje("No hay emergencias registradas.");
            return;
        }

        String id = JOptionPane.showInputDialog(
                this, "ID de la emergencia:\n" + sistema.listarEmergencias());

        if (id == null) {
            return;
        }

        Estado estado = (Estado) JOptionPane.showInputDialog(
                this, "Nuevo estado:", "Actualizar estado",
                JOptionPane.QUESTION_MESSAGE, null,
                Estado.values(), Estado.PENDIENTE);

        if (estado == null) {
            return;
        }

        try {
            sistema.actualizarEstado(usuario,
                    Integer.parseInt(id.trim()), estado);
            mensaje("Estado actualizado.");
            mostrarEmergencias();
        } catch (NumberFormatException ex) {
            mensaje("El ID debe ser numérico.");
        } catch (IllegalArgumentException | IllegalStateException
                 | SecurityException ex) {
            mensaje(ex.getMessage());
        }
    }

    private void registrarAtencion() {
        if (sistema.getEmergencias().isEmpty()) {
            mensaje("No hay emergencias registradas.");
            return;
        }

        String id = JOptionPane.showInputDialog(
                this, "ID de la emergencia:\n" + sistema.listarEmergencias());

        if (id == null) {
            return;
        }

        String detalle = pedir("Describe la atención realizada:");
        if (detalle == null) {
            return;
        }

        try {
            sistema.registrarAtencion(usuario,
                    Integer.parseInt(id.trim()), detalle);
            mensaje("Atención registrada.");
            mostrarEmergencias();
        } catch (NumberFormatException ex) {
            mensaje("El ID debe ser numérico.");
        } catch (IllegalArgumentException | IllegalStateException
                 | SecurityException ex) {
            mensaje(ex.getMessage());
        }
    }

    private void finalizarAtencion() {
        if (sistema.getEmergencias().isEmpty()) {
            mensaje("No hay emergencias registradas.");
            return;
        }

        String id = JOptionPane.showInputDialog(
                this, "ID de la emergencia:\n" + sistema.listarEmergencias());

        if (id == null) {
            return;
        }

        String detalle = pedir("Describe la atención realizada:");
        if (detalle == null) {
            return;
        }

        try {
            sistema.finalizarAtencion(usuario,
                    Integer.parseInt(id.trim()), detalle);
            mensaje("Atención finalizada. Recursos liberados.");
            mostrarEmergencias();
        } catch (NumberFormatException ex) {
            mensaje("El ID debe ser numérico.");
        } catch (IllegalArgumentException | IllegalStateException
                 | SecurityException ex) {
            mensaje(ex.getMessage());
        }
    }

    // ==================== CONSULTAS ====================

    private void mostrarEmergencias() {
        area.setText(sistema.listarEmergencias());
    }

    private void mostrarAtendidas() {
        area.setText(sistema.listarEmergenciasAtendidas());
    }

    private void mostrarRecursos() {
        area.setText(sistema.listarRecursos(false));
    }

    private void mostrarDisponibles() {
        area.setText(sistema.listarRecursosDisponibles());
    }

    // ==================== UTILIDADES ====================

    // Devuelve el texto recortado, o null si el usuario canceló.
    private String pedir(String texto) {
        String valor = JOptionPane.showInputDialog(this, texto);
        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }
        return valor.trim();
    }

    private Integer pedirPrioridad() {
        String valor = JOptionPane.showInputDialog(this,
                "Prioridad (1 = alta, número mayor = menor prioridad):");

        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }

        try {
            int prioridad = Integer.parseInt(valor.trim());
            if (prioridad < 1) {
                mensaje("La prioridad debe ser mayor que cero.");
                return null;
            }
            return prioridad;
        } catch (NumberFormatException ex) {
            mensaje("Escribe una prioridad numérica válida.");
            return null;
        }
    }

    private String listarTipos() {
        StringBuilder texto = new StringBuilder();
        for (TipoEmergencia tipo : sistema.getTipos()) {
            texto.append(tipo).append("\n");
        }
        return texto.toString();
    }

    // Muestra el error del modelo sin que la app se caiga.
    private void ejecutar(Runnable accion) {
        try {
            accion.run();
        } catch (IllegalArgumentException | IllegalStateException
                 | SecurityException ex) {
            mensaje(ex.getMessage());
        }
    }

    private void mensaje(String texto) {
        JOptionPane.showMessageDialog(this, texto);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SistemaEmergenciasApp app = new SistemaEmergenciasApp();
            app.setVisible(true);
        });
    }
}