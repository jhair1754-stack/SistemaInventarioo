package ui;

import excepciones.CodigoProductoDuplicadoException;
import excepciones.FechaVencimientoInvalidaException;
import excepciones.ProductoNoEncontradoException;
import modelos.Producto;
import servicios.InventarioService;
import estructuras.ListaEnlazada;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/** Interfaz grafica de usuario. */
public class FrmGestionProductos extends JDialog {

    //  Paleta profesional clara 
    private static final Color BG_FORM = new Color(249, 250, 251);
    private static final Color BG_WHITE = Color.WHITE;
    private static final Color ACCENT = new Color(37, 99, 235);
    private static final Color TEXT_PRI = new Color(30, 41, 59);
    private static final Color TEXT_SEC = new Color(100, 116, 139);
    private static final Color BORDER = new Color(226, 232, 240);
    private static final Color RED_BTN = new Color(220, 38, 38);
    private static final Color GREEN_BTN = new Color(22, 163, 74);

    /** Categorías reales que coinciden con los datos demo del Main */
    private static final String[] CATEGORIAS = {
        "Electronico", "Alimento", "Farmaceutico",
        "Hogar/Oficina", "Ropa", "Herramienta", "Limpieza", "Otros"
    };

    private InventarioService inventarioService;
    private JTable tblProductos;
    private DefaultTableModel modeloTabla;
    private JComboBox<String> cbFiltroCategoria;

    private JTextField txtCodigo, txtNombre, txtPrecio, txtStock, txtStockMin, txtMarca, txtFechaVenc;
    private JComboBox<String> cbCategoria, cbTipo;
    private JButton btnRegistrar, btnEliminar, btnLimpiar;

    /**


     * Constructor principal.


     */

    public FrmGestionProductos(Frame parent, InventarioService invServ) {
        super(parent, "Gestión de Productos", true);
        this.inventarioService = invServ;

        setSize(1100, 640);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_FORM);

