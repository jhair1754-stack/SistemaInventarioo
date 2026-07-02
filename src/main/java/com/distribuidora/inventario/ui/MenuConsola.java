package com.distribuidora.inventario.ui;

import com.distribuidora.inventario.exceptions.AlmacenNoEncontradoException;
import com.distribuidora.inventario.exceptions.CodigoProductoDuplicadoException;
import com.distribuidora.inventario.exceptions.ProductoNoEncontradoException;
import com.distribuidora.inventario.exceptions.StockInsuficienteException;
import com.distribuidora.inventario.models.Almacen;
import com.distribuidora.inventario.models.Almacen.TipoAlmacen;
import com.distribuidora.inventario.models.Producto;
import com.distribuidora.inventario.models.Transaccion;
import com.distribuidora.inventario.services.InventarioService;
import com.distribuidora.inventario.services.LogisticaService;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * ============================================================
 * CLASE: MenuConsola
 * ============================================================
 * Interfaz de usuario interactiva por consola para el Sistema
 * de Control de Inventario y Red Logística.
 *
 * <p>Captura todas las excepciones personalizadas en los puntos
 * de interacción y muestra mensajes descriptivos al usuario.</p>
 *
 * <h2>Navegación</h2>
 * <pre>
 *  [MENÚ PRINCIPAL]
 *    1. Gestión de Productos    (ArbolBinarioBusqueda)
 *    2. Gestión de Stock        (PilaAuditoria)
 *    3. Reportes y Consultas
 *    4. Red Logística           (GrafoLogistica)
 *    5. Salir
 * </pre>
 *
 * @author  Equipo Proyecto 4 – Ingeniería de Sistemas UNMSM
 * @version 2.0
 */
public class MenuConsola {

    // ─────────────────────────────────────────────────────────
    //  DEPENDENCIAS
    // ─────────────────────────────────────────────────────────

    private final InventarioService inv;
    private final LogisticaService  log;
    private final Scanner           sc;

    // Códigos de color ANSI
    private static final String RESET   = "\u001B[0m";
    private static final String BOLD    = "\u001B[1m";
    private static final String CYAN    = "\u001B[36m";
    private static final String GREEN   = "\u001B[32m";
    private static final String YELLOW  = "\u001B[33m";
    private static final String RED     = "\u001B[31m";
    private static final String BLUE    = "\u001B[34m";
    private static final String MAGENTA = "\u001B[35m";

    // ─────────────────────────────────────────────────────────
    //  CONSTRUCTOR
    // ─────────────────────────────────────────────────────────

    /**
     * Crea la interfaz con las dependencias inyectadas.
     *
     * @param inventarioService Servicio de inventario (BST + Pila).
     * @param logisticaService  Servicio de logística (Grafo).
     */
    public MenuConsola(InventarioService inventarioService,
                       LogisticaService  logisticaService) {
        this.inv = inventarioService;
        this.log = logisticaService;
        this.sc  = new Scanner(System.in);
    }

    // ─────────────────────────────────────────────────────────
    //  PUNTO DE ENTRADA
    // ─────────────────────────────────────────────────────────

    /**
     * Lanza el menú principal e inicia el ciclo de interacción.
     */
    public void iniciar() {
        mostrarBienvenida();
        boolean activo = true;

        while (activo) {
            mostrarMenuPrincipal();
            int op = leerInt("Seleccione una opcion: ", 1, 5);
            switch (op) {
                case 1 -> menuProductos();
                case 2 -> menuStock();
                case 3 -> menuReportes();
                case 4 -> menuLogistica();
                case 5 -> { activo = false; mostrarDespedida(); }
            }
        }
        sc.close();
    }

    // ====================================================================
    // MENÚ PRINCIPAL
    // ====================================================================

