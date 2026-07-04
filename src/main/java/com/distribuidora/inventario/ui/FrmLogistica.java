package com.distribuidora.inventario.ui;

import com.distribuidora.inventario.exceptions.AlmacenNoEncontradoException;
import com.distribuidora.inventario.models.Almacen;
import com.distribuidora.inventario.services.LogisticaService;
import com.distribuidora.inventario.structures.GrafoLogistico.ResultadoDijkstra;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class FrmLogistica extends JDialog {

    // ── Paleta profesional clara ──
    private static final Color BG_FORM  = new Color(249, 250, 251);
    private static final Color BG_WHITE = Color.WHITE;
    private static final Color ACCENT   = new Color(37, 99, 235);
    private static final Color TEXT_PRI = new Color(30, 41, 59);
    private static final Color TEXT_SEC = new Color(100, 116, 139);
    private static final Color BORDER   = new Color(226, 232, 240);
    private static final Color GREEN_BTN = new Color(22, 163, 74);

    private LogisticaService logisticaService;

    private JTextField txtIdAlmacen, txtNombre, txtDireccion, txtCapacidad;
    private JComboBox<String> cbTipo;
    private JButton btnAgregarAlmacen;

    private JTextField txtOrigen, txtDestino, txtDistancia, txtDescripcion;
    private JButton btnConectar;

    private JTextField txtRutaOrigen, txtRutaDestino;
    private JButton btnCalcularRuta;
    private JTextArea txtResultados;

    public FrmLogistica(Frame parent, LogisticaService logServ) {
        super(parent, "Gestión Logística (Grafos)", true);
        this.logisticaService = logServ;

        setSize(960, 620);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_FORM);

        initComponents();
        actualizarTextoRed();
    }

    private void initComponents() {
        JPanel pnlIzquierdo = new JPanel(new GridLayout(3, 1, 0, 8));
        pnlIzquierdo.setPreferredSize(new Dimension(370, 0));
        pnlIzquierdo.setBackground(BG_FORM);
        pnlIzquierdo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));

        // ─── 1. NUEVO ALMACÉN ───
        JPanel pnlAgregar = crearSeccion("Nuevo Almacén");
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int fila = 1;
        gbc.gridx = 0; gbc.gridy = fila; pnlAgregar.add(crearLabel("ID:"), gbc);
        txtIdAlmacen = crearTextField(14);
        txtIdAlmacen.setEditable(false);
        txtIdAlmacen.setText(logisticaService.generarCodigoAlmacen());
        gbc.gridx = 1; pnlAgregar.add(txtIdAlmacen, gbc);

        fila++;
        gbc.gridx = 0; gbc.gridy = fila; pnlAgregar.add(crearLabel("Nombre:"), gbc);
        txtNombre = crearTextField(14);
        gbc.gridx = 1; pnlAgregar.add(txtNombre, gbc);

        fila++;
        gbc.gridx = 0; gbc.gridy = fila; pnlAgregar.add(crearLabel("Dirección:"), gbc);
        txtDireccion = crearTextField(14);
        txtDireccion.setToolTipText("Formato: Distrito, Av./Calle y Número. Ej: Lima, Av. Venezuela Cdra 34");
        gbc.gridx = 1; pnlAgregar.add(txtDireccion, gbc);

        fila++;
        gbc.gridx = 0; gbc.gridy = fila; pnlAgregar.add(crearLabel("Capacidad:"), gbc);
        txtCapacidad = crearTextField(14);
        gbc.gridx = 1; pnlAgregar.add(txtCapacidad, gbc);

        fila++;
        gbc.gridx = 0; gbc.gridy = fila; pnlAgregar.add(crearLabel("Tipo:"), gbc);
        cbTipo = new JComboBox<>(new String[]{"CENTRAL", "REGIONAL", "TIENDA", "HUB"});
        cbTipo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridx = 1; pnlAgregar.add(cbTipo, gbc);

        fila++;
        btnAgregarAlmacen = crearBotonAccion("Agregar Almacén", ACCENT);
        gbc.gridx = 0; gbc.gridy = fila; gbc.gridwidth = 2;
        pnlAgregar.add(btnAgregarAlmacen, gbc);

        pnlIzquierdo.add(pnlAgregar);

        // ─── 2. CONECTAR RUTAS ───
        JPanel pnlConectar = crearSeccion("Conectar Rutas");
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 1;
        fila = 1;

        gbc.gridx = 0; gbc.gridy = fila; gbc.weightx = 0.0; pnlConectar.add(crearLabel("Origen ID:"), gbc);
        txtOrigen = crearTextField(14);
        gbc.gridx = 1; gbc.weightx = 1.0; pnlConectar.add(txtOrigen, gbc);
        JButton btnValOri = crearBotonPequeno("?");
        gbc.gridx = 2; gbc.weightx = 0.0; pnlConectar.add(btnValOri, gbc);

        fila++;
        gbc.gridx = 0; gbc.gridy = fila; gbc.weightx = 0.0; pnlConectar.add(crearLabel("Destino ID:"), gbc);
        txtDestino = crearTextField(14);
        gbc.gridx = 1; gbc.weightx = 1.0; pnlConectar.add(txtDestino, gbc);
        JButton btnValDes = crearBotonPequeno("?");
        gbc.gridx = 2; gbc.weightx = 0.0; pnlConectar.add(btnValDes, gbc);

        fila++;
        gbc.gridx = 0; gbc.gridy = fila; pnlConectar.add(crearLabel("Distancia (km):"), gbc);
        txtDistancia = crearTextField(10);
        gbc.gridx = 1; gbc.gridwidth = 2; pnlConectar.add(txtDistancia, gbc);

        fila++;
        gbc.gridx = 0; gbc.gridy = fila; gbc.gridwidth = 1;
        pnlConectar.add(crearLabel("Descripción:"), gbc);
        txtDescripcion = crearTextField(10);
        gbc.gridx = 1; gbc.gridwidth = 2; pnlConectar.add(txtDescripcion, gbc);

        fila++;
        btnConectar = crearBotonAccion("Crear Ruta", GREEN_BTN);
        gbc.gridx = 0; gbc.gridy = fila; gbc.gridwidth = 3;
        pnlConectar.add(btnConectar, gbc);

        pnlIzquierdo.add(pnlConectar);

        // ─── 3. DIJKSTRA ───
        JPanel pnlRuta = crearSeccion("Ruta Óptima (Dijkstra)");
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 1;
        fila = 1;

        gbc.gridx = 0; gbc.gridy = fila; pnlRuta.add(crearLabel("Origen:"), gbc);
        txtRutaOrigen = crearTextField(14);
        gbc.gridx = 1; pnlRuta.add(txtRutaOrigen, gbc);

        fila++;
        gbc.gridx = 0; gbc.gridy = fila; pnlRuta.add(crearLabel("Destino:"), gbc);
        txtRutaDestino = crearTextField(14);
        gbc.gridx = 1; pnlRuta.add(txtRutaDestino, gbc);

        fila++;
        btnCalcularRuta = crearBotonAccion("Calcular Ruta", ACCENT);
        gbc.gridx = 0; gbc.gridy = fila; gbc.gridwidth = 2;
        pnlRuta.add(btnCalcularRuta, gbc);

        pnlIzquierdo.add(pnlRuta);
        add(pnlIzquierdo, BorderLayout.WEST);

        // ─── PANEL DERECHO: RESULTADOS ───
        txtResultados = new JTextArea();
        txtResultados.setEditable(false);
        txtResultados.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtResultados.setBackground(BG_WHITE);
        txtResultados.setForeground(TEXT_PRI);
        txtResultados.setMargin(new Insets(12, 12, 12, 12));
        add(new JScrollPane(txtResultados), BorderLayout.CENTER);

        // ─── EVENTOS ───
        btnAgregarAlmacen.addActionListener(e -> {
            try {
                String id = txtIdAlmacen.getText();
                String nombre = txtNombre.getText();
                String dir = txtDireccion.getText();
                int cap = Integer.parseInt(txtCapacidad.getText());
                String tipo = (String) cbTipo.getSelectedItem();
                Almacen.TipoAlmacen tipoEnum = Almacen.TipoAlmacen.valueOf(tipo);

                Almacen a = new Almacen(id, nombre, dir, tipoEnum, cap);
                logisticaService.agregarAlmacen(a);
                JOptionPane.showMessageDialog(this, "Almacén agregado.");

                txtIdAlmacen.setText(logisticaService.generarCodigoAlmacen());
                txtNombre.setText(""); txtDireccion.setText(""); txtCapacidad.setText("");
                actualizarTextoRed();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Verifique los datos.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        FocusAdapter validadorFocus = new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                JTextField txt = (JTextField) e.getSource();
                String id = txt.getText().trim();
                if (!id.isEmpty() && !logisticaService.existeAlmacen(id)) {
                    txt.setForeground(new Color(220, 38, 38));
                    JOptionPane.showMessageDialog(FrmLogistica.this,
                            "El almacén " + id + " no existe en el grafo.",
                            "ID Inválido", JOptionPane.WARNING_MESSAGE);
                } else {
                    txt.setForeground(TEXT_PRI);
                }
            }
        };

        txtOrigen.addFocusListener(validadorFocus);
        txtDestino.addFocusListener(validadorFocus);

        btnValOri.addActionListener(e -> txtOrigen.transferFocus());
        btnValDes.addActionListener(e -> txtDestino.transferFocus());

        btnConectar.addActionListener(e -> {
            try {
                String o = txtOrigen.getText();
                String d = txtDestino.getText();
                double dist = Double.parseDouble(txtDistancia.getText());
                String desc = txtDescripcion.getText();

                logisticaService.conectarAlmacenes(o, d, dist, desc);
                JOptionPane.showMessageDialog(this, "Ruta conectada.");
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
            String o = txtRutaOrigen.getText();
            String d = txtRutaDestino.getText();
            try {
                ResultadoDijkstra res = logisticaService.getGrafo().dijkstra(o, d);
                StringBuilder sb = new StringBuilder();
                sb.append("═══ RUTA ÓPTIMA (Dijkstra) ═══\n");
                sb.append("Origen:  ").append(o).append("\n");
                sb.append("Destino: ").append(d).append("\n");
                if (res.esAlcanzable) {
                    sb.append(String.format("Distancia total: %.2f km\n", res.distanciaTotal));
                    sb.append("Recorrido: ");
                    for (int i = 0; i < res.camino.size(); i++) {
                        sb.append("[").append(res.camino.get(i)).append("]");
                        if (i < res.camino.size() - 1) sb.append(" → ");
                    }
                } else {
                    sb.append("⚠ NO HAY RUTA POSIBLE.");
                }
                txtResultados.setText(sb.toString() + "\n\n" + logisticaService.getGrafo().mostrarRed());
            } catch (AlmacenNoEncontradoException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    // ── Helpers ──
    private JPanel crearSeccion(String titulo) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3;
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

    private JButton crearBotonPequeno(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setOpaque(true);
        btn.setBackground(ACCENT);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setMargin(new Insets(2, 6, 2, 6));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void actualizarTextoRed() {
        txtResultados.setText(logisticaService.getGrafo().mostrarRed());
    }
}
