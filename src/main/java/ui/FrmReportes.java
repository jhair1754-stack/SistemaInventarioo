package ui;

import modelos.Producto;
import modelos.Transaccion;
import servicios.InventarioService;
import servicios.LogisticaService;
import estructuras.ListaEnlazada;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.format.DateTimeFormatter;

/** Interfaz grafica de usuario. */
public class FrmReportes extends JDialog {

    //  Paleta profesional clara 
    private static final Color BG_FORM = new Color(249, 250, 251);
    private static final Color BG_WHITE = Color.WHITE;
    private static final Color ACCENT = new Color(37, 99, 235);
    private static final Color TEXT_PRI = new Color(30, 41, 59);
    private static final Color TEXT_SEC = new Color(100, 116, 139);
    private static final Color BORDER = new Color(226, 232, 240);

    private InventarioService inventarioService;
    private LogisticaService logisticaService;

    private JTable tblInventario, tblVencidos, tblAuditoria;
    private DefaultTableModel modInventario, modVencidos, modAuditoria;

    /**


     * Constructor principal.


     */

    public FrmReportes(Frame parent, InventarioService invServ, LogisticaService logServ) {
        super(parent, "Reportes y Auditoría", true);
        this.inventarioService = invServ;
        this.logisticaService = logServ;

        setSize(1060, 640);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_FORM);

        initComponents();
        cargarDatos();
    }

    private void initComponents() {
        JTabbedPane tbpReportes = new JTabbedPane();
        tbpReportes.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tbpReportes.setBackground(BG_WHITE);

        // ═══ PESTAÑA 1: INVENTARIO COMPLETO (POLIMORFISMO) ═══
        JPanel pnlInventario = new JPanel(new BorderLayout());
        pnlInventario.setBackground(BG_WHITE);
        modInventario = new DefaultTableModel(
                new String[]{"Código", "Nombre", "Categoría", "Tipo", "Precio (S/)",
                             "Stock", "Stock Mín.", "Marca", "Fecha Venc."}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblInventario = crearTablaEstilizada(modInventario);
        pnlInventario.add(new JScrollPane(tblInventario), BorderLayout.CENTER);

        JPanel pnlSurInv = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        pnlSurInv.setBackground(BG_WHITE);
        pnlSurInv.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER));
        JButton btnActInv = crearBotonAccion("Actualizar", ACCENT);
        btnActInv.addActionListener(e -> cargarInventario());
        pnlSurInv.add(btnActInv);
        pnlInventario.add(pnlSurInv, BorderLayout.SOUTH);

        tbpReportes.addTab("Inventario Completo", pnlInventario);

        // ═══ PESTAÑA 2: PRODUCTOS VENCIDOS ═══
        JPanel pnlVencidos = new JPanel(new BorderLayout());
        pnlVencidos.setBackground(BG_WHITE);
        modVencidos = new DefaultTableModel(
                new String[]{"Código", "Nombre", "Categoría", "Precio (S/)",
                             "Stock", "Stock Mín.", "Fecha Vencimiento"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblVencidos = crearTablaEstilizada(modVencidos);
        pnlVencidos.add(new JScrollPane(tblVencidos), BorderLayout.CENTER);

        JPanel pnlSurVenc = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        pnlSurVenc.setBackground(BG_WHITE);
        pnlSurVenc.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER));
        JButton btnActualizarVenc = crearBotonAccion("Actualizar", ACCENT);
        btnActualizarVenc.addActionListener(e -> cargarVencidos());
        pnlSurVenc.add(btnActualizarVenc);
        pnlVencidos.add(pnlSurVenc, BorderLayout.SOUTH);

        tbpReportes.addTab("Productos Perecibles Vencidos", pnlVencidos);

        // ═══ PESTAÑA 3: PILA DE AUDITORÍA ═══
        JPanel pnlAuditoria = new JPanel(new BorderLayout());
        pnlAuditoria.setBackground(BG_WHITE);
        modAuditoria = new DefaultTableModel(
                new String[]{"Tipo Mov.", "Almacén", "Código Prod.", "Nombre",
                             "Cantidad", "Stock Post.", "Observación", "Fecha/Hora"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblAuditoria = crearTablaEstilizada(modAuditoria);
        pnlAuditoria.add(new JScrollPane(tblAuditoria), BorderLayout.CENTER);

        JPanel pnlSurAud = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        pnlSurAud.setBackground(BG_WHITE);
        pnlSurAud.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER));
        JButton btnActualizarAud = crearBotonAccion("Actualizar", ACCENT);
        btnActualizarAud.addActionListener(e -> cargarAuditoria());
        pnlSurAud.add(btnActualizarAud);
        pnlAuditoria.add(pnlSurAud, BorderLayout.SOUTH);

        tbpReportes.addTab("Historial de Movimientos", pnlAuditoria);

        add(tbpReportes, BorderLayout.CENTER);
    }

    //  Helpers 
    private JTable crearTablaEstilizada(DefaultTableModel modelo) {
        JTable tabla = new JTable(modelo);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabla.setRowHeight(26);
        tabla.setGridColor(BORDER);
        tabla.setSelectionBackground(new Color(219, 234, 254));
        tabla.setSelectionForeground(TEXT_PRI);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JTableHeader header = tabla.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(BG_FORM);
        header.setForeground(TEXT_PRI);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
        return tabla;
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

    //  Datos 
    private void cargarDatos() {
        cargarInventario();
        cargarVencidos();
        cargarAuditoria();
    }

    /**
     * Pestaña "Inventario Completo": aplica POLIMORFISMO para diferenciar
     * productos GENERAL (muestra Marca, "-" en Fecha Venc.) de
     * productos PERECIBLE (muestra "N/A" en Marca, fecha real en Fecha Venc.).
     */
    private void cargarInventario() {
        modInventario.setRowCount(0);
        ListaEnlazada<Producto> todos = inventarioService.listarTodosLosProductos();
        for (Producto p : todos) {
            String marca = "PERECIBLE".equals(p.getTipo()) ? "N/A" : p.getMarca();
            String fechaVenc = "PERECIBLE".equals(p.getTipo()) && p.getFechaVencimiento() != null
                    ? p.getFechaVencimiento().toString()
                    : "-";
            modInventario.addRow(new Object[]{
                p.getCodigo(),
                p.getNombre(),
                p.getCategoria(),
                p.getTipo(),
                String.format("S/ %.2f", p.getPrecioUnitario()),
                p.getCantidadStock(),
                p.getStockMinimo(),
                marca,
                fechaVenc
            });
        }
    }

    private void cargarVencidos() {
        modVencidos.setRowCount(0);
        ListaEnlazada<Producto> vencidos = inventarioService.obtenerProductosVencidos();
        for (Producto p : vencidos) {
            modVencidos.addRow(new Object[]{
                p.getCodigo(),
                p.getNombre(),
                p.getCategoria(),
                String.format("S/ %.2f", p.getPrecioUnitario()),
                p.getCantidadStock(),
                p.getStockMinimo(),
                p.getFechaVencimiento()
            });
        }
    }

    private void cargarAuditoria() {
        modAuditoria.setRowCount(0);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        ListaEnlazada<Transaccion> historial = inventarioService.obtenerHistorial(0);
        for (Transaccion t : historial) {
            modAuditoria.addRow(new Object[]{
                t.getTipo().name(),
                t.getIdAlmacen(),
                t.getCodigoProducto(),
                t.getNombreProducto(),
                t.getCantidad(),
                t.getStockPosterior(),
                t.getObservacion(),
                t.getFechaHora().format(fmt)
            });
        }
    }
}