    private void mostrarMenuPrincipal() {
        System.out.println();
        line(CYAN, "╔", "══════════════════════════════════════════════╗");
        lineTxt(CYAN, "║", "     SISTEMA DE CONTROL DE INVENTARIO         ");
        lineTxt(CYAN, "║", "          Empresa Distribuidora S.A.C.        ");
        line(CYAN, "╠", "══════════════════════════════════════════════╣");
        System.out.printf(CYAN + "  ║" + RESET +
            "  Productos: %-5d  |  Movimientos: %-8d" + CYAN + "║%n" + RESET,
            inv.getNumeroProductos(), inv.getNumeroMovimientos());
        System.out.printf(CYAN + "  ║" + RESET +
            "  Almacen activo: %-29s" + CYAN + "║%n" + RESET,
            inv.getIdAlmacenActivo());
        line(CYAN, "╠", "══════════════════════════════════════════════╣");
        menuItem(GREEN, "1", "Gestion de Productos (ArbolBinarioBusqueda)");
        menuItem(GREEN, "2", "Gestion de Stock (PilaAuditoria)           ");
        menuItem(GREEN, "3", "Reportes y Consultas                       ");
        menuItem(GREEN, "4", "Red Logistica (GrafoLogistica + Dijkstra)  ");
        menuItem(RED,   "5", "Salir                                      ");
        line(CYAN, "╚", "══════════════════════════════════════════════╝");
    }

    // ====================================================================
    // SUBMENÚ 1: GESTIÓN DE PRODUCTOS
    // ====================================================================

    private void menuProductos() {
        boolean back = false;
        while (!back) {
            System.out.println();
            hdr(BLUE, "GESTIÓN DE PRODUCTOS — ArbolBinarioBusqueda");
            menuItem(GREEN, "1", "Registrar nuevo producto");
            menuItem(GREEN, "2", "Buscar producto por codigo");
            menuItem(GREEN, "3", "Eliminar producto");
            menuItem(GREEN, "4", "Listar todos (Recorrido INORDEN del BST)");
            menuItem(RED,   "5", "Volver");
            int op = leerInt("Opcion: ", 1, 5);
            switch (op) {
                case 1 -> flujoRegistrar();
                case 2 -> flujoBuscar();
                case 3 -> flujoEliminar();
                case 4 -> flujoListar();
                case 5 -> back = true;
            }
        }
    }

    private void flujoRegistrar() {
        System.out.println();
        hdr(BLUE, "Registrar Nuevo Producto");
        System.out.println("  Tipo:  1. General   2. Perecible");
        int tipo = leerInt("  Tipo: ", 1, 2);

        System.out.print("  Codigo (ej. ELEC-005): ");
        String codigo = sc.nextLine().trim().toUpperCase();
        System.out.print("  Nombre: ");
        String nombre = sc.nextLine().trim();
        System.out.print("  Categoria: ");
        String cat = sc.nextLine().trim();
        double precio = leerDouble("  Precio unitario (S/): ", 0.01, 999999.99);
        int stock     = leerInt("  Stock inicial: ", 0, 999999);
        int min       = leerInt("  Stock minimo (alerta): ", 0, 999999);

        try {
            Producto p;
            if (tipo == 1) {
                System.out.print("  Marca: ");
                String marca = sc.nextLine().trim();
                p = new Producto(codigo, nombre, cat, precio, stock, min, marca);
            } else {
                LocalDate venc = leerFecha("  Fecha vencimiento (YYYY-MM-DD): ");
                p = new Producto(codigo, nombre, cat, precio, stock, min, venc);
            }
            // [REQUISITO RUBRICA: ÁRBOL BINARIO DE BÚSQUEDA] - Inserción desde UI
            inv.registrarProducto(p);
            System.out.println(GREEN + "  [OK] Producto registrado en el ArbolBinarioBusqueda." + RESET);

        } catch (CodigoProductoDuplicadoException e) {
            // Captura de excepción personalizada: código duplicado en BST
            System.out.println(RED + "  [ERROR] " + e.getMessage() + RESET);
        } catch (Exception e) {
            System.out.println(RED + "  [ERROR] Entrada invalida: " + e.getMessage() + RESET);
        }
    }

