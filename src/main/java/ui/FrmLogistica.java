package ui;

import excepciones.AlmacenNoEncontradoException;
import modelos.Almacen;
import servicios.LogisticaService;
import estructuras.GrafoLogistico;
import estructuras.GrafoLogistico.ResultadoDijkstra;
import estructuras.GrafoLogistico.VerticeLogistico;
import estructuras.ListaEnlazada;

import javax.swing.*;
import java.awt.*;

/** Interfaz grafica de usuario. */
public class FrmLogistica extends JDialog {

    //  Paleta profesional clara 
    private static final Color BG_FORM = new Color(249, 250, 251);
    private static final Color BG_WHITE = Color.WHITE;
    private static final Color ACCENT = new Color(37, 99, 235);
    private static final Color TEXT_PRI = new Color(30, 41, 59);
    private static final Color TEXT_SEC = new Color(100, 116, 139);
    private static final Color BORDER = new Color(226, 232, 240);
    private static final Color GREEN_BTN = new Color(22, 163, 74);
    private static final Color RED_BTN = new Color(220, 38, 38);

    private LogisticaService logisticaService;

    private JTextField txtIdAlmacen, txtNombre, txtDireccion, txtCapacidad;
    private JComboBox<String> cbTipoAlmacen;
    private JButton btnAgregarAlmacen;

    // Conectar rutas: JComboBox en lugar de JTextField
    private JComboBox<String> cbOrigenRuta, cbDestinoRuta;
    private JTextField txtDistancia, txtDescripcion;
    private JButton btnConectar;

    // Dijkstra: JComboBox en lugar de JTextField
    private JComboBox<String> cbRutaOrigen, cbRutaDestino;
    private JButton btnCalcularRuta;

    // Eliminar almacenes o rutas
    private JComboBox<String> cbEliminarAlmacen, cbEliminarOrigen, cbEliminarDestino;
    private JButton btnEliminarAlmacen, btnEliminarRuta;

    private JTextArea txtResultados;
    private JTextArea txtResultadoRuta; // Panel inferior para resultado Dijkstra

    /**


     * Constructor principal.


     */

    public FrmLogistica(Frame parent, LogisticaService logServ) {
        super(parent, "Gestión de Red Logística", true);
        this.logisticaService = logServ;

        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_FORM);

