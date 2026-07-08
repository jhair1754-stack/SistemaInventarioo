package ui;

import excepciones.ProductoNoEncontradoException;
import excepciones.StockInsuficienteException;
import modelos.Producto;
import servicios.InventarioService;
import estructuras.ListaEnlazada;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

/** Interfaz grafica de usuario. */
public class FrmControlStock extends JDialog {

    //  Paleta profesional clara 
    private static final Color BG_FORM = new Color(249, 250, 251);
    private static final Color BG_WHITE = Color.WHITE;
    private static final Color ACCENT = new Color(37, 99, 235);
    private static final Color TEXT_PRI = new Color(30, 41, 59);
    private static final Color TEXT_SEC = new Color(100, 116, 139);
    private static final Color BORDER = new Color(226, 232, 240);
    private static final Color GREEN_BTN = new Color(22, 163, 74);
    private static final Color RED_BTN = new Color(220, 38, 38);

    /** Categorías reales (mismas que Main.java y FrmGestionProductos) */
    private static final String[] CATEGORIAS = {
        "Electronico", "Alimento", "Farmaceutico",
        "Hogar/Oficina", "Ropa", "Herramienta", "Limpieza", "Otros"
    };

    private InventarioService inventarioService;
    private JTable tblProductos;
    private DefaultTableModel modeloTabla;
    private JComboBox<String> cbFiltroCategoria;

    private JTextField txtCodigo, txtCantidad, txtObservacion;
    private JButton btnEntrada, btnSalida;

    /**


     * Constructor principal.


     */

    public FrmControlStock(Frame parent, InventarioService invServ) {
        super(parent, "Control de Stock", true);
        this.inventarioService = invServ;

        setSize(860, 540);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_FORM);

        initComponents();
        actualizarTabla();
    }

    private void initComponents() {
        // ═══ FILTRO SUPERIOR ═══
        JPanel pnlNorte = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        pnlNorte.setBackground(BG_WHITE);
        pnlNorte.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));

        JLabel lblFiltro = new JLabel("Filtrar por categoría:");
        lblFiltro.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblFiltro.setForeground(TEXT_SEC);
        pnlNorte.add(lblFiltro);

        String[] opcionesFiltro = new String[CATEGORIAS.length + 1];
        opcionesFiltro[0] = "Todas";
        System.arraycopy(CATEGORIAS, 0, opcionesFiltro, 1, CATEGORIAS.length);
        cbFiltroCategoria = new JComboBox<>(opcionesFiltro);
        cbFiltroCategoria.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        pnlNorte.add(cbFiltroCategoria);
        add(pnlNorte, BorderLayout.NORTH);

        cbFiltroCategoria.addActionListener(e -> actualizarTabla());

        // ═══ TABLA CENTRAL ═══
        modeloTabla = new DefaultTableModel(
                new String[]{"Código", "Nombre", "Categoría", "Stock Actual"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblProductos = new JTable(modeloTabla);
        tblProductos.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblProductos.setRowHeight(26);
        tblProductos.setGridColor(BORDER);
        tblProductos.setSelectionBackground(new Color(219, 234, 254));
        tblProductos.setSelectionForeground(TEXT_PRI);

        JTableHeader header = tblProductos.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(BG_FORM);
        header.setForeground(TEXT_PRI);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));

        add(new JScrollPane(tblProductos), BorderLayout.CENTER);

        tblProductos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblProductos.getSelectedRow() != -1) {
                int filaSel = tblProductos.getSelectedRow();
                txtCodigo.setText(modeloTabla.getValueAt(filaSel, 0).toString());
            }
        });

        // ═══ PANEL INFERIOR: MOVIMIENTO ═══
        JPanel pnlSur = new JPanel(new GridBagLayout());
        pnlSur.setBackground(BG_WHITE);
        pnlSur.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);

        JLabel lblTitMov = new JLabel("Registrar Movimiento — Almacén: " + inventarioService.getIdAlmacenActivo());
        lblTitMov.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitMov.setForeground(TEXT_PRI);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 6;
        pnlSur.add(lblTitMov, gbc);
        gbc.gridwidth = 1;

        gbc.gridy = 1;
        gbc.gridx = 0; pnlSur.add(crearLabel("Código:"), gbc);
        txtCodigo = crearTextField(10);
        gbc.gridx = 1; pnlSur.add(txtCodigo, gbc);

        gbc.gridx = 2; pnlSur.add(crearLabel("Cantidad:"), gbc);
        txtCantidad = crearTextField(5);
        gbc.gridx = 3; pnlSur.add(txtCantidad, gbc);

        gbc.gridx = 4; pnlSur.add(crearLabel("Observación:"), gbc);
        txtObservacion = crearTextField(15);
        gbc.gridx = 5; pnlSur.add(txtObservacion, gbc);

        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        pnlBotones.setBackground(BG_WHITE);
        btnEntrada = crearBotonAccion("+ Entrada", GREEN_BTN);
        btnSalida = crearBotonAccion("- Salida", RED_BTN);
        pnlBotones.add(btnEntrada);
        pnlBotones.add(btnSalida);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 6;
        gbc.insets = new Insets(10, 6, 4, 6);
        pnlSur.add(pnlBotones, gbc);

        add(pnlSur, BorderLayout.SOUTH);

        // ═══ EVENTOS ═══
        btnEntrada.addActionListener(e -> registrarMovimiento(true));
        btnSalida.addActionListener(e -> registrarMovimiento(false));
    }

    //  Helpers 
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
        btn.setBorder(BorderFactory.createEmptyBorder(7, 18, 7, 18));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    //  Lógica 
    private void actualizarTabla() {
        modeloTabla.setRowCount(0);
        String filtro = (String) cbFiltroCategoria.getSelectedItem();
        ListaEnlazada<Producto> productos = inventarioService.listarTodosLosProductos();

        for (Producto p : productos) {
            if ("Todas".equals(filtro) || p.getCategoria().equalsIgnoreCase(filtro)) {
                modeloTabla.addRow(new Object[]{
                    p.getCodigo(),
                    p.getNombre(),
                    p.getCategoria(),
                    p.getCantidadStock()
                });
            }
        }
    }

    private void registrarMovimiento(boolean esEntrada) {
        String codigo = txtCodigo.getText().trim();
        String obs = txtObservacion.getText().trim();
        int cantidad;

        if (codigo.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Debe seleccionar o ingresar el código del producto.",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            cantidad = Integer.parseInt(txtCantidad.getText());
            if (cantidad <= 0) {
                JOptionPane.showMessageDialog(this,
                        "La cantidad debe ser mayor a 0.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "La cantidad debe ser un número entero válido.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (inventarioService.getIdAlmacenActivo() == null) {
            JOptionPane.showMessageDialog(this,
                    "No hay ningún almacén activo seleccionado.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if (esEntrada) {
                inventarioService.registrarEntrada(codigo, cantidad, obs);
                JOptionPane.showMessageDialog(this,
                        "Entrada registrada correctamente.",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                inventarioService.registrarSalida(codigo, cantidad, obs);
                JOptionPane.showMessageDialog(this,
                        "Salida registrada correctamente.",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
            txtCantidad.setText("");
            txtObservacion.setText("");
            actualizarTabla();
        } catch (ProductoNoEncontradoException | StockInsuficienteException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Error de Validación", JOptionPane.ERROR_MESSAGE);
        }
    }
}