    private void flujoBuscar() {
        System.out.println();
        hdr(BLUE, "Buscar Producto — BST O(log n)");
        System.out.print("  Codigo: ");
        String cod = sc.nextLine().trim().toUpperCase();
        try {
            // [REQUISITO RUBRICA: ÁRBOL BINARIO DE BÚSQUEDA] - Búsqueda desde UI
            Producto p = inv.buscarProducto(cod);
            System.out.println();
            System.out.println(p);
        } catch (ProductoNoEncontradoException e) {
            // Captura de excepción personalizada: no encontrado en BST
            System.out.println(RED + "  [ERROR] " + e.getMessage() + RESET);
        }
    }

    private void flujoEliminar() {
        System.out.println();
        hdr(BLUE, "Eliminar Producto del BST");
        System.out.print("  Codigo: ");
        String cod = sc.nextLine().trim().toUpperCase();
        System.out.print(YELLOW + "  Confirmar eliminacion de '" + cod + "'? (s/n): " + RESET);
        if (!sc.nextLine().trim().equalsIgnoreCase("s")) {
            System.out.println("  Cancelado.");
            return;
        }
        try {
            inv.eliminarProducto(cod);
            System.out.println(GREEN + "  [OK] Producto eliminado del ArbolBinarioBusqueda." + RESET);
        } catch (ProductoNoEncontradoException e) {
            System.out.println(RED + "  [ERROR] " + e.getMessage() + RESET);
        }
    }

    private void flujoListar() {
        System.out.println();
        hdr(BLUE, "INVENTARIO COMPLETO — Recorrido INORDEN del ArbolBinarioBusqueda");
        // [REQUISITO RUBRICA: ÁRBOL BINARIO DE BÚSQUEDA] - Inorden desde UI
        List<Producto> lista = inv.listarTodosLosProductos();
        if (lista.isEmpty()) { System.out.println("  El inventario esta vacio."); return; }

        System.out.printf("  %-12s %-22s %-14s %-10s %-8s %-6s %-10s%n",
            "Codigo", "Nombre", "Categoria", "Precio", "Stock", "Min.", "Tipo");
        System.out.println("  " + "─".repeat(88));
        for (Producto p : lista) {
            String alerta = p.estaEnStockCritico() ? RED + " ¡CRITICO!" + RESET : "";
            System.out.printf("  %-12s %-22s %-14s S/%-8.2f %-8d %-6d %-10s%s%n",
                p.getCodigo(), trunc(p.getNombre(),21), trunc(p.getCategoria(),13),
                p.getPrecioUnitario(), p.getCantidadStock(), p.getStockMinimo(),
                p.getTipo(), alerta);
        }
        System.out.println("  " + "─".repeat(88));
        System.out.printf("  Total: %d productos  |  Altura del BST: %d%n",
            lista.size(), inv.getAlturaArbol());
    }

    // ====================================================================
    // SUBMENÚ 2: GESTIÓN DE STOCK
    // ====================================================================

    private void menuStock() {
        boolean back = false;
        while (!back) {
            System.out.println();
            hdr(BLUE, "GESTIÓN DE STOCK — PilaAuditoria LIFO");
            menuItem(GREEN,  "1", "Registrar ENTRADA de stock (push ENTRADA)");
            menuItem(YELLOW, "2", "Registrar SALIDA  de stock (push SALIDA) ");
            menuItem(GREEN,  "3", "Cambiar almacen activo");
            menuItem(RED,    "4", "Volver");
            int op = leerInt("Opcion: ", 1, 4);
            switch (op) {
                case 1 -> flujoEntrada();
                case 2 -> flujoSalida();
                case 3 -> flujoCambiarAlmacen();
                case 4 -> back = true;
            }
        }
    }

