package main;

import excepciones.StockInsuficienteException;
import modelos.Almacen;
import modelos.Almacen.TipoAlmacen;
import modelos.Producto;
import servicios.InventarioService;
import servicios.LogisticaService;
import ui.FrmPrincipal;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import java.awt.Font;
import java.time.LocalDate;

/**
 * PUNTO DE ENTRADA: Main
 * Inicializa los servicios, carga datos de demostración en RAM
 * y lanza la interfaz interactiva por consola.
 *
 * <h2>Para compilar y ejecutar con Maven</h2>
 * 
 * @author Equipo 2
 * @version Beta
 */
/** Clase principal del sistema. */
public class Main {

    /**
     * Método principal.
 * 
 * @param args Argumentos de línea de comandos (no usados).
     */
    public static void main(String[] args) {

        //  INICIALIZAR SERVICIOS 
        LogisticaService logisticaService = new LogisticaService();
        InventarioService inventarioService = new InventarioService(logisticaService, "ALM-01");

        //  CARGAR RED LOGÍSTICA EN EL GRAFO
        System.out.println("  [INIT] Cargando red logistica...");
        cargarRedLogistica(logisticaService);

        //  CARGAR PRODUCTOS EN EL BST
        System.out.println("  [INIT] Cargando productos en el ArbolBinarioBusqueda...");
        cargarProductos(inventarioService);

        //  SIMULAR MOVIMIENTOS EN LA PILA
        System.out.println("  [INIT] Simulando movimientos (push a PilaAuditoria)...");
        simularMovimientos(inventarioService);

        System.out.println("  [INIT] Sistema listo.\n");

        //  LANZAR INTERFAZ GRÁFICA (GUI) 
        SwingUtilities.invokeLater(() -> {
            try {
                // Usamos el L&F Cross-Platform (Metal) para que setBackground()
                // funcione correctamente en los botones de todos los sistemas operativos.
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());

                // Ajustes globales de tipografía para que Metal se vea profesional
                java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
                while (keys.hasMoreElements()) {
                    Object key = keys.nextElement();
                    Object value = UIManager.get(key);
                    if (value instanceof javax.swing.plaf.FontUIResource) {
                        UIManager.put(key, new javax.swing.plaf.FontUIResource(
                                new Font("Segoe UI", Font.PLAIN, 13)));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            new FrmPrincipal(inventarioService, logisticaService).setVisible(true);
        });
    }

    //  CARGA DE RED LOGÍSTICA

    /**
     * Carga 6 almacenes y 7 rutas en el GrafoLogistica de demostración.
     *
     * Topología:
 * 
 * @param ls Servicio de logística.
     */
    private static void cargarRedLogistica(LogisticaService ls) {
        // VÉRTICES
        ls.agregarAlmacen(new Almacen("ALM-01", "Central Lima",
            "Av. Argentina 1250, Cercado", TipoAlmacen.CENTRAL, 50000));
        ls.agregarAlmacen(new Almacen("ALM-02", "Hub Callao",
            "Av. Faucett 800, Callao",   TipoAlmacen.HUB,     30000));
        ls.agregarAlmacen(new Almacen("ALM-03", "Regional SJL",
            "Av. Wiese 1450, SJL",       TipoAlmacen.REGIONAL,20000));
        ls.agregarAlmacen(new Almacen("ALM-04", "Tienda Surco",
            "Av. Benavides 3200, Surco", TipoAlmacen.TIENDA,   5000));
        ls.agregarAlmacen(new Almacen("ALM-05", "Regional Ica",
            "Calle Los Alamos 210, Ica", TipoAlmacen.REGIONAL,15000));
        ls.agregarAlmacen(new Almacen("ALM-06", "Tienda Miraflores",
            "Av. Larco 850, Miraflores", TipoAlmacen.TIENDA,   3000));

        // ARISTAS
        ls.conectarAlmacenes("ALM-01", "ALM-02", 12.5, "Av. Colonial");
        ls.conectarAlmacenes("ALM-01", "ALM-03", 25.0, "Av. Universitaria");
        ls.conectarAlmacenes("ALM-02", "ALM-04", 18.0, "Panamericana Sur");
        ls.conectarAlmacenes("ALM-03", "ALM-04", 30.0, "Via Expresa");
        ls.conectarAlmacenes("ALM-04", "ALM-05", 55.0, "Panamericana Sur");
        ls.conectarAlmacenes("ALM-05", "ALM-06", 80.0, "Carretera Ica-Lima");
        ls.conectarAlmacenes("ALM-02", "ALM-06", 10.0, "Av. Arequipa");
    }

    //  CARGA DE PRODUCTOS

    /**
     * Inserta 10 productos de demostración en el ArbolBinarioBusqueda.
     * Incluye tanto productos Generales como Perecibles.
     * Tres de ellos tienen stock intencional mente bajo para
     * demostrar las alertas de stock crítico.
 * 
 * @param is Servicio de inventario.
     */
    private static void cargarProductos(InventarioService is) {
        //  PRODUCTOS GENERALES 
        // Constructor: (codigo, nombre, categoria, precio, stock, min, marca)
        is.registrarProducto(new Producto("ELEC-001",
            "Laptop Lenovo IdeaPad", "Electronico",
            2500.00, 50, 10, "Lenovo"));

        is.registrarProducto(new Producto("ELEC-002",
            "Monitor Samsung 24\"", "Electronico",
            850.00, 30, 8, "Samsung"));

        is.registrarProducto(new Producto("HOGA-001",
            "Silla Oficina Ergonomica", "Hogar/Oficina",
            380.00, 5, 10, "Actiu"));       // Stock crítico: 5 < 10

        is.registrarProducto(new Producto("ROPE-001",
            "Polo Deportivo Adidas", "Ropa",
            89.90, 200, 30, "Adidas"));

        is.registrarProducto(new Producto("HERRA-001",
            "Taladro Inalambrico Bosch", "Herramienta",
            459.00, 3, 5, "Bosch"));        // Stock crítico: 3 < 5

        //  PRODUCTOS PERECIBLES 
        // Constructor: (codigo, nombre, categoria, precio, stock, min, fechaVenc)
        is.registrarProducto(new Producto("ALIM-001",
            "Yogurt Gloria x6", "Alimento",
            18.50, 120, 30,
            LocalDate.now().plusDays(15)));

        is.registrarProducto(new Producto("ALIM-002",
            "Aceite Primor 1L", "Alimento",
            9.80, 300, 50,
            LocalDate.now().plusMonths(18)));

        is.registrarProducto(new Producto("FARM-001",
            "Paracetamol 500mg x100", "Farmaceutico",
            12.00, 500, 100,
            LocalDate.now().plusMonths(24)));

        is.registrarProducto(new Producto("FARM-002",
            "Vacuna Influenza (dosis)", "Farmaceutico",
            45.00, 8, 20,
            LocalDate.now().plusMonths(6)));  // Stock crítico: 8 < 20

        is.registrarProducto(new Producto("ALIM-003",
            "Leche Evaporada Gloria x24", "Alimento",
            98.40, 80, 20,
            LocalDate.now().plusMonths(12)));
    }

    //  SIMULACIÓN DE MOVIMIENTOS

    /**
     * Simula entradas y salidas de stock para pre-cargar la
     * PilaAuditoria con datos de demostración.
 * 
 * @param is Servicio de inventario.
     */
    private static void simularMovimientos(InventarioService is) {
        try {
            // Entradas de stock
            is.registrarEntrada("ELEC-001", 20, "Compra a proveedor TechPeru SAC");
            is.registrarEntrada("ALIM-001", 50, "Lote 2024-A de Gloria SA");
            is.registrarEntrada("FARM-001", 200, "Farmacia Nacional - pedido mensual");

            // Salidas de stock
            is.registrarSalida("ROPE-001", 15, "Pedido cliente #1045 – SJL");
            is.registrarSalida("ALIM-002", 30, "Despacho Plaza Vea");
            is.registrarSalida("ELEC-002",  5, "Venta corporativa empresa ABC");

            // Esta salida dejará HOGA-001 en stock crítico y emitirá alerta
            is.registrarSalida("HOGA-001", 3, "Pedido cliente #2001 – Tienda Surco");

        } catch (StockInsuficienteException e) {
            System.out.println("  [WARN] Movimiento de demo ignorado: " + e.getMessage());
        }
    }
}