        initComponents();
        actualizarTabla();
    }

    private void initComponents() {
        // ═══ PANEL IZQUIERDO: FORMULARIO ═══
        JPanel pnlFormulario = new JPanel(new GridBagLayout());
        pnlFormulario.setBackground(BG_WHITE);
        pnlFormulario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 6, 5, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Titulo del formulario
        JLabel lblTitForm = new JLabel("Datos del Producto");
        lblTitForm.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitForm.setForeground(TEXT_PRI);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        pnlFormulario.add(lblTitForm, gbc);
        gbc.gridwidth = 1;

        int fila = 1;

        // Tipo
        gbc.gridx = 0; gbc.gridy = fila; pnlFormulario.add(crearLabel("Tipo:"), gbc);
        cbTipo = new JComboBox<>(new String[]{"GENERAL", "PERECIBLE"});
        cbTipo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridx = 1; pnlFormulario.add(cbTipo, gbc);
        fila++;

        // Categoría
        gbc.gridx = 0; gbc.gridy = fila; pnlFormulario.add(crearLabel("Categoría:"), gbc);
        cbCategoria = new JComboBox<>(CATEGORIAS);
        cbCategoria.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridx = 1; pnlFormulario.add(cbCategoria, gbc);
        fila++;

        // Código (Auto-generado)
        gbc.gridx = 0; gbc.gridy = fila; pnlFormulario.add(crearLabel("Código:"), gbc);
        txtCodigo = new JTextField(15);
        txtCodigo.setEditable(true);
        txtCodigo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtCodigo.setToolTipText("El código se autogenera al elegir categoría");
        gbc.gridx = 1; pnlFormulario.add(txtCodigo, gbc);
        fila++;

        // Nombre
        gbc.gridx = 0; gbc.gridy = fila; pnlFormulario.add(crearLabel("Nombre:"), gbc);
        txtNombre = crearTextField(15);
        gbc.gridx = 1; pnlFormulario.add(txtNombre, gbc);
        fila++;

        // Precio
        gbc.gridx = 0; gbc.gridy = fila; pnlFormulario.add(crearLabel("Precio (S/):"), gbc);
        txtPrecio = crearTextField(15);
        gbc.gridx = 1; pnlFormulario.add(txtPrecio, gbc);
        fila++;

        // Stock Inicial
        gbc.gridx = 0; gbc.gridy = fila; pnlFormulario.add(crearLabel("Stock Inicial:"), gbc);
        txtStock = crearTextField(15);
        gbc.gridx = 1; pnlFormulario.add(txtStock, gbc);
        fila++;

        // Stock Mínimo
        gbc.gridx = 0; gbc.gridy = fila; pnlFormulario.add(crearLabel("Stock Mínimo:"), gbc);
        txtStockMin = crearTextField(15);
        gbc.gridx = 1; pnlFormulario.add(txtStockMin, gbc);
        fila++;

        // Marca
        gbc.gridx = 0; gbc.gridy = fila; pnlFormulario.add(crearLabel("Marca:"), gbc);
        txtMarca = crearTextField(15);
        gbc.gridx = 1; pnlFormulario.add(txtMarca, gbc);
        fila++;

        // Fecha Vencimiento
        gbc.gridx = 0; gbc.gridy = fila; pnlFormulario.add(crearLabel("Vencimiento:"), gbc);
        txtFechaVenc = crearTextField(15);
        txtFechaVenc.setToolTipText("Formato: YYYY-MM-DD");
        txtFechaVenc.setEnabled(false);
        gbc.gridx = 1; pnlFormulario.add(txtFechaVenc, gbc);
        fila++;

        // Botones
        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        pnlBotones.setBackground(BG_WHITE);

        btnRegistrar = crearBotonAccion("Registrar", GREEN_BTN);
        btnEliminar = crearBotonAccion("Eliminar", RED_BTN);
        btnLimpiar = crearBotonAccion("Limpiar", TEXT_SEC);

        pnlBotones.add(btnRegistrar);
        pnlBotones.add(btnEliminar);
        pnlBotones.add(btnLimpiar);

        gbc.gridx = 0; gbc.gridy = fila; gbc.gridwidth = 2;
        gbc.insets = new Insets(14, 6, 4, 6);
        pnlFormulario.add(pnlBotones, gbc);

        add(pnlFormulario, BorderLayout.WEST);

        // ═══ PANEL DERECHO: TABLA + FILTRO ═══
        JPanel pnlDerecha = new JPanel(new BorderLayout());
        pnlDerecha.setBackground(BG_FORM);

        // Filtro
        JPanel pnlFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        pnlFiltro.setBackground(BG_WHITE);
        pnlFiltro.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
        JLabel lblFiltro = new JLabel("Filtrar por categoría:");
        lblFiltro.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblFiltro.setForeground(TEXT_SEC);
        pnlFiltro.add(lblFiltro);

        String[] opcionesFiltro = new String[CATEGORIAS.length + 1];
        opcionesFiltro[0] = "Todas";
        System.arraycopy(CATEGORIAS, 0, opcionesFiltro, 1, CATEGORIAS.length);
        cbFiltroCategoria = new JComboBox<>(opcionesFiltro);
        cbFiltroCategoria.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        pnlFiltro.add(cbFiltroCategoria);
        pnlDerecha.add(pnlFiltro, BorderLayout.NORTH);

        // Tabla expandida: Código | Nombre | Categoría | Precio | Stock Actual | Stock Mínimo | Fecha Venc.
        modeloTabla = new DefaultTableModel(
                new String[]{"Código", "Nombre", "Categoría", "Precio (S/)", "Stock Actual", "Stock Mín.", "Fecha Venc."}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblProductos = new JTable(modeloTabla);
        tblProductos.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblProductos.setRowHeight(26);
        tblProductos.setGridColor(BORDER);
        tblProductos.setSelectionBackground(new Color(219, 234, 254));
        tblProductos.setSelectionForeground(TEXT_PRI);
        tblProductos.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JTableHeader header = tblProductos.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(BG_FORM);
        header.setForeground(TEXT_PRI);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));

        pnlDerecha.add(new JScrollPane(tblProductos), BorderLayout.CENTER);
        add(pnlDerecha, BorderLayout.CENTER);

        // ═══ EVENTOS ═══
        cbTipo.addActionListener(e -> {
            boolean esPerecible = "PERECIBLE".equals(cbTipo.getSelectedItem());
            txtFechaVenc.setEnabled(esPerecible);
            txtMarca.setEnabled(!esPerecible);
        });

        cbCategoria.addActionListener(e -> autogenerarCodigo());
        cbFiltroCategoria.addActionListener(e -> actualizarTabla());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnRegistrar.addActionListener(e -> registrarProducto());
        btnEliminar.addActionListener(e -> eliminarProducto());

        tblProductos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblProductos.getSelectedRow() != -1) {
                int filaSel = tblProductos.getSelectedRow();
                txtCodigo.setText(modeloTabla.getValueAt(filaSel, 0).toString());
                txtCodigo.setEditable(true);
            }
        });

        autogenerarCodigo();
    }

    //  Helpers de estilo 
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

    //  Lógica de negocio 
    private void autogenerarCodigo() {
        String cat = (String) cbCategoria.getSelectedItem();
        txtCodigo.setText(inventarioService.generarCodigoProducto(cat));
        txtCodigo.setEditable(true);
    }

    private void limpiarFormulario() {
        autogenerarCodigo();
        txtNombre.setText("");
        txtPrecio.setText("");
        txtStock.setText("");
        txtStockMin.setText("");
        txtMarca.setText("");
        txtFechaVenc.setText("");
        tblProductos.clearSelection();
    }

    /**
     * Actualiza la tabla mostrando todas las columnas solicitadas.
     * Aplica POLIMORFISMO: para productos GENERAL muestra "-" en Fecha Venc.,
     * para PERECIBLE muestra la fecha real.
     */
    private void actualizarTabla() {
        modeloTabla.setRowCount(0);
        String filtro = (String) cbFiltroCategoria.getSelectedItem();
        ListaEnlazada<Producto> productos = inventarioService.listarTodosLosProductos();

        for (Producto p : productos) {
            if ("Todas".equals(filtro) || p.getCategoria().equalsIgnoreCase(filtro)) {
                // Polimorfismo: mostrar fecha de vencimiento solo si es perecible
                String fechaVenc = "PERECIBLE".equals(p.getTipo()) && p.getFechaVencimiento() != null
                        ? p.getFechaVencimiento().toString()
                        : "-";
                modeloTabla.addRow(new Object[]{
                    p.getCodigo(),
                    p.getNombre(),
                    p.getCategoria(),
                    String.format("S/ %.2f", p.getPrecioUnitario()),
                    p.getCantidadStock(),
                    p.getStockMinimo(),
                    fechaVenc
                });
            }
        }
    }

    private void registrarProducto() {
        try {
            String codigo = txtCodigo.getText();
            String nombre = txtNombre.getText();
            String cat = (String) cbCategoria.getSelectedItem();
            double precio = Double.parseDouble(txtPrecio.getText());
            int stock = Integer.parseInt(txtStock.getText());
            int stockMin = Integer.parseInt(txtStockMin.getText());

            if (precio < 0 || stock < 0 || stockMin < 0) {
                JOptionPane.showMessageDialog(this,
                        "El precio, stock actual y stock mínimo no pueden ser negativos.",
                        "Error de Validación", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean perecible = "PERECIBLE".equals(cbTipo.getSelectedItem());

            Producto p;
            if (perecible) {
                LocalDate fechaVenc = LocalDate.parse(txtFechaVenc.getText());
                if (fechaVenc.isBefore(LocalDate.now())) {
                    throw new FechaVencimientoInvalidaException(
                            "La fecha de vencimiento no puede ser anterior a hoy.");
                }
                p = new Producto(codigo, nombre, cat, precio, stock, stockMin, fechaVenc);
            } else {
                String marca = txtMarca.getText();
                p = new Producto(codigo, nombre, cat, precio, stock, stockMin, marca);
            }

            inventarioService.registrarProducto(p);
            JOptionPane.showMessageDialog(this,
                    "Producto registrado con éxito.", "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            actualizarTabla();

        } catch (NumberFormatException | DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Verifique los datos numéricos y de fecha.",
                    "Error de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (FechaVencimientoInvalidaException | CodigoProductoDuplicadoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Error de Validación", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarProducto() {
        String codigo = txtCodigo.getText().trim();
        if (codigo.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione o ingrese un código para eliminar.",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!inventarioService.getArbolProductos().existe(codigo)) {
            JOptionPane.showMessageDialog(this,
                    "Producto no encontrado: " + codigo,
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar el producto " + codigo + "?",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                inventarioService.eliminarProducto(codigo);
                JOptionPane.showMessageDialog(this,
                        "Producto eliminado correctamente.", "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                limpiarFormulario();
                actualizarTabla();
            } catch (ProductoNoEncontradoException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