    private void flujoEntrada() {
        System.out.println();
        hdr(GREEN, "Registrar Entrada de Stock");
        System.out.print("  Codigo del producto: ");
        String cod = sc.nextLine().trim().toUpperCase();
        int cant   = leerInt("  Cantidad a ingresar: ", 1, 999999);
        System.out.print("  Observacion (proveedor, lote, etc.): ");
        String obs = sc.nextLine().trim();
        try {
            // [REQUISITO RUBRICA: PILAS] - Entrada genera push(ENTRADA) en la pila
            inv.registrarEntrada(cod, cant, obs);
        } catch (ProductoNoEncontradoException e) {
            System.out.println(RED + "  [ERROR] " + e.getMessage() + RESET);
        }
    }

    private void flujoSalida() {
        System.out.println();
        hdr(YELLOW, "Registrar Salida de Stock");
        System.out.print("  Codigo del producto: ");
        String cod = sc.nextLine().trim().toUpperCase();

        try {
            Producto p = inv.buscarProducto(cod);
            System.out.printf("  Stock disponible: %d unidades%n", p.getCantidadStock());
        } catch (ProductoNoEncontradoException e) {
            System.out.println(RED + "  [ERROR] " + e.getMessage() + RESET);
            return;
        }

        int cant = leerInt("  Cantidad a retirar: ", 1, 999999);
        System.out.print("  Observacion (cliente, pedido, etc.): ");
        String obs = sc.nextLine().trim();
        try {
            // [REQUISITO RUBRICA: PILAS] - Salida genera push(SALIDA) en la pila
            inv.registrarSalida(cod, cant, obs);
        } catch (ProductoNoEncontradoException e) {
            System.out.println(RED + "  [ERROR] " + e.getMessage() + RESET);
        } catch (StockInsuficienteException e) {
            // Captura de excepción personalizada: stock insuficiente
            System.out.println(RED + "  [ERROR] " + e.getMessage() + RESET);
            System.out.printf(YELLOW + "  Sugerencia: registre una entrada de al menos %d unidades.%n" + RESET,
                e.getFaltante());
        }
    }

    private void flujoCambiarAlmacen() {
        System.out.println();
        log.listarAlmacenes();
        System.out.print("  ID del almacen a activar: ");
        String id = sc.nextLine().trim().toUpperCase();
        inv.setIdAlmacenActivo(id);
        System.out.printf(GREEN + "  [OK] Almacen activo: %s%n" + RESET, id);
    }

    // ====================================================================
    // SUBMENÚ 3: REPORTES
    // ====================================================================

    private void menuReportes() {
        boolean back = false;
        while (!back) {
            System.out.println();
            hdr(BLUE, "REPORTES Y CONSULTAS");
            menuItem(GREEN, "1", "Historial de movimientos (PilaAuditoria LIFO)");
            menuItem(GREEN, "2", "Desapilar ultimo movimiento (POP)");
            menuItem(GREEN, "3", "Productos con stock critico");
            menuItem(GREEN, "4", "Estadisticas del sistema");
            menuItem(RED,   "5", "Volver");
            int op = leerInt("Opcion: ", 1, 5);
            switch (op) {
                case 1 -> flujoHistorial();
                case 2 -> flujoDesapilar();
                case 3 -> flujoStockCritico();
                case 4 -> flujoEstadisticas();
                case 5 -> back = true;
            }
        }
    }