        initComponents();
        actualizarTextoRed();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        JPanel pnlIzquierdo = new JPanel();
        pnlIzquierdo.setLayout(new BoxLayout(pnlIzquierdo, BoxLayout.Y_AXIS));
        pnlIzquierdo.setBackground(BG_FORM);
        pnlIzquierdo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));

        JPanel pnlAgregar = crearSeccion("Nuevo Almacén");
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int fila = 1;
        gbc.gridx = 0; gbc.gridy = fila; gbc.weightx = 0;
        pnlAgregar.add(crearLabel("ID:"), gbc);
        txtIdAlmacen = crearTextField(14);
        txtIdAlmacen.setEditable(false);
        txtIdAlmacen.setText(logisticaService.generarCodigoAlmacen());
        gbc.gridx = 1; gbc.weightx = 1.0; pnlAgregar.add(txtIdAlmacen, gbc);

        fila++;
        gbc.gridx = 0; gbc.gridy = fila; gbc.weightx = 0;
        pnlAgregar.add(crearLabel("Nombre:"), gbc);
        txtNombre = crearTextField(14);
        gbc.gridx = 1; gbc.weightx = 1.0; pnlAgregar.add(txtNombre, gbc);

        fila++;
        gbc.gridx = 0; gbc.gridy = fila; gbc.weightx = 0;
        pnlAgregar.add(crearLabel("Dirección:"), gbc);
        txtDireccion = crearTextField(14);
        txtDireccion.setToolTipText("Formato: Distrito, Av./Calle y Número. Ej: Lima, Av. Venezuela Cdra 34");
        gbc.gridx = 1; gbc.weightx = 1.0; pnlAgregar.add(txtDireccion, gbc);

        fila++;
        gbc.gridx = 0; gbc.gridy = fila; gbc.weightx = 0;
        pnlAgregar.add(crearLabel("Capacidad:"), gbc);
        txtCapacidad = crearTextField(14);
        gbc.gridx = 1; gbc.weightx = 1.0; pnlAgregar.add(txtCapacidad, gbc);

        fila++;
        gbc.gridx = 0; gbc.gridy = fila; gbc.weightx = 0;
        pnlAgregar.add(crearLabel("Tipo:"), gbc);
        cbTipoAlmacen = new JComboBox<>(new String[]{"CENTRAL", "REGIONAL", "TIENDA", "HUB"});
        cbTipoAlmacen.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridx = 1; gbc.weightx = 1.0; pnlAgregar.add(cbTipoAlmacen, gbc);

        fila++;
        btnAgregarAlmacen = crearBotonAccion("Agregar Almacén", ACCENT);
        gbc.gridx = 0; gbc.gridy = fila; gbc.gridwidth = 2; gbc.weightx = 1.0;
        pnlAgregar.add(btnAgregarAlmacen, gbc);

        pnlIzquierdo.add(pnlAgregar);
        pnlIzquierdo.add(Box.createVerticalStrut(8));

        JPanel pnlConectar = crearSeccion("Conectar Rutas");
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 1;
        fila = 1;

        gbc.gridx = 0; gbc.gridy = fila; gbc.weightx = 0;
        pnlConectar.add(crearLabel("Origen:"), gbc);
        cbOrigenRuta = new JComboBox<>();
        cbOrigenRuta.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridx = 1; gbc.weightx = 1.0; pnlConectar.add(cbOrigenRuta, gbc);

        fila++;
        gbc.gridx = 0; gbc.gridy = fila; gbc.weightx = 0;
        pnlConectar.add(crearLabel("Destino:"), gbc);
        cbDestinoRuta = new JComboBox<>();
        cbDestinoRuta.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridx = 1; gbc.weightx = 1.0; pnlConectar.add(cbDestinoRuta, gbc);

        fila++;
        gbc.gridx = 0; gbc.gridy = fila; gbc.weightx = 0;
        pnlConectar.add(crearLabel("Distancia (km):"), gbc);
        txtDistancia = crearTextField(14);
        gbc.gridx = 1; gbc.weightx = 1.0; pnlConectar.add(txtDistancia, gbc);

        fila++;
        gbc.gridx = 0; gbc.gridy = fila; gbc.weightx = 0;
        pnlConectar.add(crearLabel("Descripción:"), gbc);
        txtDescripcion = crearTextField(14);
        gbc.gridx = 1; gbc.weightx = 1.0; pnlConectar.add(txtDescripcion, gbc);

        fila++;
        btnConectar = crearBotonAccion("Crear Ruta", GREEN_BTN);
        gbc.gridx = 0; gbc.gridy = fila; gbc.gridwidth = 2;
        pnlConectar.add(btnConectar, gbc);

        pnlIzquierdo.add(pnlConectar);
        pnlIzquierdo.add(Box.createVerticalStrut(8));

        JPanel pnlRuta = crearSeccion("Optimización de Rutas Logísticas");
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 1;
        fila = 1;

        gbc.gridx = 0; gbc.gridy = fila; gbc.weightx = 0;
        pnlRuta.add(crearLabel("Origen:"), gbc);
        cbRutaOrigen = new JComboBox<>();
        cbRutaOrigen.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridx = 1; gbc.weightx = 1.0; pnlRuta.add(cbRutaOrigen, gbc);

        fila++;
        gbc.gridx = 0; gbc.gridy = fila; gbc.weightx = 0;
        pnlRuta.add(crearLabel("Destino:"), gbc);
        cbRutaDestino = new JComboBox<>();
        cbRutaDestino.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridx = 1; gbc.weightx = 1.0; pnlRuta.add(cbRutaDestino, gbc);

        fila++;
        btnCalcularRuta = crearBotonAccion("Calcular Ruta Óptima", ACCENT);
        gbc.gridx = 0; gbc.gridy = fila; gbc.gridwidth = 2;
        pnlRuta.add(btnCalcularRuta, gbc);

        pnlIzquierdo.add(pnlRuta);
        pnlIzquierdo.add(Box.createVerticalStrut(8));

        JPanel pnlEliminar = crearSeccion("Eliminar Almacén o Ruta");
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 1;
        fila = 1;

        gbc.gridx = 0; gbc.gridy = fila; gbc.weightx = 0;
        pnlEliminar.add(crearLabel("Almacén:"), gbc);
        cbEliminarAlmacen = new JComboBox<>();
        cbEliminarAlmacen.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridx = 1; gbc.weightx = 1.0; pnlEliminar.add(cbEliminarAlmacen, gbc);

        fila++;
        btnEliminarAlmacen = crearBotonAccion("Eliminar Almacén", RED_BTN);
        gbc.gridx = 0; gbc.gridy = fila; gbc.gridwidth = 2;
        pnlEliminar.add(btnEliminarAlmacen, gbc);

        fila++;
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = fila; gbc.weightx = 0;
        pnlEliminar.add(crearLabel("Ruta Origen:"), gbc);
        cbEliminarOrigen = new JComboBox<>();
        cbEliminarOrigen.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridx = 1; gbc.weightx = 1.0; pnlEliminar.add(cbEliminarOrigen, gbc);

        fila++;
        gbc.gridx = 0; gbc.gridy = fila; gbc.weightx = 0;
        pnlEliminar.add(crearLabel("Ruta Destino:"), gbc);
        cbEliminarDestino = new JComboBox<>();
        cbEliminarDestino.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridx = 1; gbc.weightx = 1.0; pnlEliminar.add(cbEliminarDestino, gbc);

        fila++;
        btnEliminarRuta = crearBotonAccion("Eliminar Ruta", RED_BTN);
        gbc.gridx = 0; gbc.gridy = fila; gbc.gridwidth = 2;
        pnlEliminar.add(btnEliminarRuta, gbc);

        // pnlEliminar se agrega al panel derecho
        
        JScrollPane scrollIzquierdo = new JScrollPane(pnlIzquierdo);
        scrollIzquierdo.setPreferredSize(new Dimension(450, 0));
        scrollIzquierdo.setBorder(null);
        scrollIzquierdo.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollIzquierdo, BorderLayout.WEST);

        JPanel pnlDerecho = new JPanel(new BorderLayout(0, 8));
        pnlDerecho.setBackground(BG_FORM);
        pnlDerecho.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // Red logística (texto monoespaciado)
        txtResultados = new JTextArea();
        txtResultados.setEditable(false);
        txtResultados.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtResultados.setBackground(BG_WHITE);
        txtResultados.setForeground(TEXT_PRI);
        txtResultados.setMargin(new Insets(12, 12, 12, 12));
        JScrollPane scrollRed = new JScrollPane(txtResultados);
        scrollRed.setPreferredSize(new Dimension(600, 250)); // Reducido drásticamente
        scrollRed.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER),
                "Red Logística Actual",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12), TEXT_PRI));
        pnlDerecho.add(scrollRed, BorderLayout.CENTER);

        // Resultado de Dijkstra (panel inferior con tarjeta)
        txtResultadoRuta = new JTextArea(4, 30);
        txtResultadoRuta.setEditable(false);
        txtResultadoRuta.setFont(new Font("Segoe UI", Font.BOLD, 13));
        txtResultadoRuta.setBackground(new Color(240, 253, 244)); // Verde muy suave
        txtResultadoRuta.setForeground(new Color(21, 128, 61));
        txtResultadoRuta.setMargin(new Insets(10, 14, 10, 14));
        txtResultadoRuta.setText("Seleccione origen y destino para calcular la ruta óptima.");
        JScrollPane scrollRuta = new JScrollPane(txtResultadoRuta);
        scrollRuta.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(187, 247, 208)),
                "Resultado de Ruta Óptima",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12), new Color(21, 128, 61)));
        scrollRuta.setPreferredSize(new Dimension(0, 100));
        
        JPanel pnlSurDerecho = new JPanel(new BorderLayout(0, 8));
        pnlSurDerecho.setBackground(BG_FORM);
        pnlSurDerecho.add(pnlEliminar, BorderLayout.CENTER);
        pnlSurDerecho.add(scrollRuta, BorderLayout.SOUTH);

        pnlDerecho.add(pnlSurDerecho, BorderLayout.SOUTH);

        add(pnlDerecho, BorderLayout.CENTER);

        cargarCombosAlmacenes();

        btnAgregarAlmacen.addActionListener(e -> {
            try {
                String id = txtIdAlmacen.getText();
                String nombre = txtNombre.getText();
                String dir = txtDireccion.getText();
                int cap = Integer.parseInt(txtCapacidad.getText());
                String tipo = (String) cbTipoAlmacen.getSelectedItem();
                Almacen.TipoAlmacen tipoEnum = Almacen.TipoAlmacen.valueOf(tipo);

                Almacen a = new Almacen(id, nombre, dir, tipoEnum, cap);
                logisticaService.agregarAlmacen(a);
                JOptionPane.showMessageDialog(this, "Almacén '" + id + "' agregado correctamente.");

                txtIdAlmacen.setText(logisticaService.generarCodigoAlmacen());
                txtNombre.setText(""); txtDireccion.setText(""); txtCapacidad.setText("");
                cargarCombosAlmacenes();
                actualizarTextoRed();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Verifique los datos ingresados.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnConectar.addActionListener(e -> {
            try {
                String o = extraerIdAlmacen(cbOrigenRuta);
                String d = extraerIdAlmacen(cbDestinoRuta);
                if (o == null || d == null) {
                    JOptionPane.showMessageDialog(this, "Seleccione origen y destino.",
                            "Advertencia", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                double dist = Double.parseDouble(txtDistancia.getText());
                if (dist < 0) {
                    JOptionPane.showMessageDialog(this, "La distancia no puede ser negativa.",
                            "Error de Validación", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String desc = txtDescripcion.getText();

                logisticaService.conectarAlmacenes(o, d, dist, desc);
                JOptionPane.showMessageDialog(this, "Ruta conectada: " + o + " ↔ " + d);
                actualizarTextoRed();
            } catch (AlmacenNoEncontradoException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Verifique los datos (distancia debe ser numérico).",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCalcularRuta.addActionListener(e -> {
            String o = extraerIdAlmacen(cbRutaOrigen);
            String d = extraerIdAlmacen(cbRutaDestino);
            if (o == null || d == null) {
                JOptionPane.showMessageDialog(this, "Seleccione origen y destino.",
                        "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                ResultadoDijkstra res = logisticaService.getGrafo().dijkstra(o, d);
                if (res.esAlcanzable) {
                    StringBuilder ruta = new StringBuilder();
                    for (int i = 0; i < res.camino.size(); i++) {
                        ruta.append(res.camino.get(i));
                        if (i < res.camino.size() - 1) ruta.append(" -> ");
                    }
                    txtResultadoRuta.setBackground(new Color(240, 253, 244));
                    txtResultadoRuta.setForeground(new Color(21, 128, 61));
                    txtResultadoRuta.setText(
                        "Ruta Óptima Encontrada\n" +
                        "Recorrido: " + ruta.toString() + "\n" +
                        "Distancia Total: " + String.format("%.2f", res.distanciaTotal) + " km"
                    );
                } else {
                    txtResultadoRuta.setBackground(new Color(254, 242, 242));
                    txtResultadoRuta.setForeground(new Color(185, 28, 28));
                    txtResultadoRuta.setText(
                        "⚠ No existe ruta posible entre " + o + " y " + d + "."
                    );
                }
            } catch (AlmacenNoEncontradoException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnEliminarAlmacen.addActionListener(e -> {
            String id = extraerIdAlmacen(cbEliminarAlmacen);
            if (id == null) {
                JOptionPane.showMessageDialog(this, "Seleccione un almacén para eliminar.",
                        "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro de eliminar el almacén '" + id + "'? Se eliminarán todas sus conexiones.",
                    "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                boolean exito = logisticaService.eliminarAlmacen(id);
                if (exito) {
                    JOptionPane.showMessageDialog(this, "Almacén '" + id + "' eliminado correctamente.");
                    txtIdAlmacen.setText(logisticaService.generarCodigoAlmacen());
                    cargarCombosAlmacenes();
                    actualizarTextoRed();
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo eliminar el almacén.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnEliminarRuta.addActionListener(e -> {
            String o = extraerIdAlmacen(cbEliminarOrigen);
            String d = extraerIdAlmacen(cbEliminarDestino);
            if (o == null || d == null) {
                JOptionPane.showMessageDialog(this, "Seleccione origen y destino.",
                        "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (o.equalsIgnoreCase(d)) {
                JOptionPane.showMessageDialog(this, "El origen y destino no pueden ser iguales.",
                        "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro de eliminar la ruta entre '" + o + "' y '" + d + "'?",
                    "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                boolean exito = logisticaService.eliminarRuta(o, d);
                if (exito) {
                    JOptionPane.showMessageDialog(this, "Ruta entre '" + o + "' y '" + d + "' eliminada correctamente.");
                    actualizarTextoRed();
                } else {
                    JOptionPane.showMessageDialog(this, "No existe una ruta directa entre '" + o + "' y '" + d + "'.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    //  Helpers 

    /**
     * Carga los 4 JComboBox de almacenes con los vértices del grafo.
     * Formato: "ALM-01 — Central Lima"
     */
    private void cargarCombosAlmacenes() {
        cbOrigenRuta.removeAllItems();
        cbDestinoRuta.removeAllItems();
        cbRutaOrigen.removeAllItems();
        cbRutaDestino.removeAllItems();
        cbEliminarAlmacen.removeAllItems();
        cbEliminarOrigen.removeAllItems();
        cbEliminarDestino.removeAllItems();

        ListaEnlazada<VerticeLogistico> vertices = logisticaService.getGrafo().getVertices();
        for (VerticeLogistico v : vertices) {
            String item = v.almacen.getId() + " — " + v.almacen.getNombre();
            cbOrigenRuta.addItem(item);
            cbDestinoRuta.addItem(item);
            cbRutaOrigen.addItem(item);
            cbRutaDestino.addItem(item);
            cbEliminarAlmacen.addItem(item);
            cbEliminarOrigen.addItem(item);
            cbEliminarDestino.addItem(item);
        }
    }

    /** Extrae el ID (ej. "ALM-01") del item seleccionado en un combo ("ALM-01 — Nombre"). */
    private String extraerIdAlmacen(JComboBox<String> combo) {
        String sel = (String) combo.getSelectedItem();
        if (sel == null || sel.isEmpty()) return null;
        return sel.split(" — ")[0].trim();
    }

    private JPanel crearSeccion(String titulo) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 6, 0);
        JLabel lblTit = new JLabel(titulo);
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTit.setForeground(TEXT_PRI);
        panel.add(lblTit, gbc);
        return panel;
    }

    private JLabel crearLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(TEXT_PRI);
        return lbl;
    }

    private JTextField crearTextField(int cols) {
        JTextField tf = new JTextField(cols);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return tf;
    }

    private JButton crearBotonAccion(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setOpaque(true);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(7, 16, 7, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void actualizarTextoRed() {
        txtResultados.setText(logisticaService.getGrafo().mostrarRed());
    }
}
