package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

// Ventana principal en Swing que muestra el login, el menú según el rol y los diálogos de cada acción.
public class SistemaEmergenciasApp extends JFrame {
    private SistemaEmergencias sistema;
    private JTextArea area;
    private Usuario usuario;

    public SistemaEmergenciasApp() {
        sistema = new SistemaEmergencias();

        setTitle("Sistema de Gestión de Emergencias");
        setSize(750, 500);
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

        JButton emergencias = new JButton("Ver emergencias");
        JButton recursos = new JButton("Ver recursos");
        JButton salir = new JButton("Cerrar sesión");

        emergencias.addActionListener(e -> mostrarEmergencias());
        recursos.addActionListener(e -> mostrarRecursos());
        salir.addActionListener(e -> mostrarInicio());

        botones.add(emergencias);
        botones.add(recursos);

        if (usuario instanceof Administrador) {
            JButton tipo = new JButton("Crear tipo");
            JButton recurso = new JButton("Registrar recurso");

            tipo.addActionListener(e -> crearTipo());
            recurso.addActionListener(e -> crearRecurso());

            botones.add(tipo);
            botones.add(recurso);
        } else {
            JButton registrar = new JButton("Registrar emergencia");
            JButton asignar = new JButton("Asignar recurso");
            JButton estado = new JButton("Actualizar estado");
            JButton finalizar = new JButton("Finalizar atención");

            registrar.addActionListener(e -> registrarEmergencia());
            asignar.addActionListener(e -> asignarRecurso());
            estado.addActionListener(e -> actualizarEstado());
            finalizar.addActionListener(e -> finalizarAtencion());

            botones.add(registrar);
            botones.add(asignar);
            botones.add(estado);
            botones.add(finalizar);
        }

        botones.add(salir);

        principal.add(encabezado, BorderLayout.NORTH);
        principal.add(scroll, BorderLayout.CENTER);
        principal.add(botones, BorderLayout.SOUTH);

        setContentPane(principal);
        mostrarEmergencias();

        revalidate();
        repaint();
    }

    private void crearTipo() {
        String nombre = JOptionPane.showInputDialog(
                this, "Nombre del tipo de emergencia:");

        if (nombre == null || nombre.trim().isEmpty()) {
            return;
        }

        String valor = JOptionPane.showInputDialog(
                this, "Prioridad (1 = alta, número mayor = menor prioridad):");

        try {
            int prioridad = Integer.parseInt(valor);
            if (prioridad < 1) {
                mensaje("La prioridad debe ser mayor que cero.");
                return;
            }

            sistema.registrarTipo(nombre.trim(), prioridad);
            mensaje("Tipo de emergencia registrado.");
        } catch (NumberFormatException ex) {
            mensaje("Escribe una prioridad numérica válida.");
        }
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

        String nombre = JOptionPane.showInputDialog(
                this, "Nombre del recurso:");

        if (nombre == null || nombre.trim().isEmpty()) {
            return;
        }

        String detalle;

        if (clase.equals("Vehículo")) {
            detalle = JOptionPane.showInputDialog(this, "Placa:");
        } else if (clase.equals("Personal")) {
            detalle = JOptionPane.showInputDialog(this, "Cargo:");
        } else {
            detalle = JOptionPane.showInputDialog(this, "Tipo de equipo:");
        }

        if (detalle == null || detalle.trim().isEmpty()) {
            return;
        }

        sistema.registrarRecurso(clase, nombre.trim(), detalle.trim());
        mensaje("Recurso registrado correctamente.");
    }

    private void registrarEmergencia() {
        String descripcion = JOptionPane.showInputDialog(
                this, "Descripción de la emergencia:");

        if (descripcion == null || descripcion.trim().isEmpty()) {
            return;
        }

        String ubicacion = JOptionPane.showInputDialog(
                this, "Ubicación de la emergencia:");

        if (ubicacion == null || ubicacion.trim().isEmpty()) {
            return;
        }

        List<TipoEmergencia> tipos = sistema.getTipos();

        if (tipos.isEmpty()) {
            mensaje("El administrador debe crear un tipo de emergencia.");
            return;
        }

        TipoEmergencia tipo = (TipoEmergencia) JOptionPane.showInputDialog(
                this, "Selecciona el tipo de emergencia:",
                "Registrar emergencia", JOptionPane.QUESTION_MESSAGE,
                null, tipos.toArray(), tipos.get(0));

        if (tipo == null) {
            return;
        }

        sistema.registrarEmergencia(
                descripcion.trim(), ubicacion.trim(), tipo);

        mensaje("Emergencia registrada correctamente.");
        mostrarEmergencias();
    }

    private void asignarRecurso() {
        if (sistema.getEmergencias().isEmpty()) {
            mensaje("No hay emergencias registradas.");
            return;
        }

        List<Recurso> disponibles = new ArrayList<>();

        for (Recurso recurso : sistema.getRecursos()) {
            if (recurso.isDisponible()) {
                disponibles.add(recurso);
            }
        }

        if (disponibles.isEmpty()) {
            mensaje("No hay recursos disponibles.");
            return;
        }

        String valorEmergencia = JOptionPane.showInputDialog(
                this, "ID de la emergencia:\n"
                        + sistema.listarEmergencias());

        if (valorEmergencia == null) {
            return;
        }

        String[] opciones = new String[disponibles.size()];

        for (int i = 0; i < disponibles.size(); i++) {
            Recurso r = disponibles.get(i);
            opciones[i] = r.getId() + " - " + r.descripcion();
        }

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

            sistema.asignarRecurso(idEmergencia, idRecurso);
            mensaje("Recurso asignado correctamente.");
            mostrarEmergencias();
        } catch (NumberFormatException ex) {
            mensaje("Escribe un ID de emergencia válido.");
        } catch (IllegalArgumentException ex) {
            mensaje(ex.getMessage());
        }
    }

    private void actualizarEstado() {
        String id = JOptionPane.showInputDialog(
                this, "ID de la emergencia:\n"
                        + sistema.listarEmergencias());

        if (id == null) {
            return;
        }

        String[] estados = {"Pendiente", "En atención"};

        String estado = (String) JOptionPane.showInputDialog(
                this, "Nuevo estado:", "Actualizar estado",
                JOptionPane.QUESTION_MESSAGE, null, estados, estados[0]);

        if (estado == null) {
            return;
        }

        try {
            sistema.actualizarEstado(Integer.parseInt(id.trim()), estado);
            mensaje("Estado actualizado.");
            mostrarEmergencias();
        } catch (NumberFormatException ex) {
            mensaje("El ID debe ser numérico.");
        } catch (IllegalArgumentException ex) {
            mensaje(ex.getMessage());
        }
    }

    private void finalizarAtencion() {
        String id = JOptionPane.showInputDialog(
                this, "ID de la emergencia:\n"
                        + sistema.listarEmergencias());

        if (id == null) {
            return;
        }

        String detalle = JOptionPane.showInputDialog(
                this, "Describe la atención realizada:");

        if (detalle == null || detalle.trim().isEmpty()) {
            return;
        }

        try {
            sistema.finalizarAtencion(
                    Integer.parseInt(id.trim()), detalle.trim());
            mensaje("Atención finalizada. Recursos liberados.");
            mostrarEmergencias();
        } catch (NumberFormatException ex) {
            mensaje("El ID debe ser numérico.");
        } catch (IllegalArgumentException ex) {
            mensaje(ex.getMessage());
        }
    }

    private void mostrarEmergencias() {
        area.setText(sistema.listarEmergencias());
    }

    private void mostrarRecursos() {
        area.setText(sistema.listarRecursos(false));
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