    private void flujoHistorial() {
        System.out.println();
        hdr(BLUE, "HISTORIAL DE MOVIMIENTOS — PilaAuditoria (LIFO: más reciente primero)");
        int limite = leerInt("  Ultimos cuantos movimientos? (0 = todos): ", 0, 9999);

        // [REQUISITO RUBRICA: PILAS] - Recorrido de la pila de auditoría en UI
        List<Transaccion> hist = inv.obtenerHistorial(limite);
        if (hist.isEmpty()) { System.out.println("  Sin movimientos."); return; }

        System.out.println("  " + "─".repeat(110));
        System.out.printf("  %-7s %-21s %-12s %-20s %-7s %-8s %-8s %-9s%n",
            "#Trx", "Fecha/Hora", "Tipo", "Producto", "Cant.", "Antes", "Despues", "Almacen");
        System.out.println("  " + "─".repeat(110));

        for (Transaccion t : hist) {
            String tag = switch (t.getTipo()) {
                case ENTRADA  -> GREEN  + "[ENTRADA ]" + RESET;
                case SALIDA   -> YELLOW + "[SALIDA  ]" + RESET;
                case REGISTRO -> BLUE   + "[REGISTRO]" + RESET;
                case AJUSTE   -> MAGENTA+ "[AJUSTE  ]" + RESET;
            };
            System.out.printf("  %-7d %-21s %s %-20s %-7d %-8d %-8d %-9s%n",
                t.getNumero(),
                t.getFechaHora().toString().substring(0,19).replace("T"," "),
                tag,
                trunc(t.getNombreProducto(), 19),
                t.getCantidad(), t.getStockAnterior(), t.getStockPosterior(),
                t.getIdAlmacen()
            );
            if (!"-".equals(t.getObservacion()))
                System.out.println("         Obs: " + t.getObservacion());
        }
        System.out.println("  " + "─".repeat(110));
        System.out.printf("  Mostrados: %d | Total en pila: %d%n",
            hist.size(), inv.getNumeroMovimientos());
    }

    private void flujoDesapilar() {
        System.out.println();
        hdr(BLUE, "Desapilar Último Movimiento — POP de PilaAuditoria");
        try {
            // [REQUISITO RUBRICA: PILAS] - Operación POP explícita desde UI
            Transaccion t = inv.desapilarUltimo();
            System.out.println(GREEN + "  Movimiento desapilado:" + RESET);
            System.out.println("  " + t);
        } catch (IllegalStateException e) {
            System.out.println(RED + "  [ERROR] La pila de auditoria esta vacia." + RESET);
        }
    }

    private void flujoStockCritico() {
        System.out.println();
        hdr(RED, "PRODUCTOS CON STOCK CRÍTICO");
        List<Producto> crit = inv.obtenerProductosCriticos();
        if (crit.isEmpty()) {
            System.out.println(GREEN + "  Todos los productos tienen stock suficiente." + RESET);
            return;
        }
        System.out.printf("  %-12s %-22s %-10s %-10s %-10s%n",
            "Codigo", "Nombre", "Stock Act.", "Stock Min.", "Faltante");
        System.out.println("  " + "─".repeat(67));
        for (Producto p : crit) {
            System.out.printf(RED + "  %-12s %-22s %-10d %-10d %-10d%n" + RESET,
                p.getCodigo(), trunc(p.getNombre(),21),
                p.getCantidadStock(), p.getStockMinimo(),
                p.getStockMinimo() - p.getCantidadStock());
        }
        System.out.printf("  Total criticos: %d%n", crit.size());
    }

    private void flujoEstadisticas() {
        System.out.println();
        hdr(CYAN, "ESTADÍSTICAS DEL SISTEMA");
        System.out.println("  ─────────────────────────────────────────");
        System.out.printf("  Productos en BST:         %d%n", inv.getNumeroProductos());
        System.out.printf("  Altura del BST:            %d%n", inv.getAlturaArbol());
        System.out.printf("  Movimientos en Pila:      %d%n", inv.getNumeroMovimientos());
        System.out.printf("  Productos stock critico:  %d%n", inv.obtenerProductosCriticos().size());
        System.out.printf("  Almacen activo:           %s%n", inv.getIdAlmacenActivo());
        System.out.printf("  Almacenes en Grafo:       %d%n", log.getNumeroAlmacenes());
        System.out.printf("  Rutas en Grafo:           %d%n", log.getNumeroRutas());
        System.out.println("  ─────────────────────────────────────────");
    }

    // ====================================================================
    // SUBMENÚ 4: RED LOGÍSTICA
    // ====================================================================

