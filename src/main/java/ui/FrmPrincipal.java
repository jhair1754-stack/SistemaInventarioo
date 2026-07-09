package ui;

import servicios.InventarioService;
import servicios.LogisticaService;
import estructuras.GrafoLogistico.VerticeLogistico;

import javax.swing.*;
import java.awt.*;

/** Interfaz grafica de usuario. */
public class FrmPrincipal extends JFrame {

    private InventarioService inventarioService;
    private LogisticaService logisticaService;

    private JComboBox<String> cbAlmacenActivo;

    //  Paleta profesional clara 
    private static final Color BG_MAIN = new Color(245, 247, 250);   // Fondo general gris perla
    private static final Color BG_HEADER = new Color(255, 255, 255);   // Header blanco
    private static final Color ACCENT = new Color(37, 99, 235);     // Azul profesional
    private static final Color ACCENT_HOVER = new Color(29, 78, 216);
    private static final Color TEXT_PRIMARY = new Color(30, 41, 59);    // Texto oscuro
    private static final Color TEXT_SECONDARY = new Color(100, 116, 139);
    private static final Color BORDER = new Color(226, 232, 240);    // Bordes suaves

    /**


     * Constructor principal.


     */

    public FrmPrincipal(InventarioService invServ, LogisticaService logServ) {
        this.inventarioService = invServ;
        this.logisticaService = logServ;

        setTitle("Sistema de Control de Inventario");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_MAIN);

        initComponents();
    }

    private void initComponents() {
        // ══════════ HEADER ══════════
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(BG_HEADER);
        pnlHeader.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                BorderFactory.createEmptyBorder(12, 20, 12, 20)));

        // Logo + Titulo
        JPanel pnlLogo = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        pnlLogo.setBackground(BG_HEADER);
        try {
            java.net.URL imgURL = getClass().getResource("/images/logo.jpg");
            if (imgURL != null) {
                ImageIcon icon = new ImageIcon(imgURL);
                Image img = icon.getImage().getScaledInstance(44, 44, Image.SCALE_SMOOTH);
                pnlLogo.add(new JLabel(new ImageIcon(img)));
            }
        } catch (Exception e) {
            System.err.println("No se pudo cargar el logo");
        }

        JPanel pnlTitulos = new JPanel();
        pnlTitulos.setLayout(new BoxLayout(pnlTitulos, BoxLayout.Y_AXIS));
        pnlTitulos.setBackground(BG_HEADER);

        JLabel lblTitulo = new JLabel("Sistema de Control de Inventario");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTitulo.setForeground(TEXT_PRIMARY);
        pnlTitulos.add(lblTitulo);

        JLabel lblSubtitulo = new JLabel("Grupo 2");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitulo.setForeground(TEXT_SECONDARY);
        pnlTitulos.add(lblSubtitulo);

        pnlLogo.add(pnlTitulos);
        pnlHeader.add(pnlLogo, BorderLayout.WEST);

        // Selector de almacén
        JPanel pnlCombo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlCombo.setBackground(BG_HEADER);
        JLabel lblAlmacen = new JLabel("Almacén activo:");
        lblAlmacen.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblAlmacen.setForeground(TEXT_SECONDARY);
        pnlCombo.add(lblAlmacen);

        cbAlmacenActivo = new JComboBox<>();
        cbAlmacenActivo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        actualizarComboAlmacenes();

        cbAlmacenActivo.addActionListener(e -> {
            String seleccionado = (String) cbAlmacenActivo.getSelectedItem();
            if (seleccionado != null && !seleccionado.equals("Sin almacenes")) {
                String id = seleccionado.split(" — ")[0];
                inventarioService.setIdAlmacenActivo(id);
            }
        });

        pnlCombo.add(cbAlmacenActivo);
        pnlHeader.add(pnlCombo, BorderLayout.EAST);
        add(pnlHeader, BorderLayout.NORTH);

        // ══════════ CENTRO: BOTONES ══════════
        JPanel pnlCentro = new JPanel(new GridLayout(2, 2, 18, 18));
        pnlCentro.setBackground(BG_MAIN);
        pnlCentro.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        JButton btnProductos = crearBoton("Gestión de Productos", "Administrar catálogo y precios", "📦");
        JButton btnStock = crearBoton("Control de Stock", "Entradas y salidas de inventario", "📊");
        JButton btnLogistica = crearBoton("Red Logística", "Almacenes y rutas de distribución", "🗺️");
        JButton btnReportes = crearBoton("Reportes y Auditoría", "Productos vencidos e historial", "📋");

        btnProductos.addActionListener(e -> new FrmGestionProductos(this, inventarioService).setVisible(true));
        btnStock.addActionListener(e -> new FrmControlStock(this, inventarioService).setVisible(true));
        btnLogistica.addActionListener(e -> {
            new FrmLogistica(this, logisticaService).setVisible(true);
            actualizarComboAlmacenes();
        });
        btnReportes.addActionListener(e -> new FrmReportes(this, inventarioService, logisticaService).setVisible(true));

        pnlCentro.add(btnProductos);
        pnlCentro.add(btnStock);
        pnlCentro.add(btnLogistica);
        pnlCentro.add(btnReportes);

        add(pnlCentro, BorderLayout.CENTER);

        // ══════════ FOOTER ══════════
        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlFooter.setBackground(BG_MAIN);
        pnlFooter.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER));
        JLabel lblFooter = new JLabel(" ");
        lblFooter.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblFooter.setForeground(TEXT_SECONDARY);
        pnlFooter.add(lblFooter);
        add(pnlFooter, BorderLayout.SOUTH);
    }

    private JButton crearBoton(String titulo, String subtitulo, String icono) {
        JButton btn = new JButton("<html><center>"
                + "<span style='font-size:22px'>" + icono + "</span><br>"
                + "<b style='font-size:12px'>" + titulo + "</b><br>"
                + "<span style='font-size:10px; color:#64748B'>" + subtitulo + "</span>"
                + "</center></html>");
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setOpaque(true);
        btn.setBackground(Color.WHITE);
        btn.setForeground(TEXT_PRIMARY);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(20, 14, 20, 14)));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(239, 246, 255));
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ACCENT, 1),
                        BorderFactory.createEmptyBorder(20, 14, 20, 14)));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(Color.WHITE);
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER, 1),
                        BorderFactory.createEmptyBorder(20, 14, 20, 14)));
            }
        });
        return btn;
    }

    /**
     * Actualiza el combo de almacenes.
     */
    public void actualizarComboAlmacenes() {
        cbAlmacenActivo.removeAllItems();
        if (logisticaService.getGrafo().getVertices().isEmpty()) {
            cbAlmacenActivo.addItem("Sin almacenes");
            inventarioService.setIdAlmacenActivo(null);
        } else {
            for (VerticeLogistico v : logisticaService.getGrafo().getVertices()) {
                cbAlmacenActivo.addItem(v.almacen.getId() + " — " + v.almacen.getNombre());
            }
            String actualId = inventarioService.getIdAlmacenActivo();
            boolean encontrado = false;
            if (actualId != null) {
                for (int i = 0; i < cbAlmacenActivo.getItemCount(); i++) {
                    if (cbAlmacenActivo.getItemAt(i).startsWith(actualId)) {
                        cbAlmacenActivo.setSelectedIndex(i);
                        encontrado = true;
                        break;
                    }
                }
            }
            if (!encontrado) {
                cbAlmacenActivo.setSelectedIndex(0);
            }
        }
    }
}