    private void menuLogistica() {
        boolean back = false;
        while (!back) {
            System.out.println();
            hdr(BLUE, "RED LOGÍSTICA — GrafoLogistica");
            menuItem(GREEN, "1", "Ver red (Lista de Adyacencia)");
            menuItem(GREEN, "2", "Agregar almacen (nuevo vertice)");
            menuItem(GREEN, "3", "Conectar almacenes (nueva arista)");
            menuItem(GREEN, "4", "Ruta optima entre almacenes (Dijkstra)");
            menuItem(GREEN, "5", "Listar almacenes");
            menuItem(RED,   "6", "Volver");
            int op = leerInt("Opcion: ", 1, 6);
            switch (op) {
                case 1 -> { System.out.println();
                    // [REQUISITO RUBRICA: GRAFOS] - Visualización del grafo desde UI
                    log.mostrarRedLogistica(); }
                case 2 -> flujoAgregarAlmacen();
                case 3 -> flujoConectar();
                case 4 -> flujoRutaOptima();
                case 5 -> { System.out.println(); log.listarAlmacenes(); }
                case 6 -> back = true;
            }
        }
    }

    private void flujoAgregarAlmacen() {
        System.out.println();
        hdr(BLUE, "Agregar Almacén al GrafoLogistica");
        System.out.print("  ID (ej. ALM-07): ");
        String id = sc.nextLine().trim().toUpperCase();
        System.out.print("  Nombre: ");
        String nom = sc.nextLine().trim();
        System.out.print("  Ubicacion: ");
        String ubi = sc.nextLine().trim();
        System.out.println("  Tipo:  1.CENTRAL  2.REGIONAL  3.TIENDA  4.HUB");
        int tn = leerInt("  Tipo: ", 1, 4);
        TipoAlmacen tipo = switch (tn) {
            case 1 -> TipoAlmacen.CENTRAL;
            case 2 -> TipoAlmacen.REGIONAL;
            case 3 -> TipoAlmacen.TIENDA;
            default -> TipoAlmacen.HUB;
        };
        int cap = leerInt("  Capacidad max (unidades): ", 1, 9999999);
        try {
            // [REQUISITO RUBRICA: GRAFOS] - Agregar vértice desde UI
            log.agregarAlmacen(new Almacen(id, nom, ubi, tipo, cap));
        } catch (Exception e) {
            System.out.println(RED + "  [ERROR] " + e.getMessage() + RESET);
        }
    }

    private void flujoConectar() {
        System.out.println();
        hdr(BLUE, "Conectar Almacenes (Agregar Arista al GrafoLogistica)");
        log.listarAlmacenes();
        System.out.print("  ID origen : "); String orig = sc.nextLine().trim().toUpperCase();
        System.out.print("  ID destino: "); String dest = sc.nextLine().trim().toUpperCase();
        double km = leerDouble("  Distancia (km): ", 0.1, 99999.0);
        System.out.print("  Via/carretera: "); String via = sc.nextLine().trim();
        try {
            // [REQUISITO RUBRICA: GRAFOS] - Agregar arista ponderada desde UI
            log.conectarAlmacenes(orig, dest, km, via);
        } catch (AlmacenNoEncontradoException e) {
            System.out.println(RED + "  [ERROR] " + e.getMessage() + RESET);
        }
    }

    private void flujoRutaOptima() {
        System.out.println();
        hdr(BLUE, "Ruta Óptima — Algoritmo de Dijkstra");
        System.out.print("  ID origen : "); String orig = sc.nextLine().trim().toUpperCase();
        System.out.print("  ID destino: "); String dest = sc.nextLine().trim().toUpperCase();
        try {
            // [REQUISITO RUBRICA: GRAFOS] - Dijkstra desde UI
            log.calcularRutaOptima(orig, dest);
        } catch (AlmacenNoEncontradoException e) {
            System.out.println(RED + "  [ERROR] " + e.getMessage() + RESET);
        }
    }

    // ─────────────────────────────────────────────────────────
    //  UTILIDADES DE ENTRADA SEGURA
    // ─────────────────────────────────────────────────────────

    private int leerInt(String msg, int min, int max) {
        while (true) {
            try {
                System.out.print("  " + msg);
                int v = Integer.parseInt(sc.nextLine().trim());
                if (v >= min && v <= max) return v;
                System.out.printf(YELLOW + "  Ingrese entre %d y %d.%n" + RESET, min, max);
            } catch (NumberFormatException e) {
                System.out.println(YELLOW + "  Solo numeros enteros." + RESET);
            }
        }
    }

    private double leerDouble(String msg, double min, double max) {
        while (true) {
            try {
                System.out.print("  " + msg);
                double v = Double.parseDouble(sc.nextLine().trim().replace(",","."));
                if (v >= min && v <= max) return v;
                System.out.printf(YELLOW + "  Ingrese entre %.2f y %.2f.%n" + RESET, min, max);
            } catch (NumberFormatException e) {
                System.out.println(YELLOW + "  Solo numeros decimales." + RESET);
            }
        }
    }

    private LocalDate leerFecha(String msg) {
        while (true) {
            try {
                System.out.print("  " + msg);
                return LocalDate.parse(sc.nextLine().trim());
            } catch (DateTimeParseException e) {
                System.out.println(YELLOW + "  Use formato YYYY-MM-DD (ej. 2027-06-30)." + RESET);
            }
        }
    }

    private String trunc(String s, int max) {
        if (s == null || s.length() <= max) return s == null ? "" : s;
        return s.substring(0, max - 3) + "...";
    }

    // ─────────────────────────────────────────────────────────
    //  UTILIDADES DE FORMATO DE CUADROS
    // ─────────────────────────────────────────────────────────

    private void line(String color, String left, String content) {
        System.out.println(color + BOLD + "  " + left + content + RESET);
    }

    private void lineTxt(String color, String bar, String text) {
        System.out.printf(color + BOLD + "  %s" + RESET + " %-44s" + color + BOLD + "%s%n" + RESET,
            bar, text, "║");
    }

    private void menuItem(String color, String num, String text) {
        System.out.printf(CYAN + "  ║" + RESET + color + "  %s." + RESET + " %-43s" + CYAN + "║%n" + RESET,
            num, text);
    }

    private void hdr(String color, String title) {
        System.out.println(color + BOLD + "  ── " + title + " ──" + RESET);
    }

    // ─────────────────────────────────────────────────────────
    //  BIENVENIDA Y DESPEDIDA
    // ─────────────────────────────────────────────────────────

    private void mostrarBienvenida() {
        System.out.println(CYAN + BOLD);
        System.out.println("  ╔══════════════════════════════════════════════════════╗");
        System.out.println("  ║  SISTEMA DE CONTROL DE INVENTARIO Y RED LOGISTICA   ║");
        System.out.println("  ║  Empresa Distribuidora S.A.C.        v2.0           ║");
        System.out.println("  ║                                                      ║");
        System.out.println("  ║  Estructuras implementadas desde cero:               ║");
        System.out.println("  ║   * ArbolBinarioBusqueda (BST)  → Productos         ║");
        System.out.println("  ║   * PilaAuditoria (Stack LIFO)  → Historial         ║");
        System.out.println("  ║   * GrafoLogistica + Dijkstra   → Red Logistica     ║");
        System.out.println("  ║                                                      ║");
        System.out.println("  ║  Ingenieria de Sistemas – UNMSM 2026-I              ║");
        System.out.println("  ║  Proyecto 4 – Equipo de 9 integrantes               ║");
        System.out.println("  ╚══════════════════════════════════════════════════════╝");
        System.out.println(RESET);
    }

    private void mostrarDespedida() {
        System.out.println(CYAN + BOLD);
        System.out.println("  ╔══════════════════════════════════╗");
        System.out.println("  ║  Gracias por usar el sistema.    ║");
        System.out.println("  ║  Hasta pronto.                   ║");
        System.out.println("  ╚══════════════════════════════════╝");
        System.out.println(RESET);
    }
}
