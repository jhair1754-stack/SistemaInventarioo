package com.distribuidora.inventario.ui;

import com.distribuidora.inventario.exceptions.AlmacenNoEncontradoException;
import com.distribuidora.inventario.exceptions.CodigoProductoDuplicadoException;
import com.distribuidora.inventario.exceptions.ProductoNoEncontradoException;
import com.distribuidora.inventario.exceptions.StockInsuficienteException;
import com.distribuidora.inventario.models.Almacen;
import com.distribuidora.inventario.models.Almacen.TipoAlmacen;
import com.distribuidora.inventario.models.Producto;
import com.distribuidora.inventario.models.ProductoGeneral;
import com.distribuidora.inventario.models.ProductoPerecible;
import com.distribuidora.inventario.models.Transaccion;
import com.distribuidora.inventario.services.InventarioService;
import com.distribuidora.inventario.services.LogisticaService;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * ================================================================
 * CLASE: MenuConsola
 * ================================================================
 * Interfaz de usuario basada en consola para el Sistema de Control
 * de Inventario. Muestra menús interactivos, valida entradas del
 * usuario, captura excepciones personalizadas en los puntos de
 * interacción y delega la lógica a los servicios correspondientes.
 *
 * <h2>Estructura de navegación</h2>
 * <pre>
 *  [MENÚ PRINCIPAL]
 *    1. Gestión de Productos (submenú)
 *    2. Gestión de Stock (submenú)
 *    3. Reportes y Consultas (submenú)
 *    4. Red Logística (submenú)
 *    5. Salir
 * </pre>
 *
 * @author  Equipo Proyecto 4 - Ingeniería de Sistemas UNMSM
 * @version 1.0
 */
public class MenuConsola {

    // ----------------------------------------------------------------
    // DEPENDENCIAS
    // ----------------------------------------------------------------

    private final InventarioService inventarioService;
    private final LogisticaService  logisticaService;
    private final Scanner           scanner;

    // ----------------------------------------------------------------
    // CÓDIGOS DE COLOR ANSI PARA CONSOLA
    // ----------------------------------------------------------------
    private static final String RESET  = "\u001B[0m";
    private static final String BOLD   = "\u001B[1m";
    private static final String CYAN   = "\u001B[36m";
    private static final String GREEN  = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED    = "\u001B[31m";
    private static final String BLUE   = "\u001B[34m";
    private static final String MAGENTA= "\u001B[35m";

    // ----------------------------------------------------------------
    // CONSTRUCTOR
    // ----------------------------------------------------------------

    /**
     * Crea la interfaz de usuario con sus dependencias inyectadas.
     *
     * @param inventarioService Servicio de inventario (BST + Pila).
     * @param logisticaService  Servicio de logística (Grafo).
     */
    public MenuConsola(InventarioService inventarioService,
                       LogisticaService  logisticaService) {
        this.inventarioService = inventarioService;
        this.logisticaService  = logisticaService;
        this.scanner           = new Scanner(System.in);
    }

    // ================================================================
    // PUNTO DE ENTRADA DEL MENÚ
    // ================================================================

    /**
     * Lanza el menú principal e inicia el ciclo de interacción
     * con el usuario. Se ejecuta hasta que el usuario elija "Salir".
     */
    public void iniciar() {
        mostrarBienvenida();
        boolean ejecutando = true;

        while (ejecutando) {
            mostrarMenuPrincipal();
            int opcion = leerEnteroSeguro("Seleccione una opcion: ", 1, 5);

            switch (opcion) {
                case 1 -> menuGestionProductos();
                case 2 -> menuGestionStock();
                case 3 -> menuReportes();
                case 4 -> menuRedLogistica();
                case 5 -> {
                    ejecutando = false;
                    mostrarDespedida();
                }
            }
        }
        scanner.close();
    }

    // ================================================================
    // MENÚ PRINCIPAL
    // ================================================================

    private void mostrarMenuPrincipal() {
        System.out.println();
        System.out.println(CYAN + BOLD +
            "  ╔══════════════════════════════════════════════╗" + RESET);
        System.out.println(CYAN + BOLD +
            "  ║     SISTEMA DE CONTROL DE INVENTARIO         ║" + RESET);
        System.out.println(CYAN + BOLD +
            "  ║          Empresa Distribuidora               ║" + RESET);
        System.out.println(CYAN + BOLD +
            "  ╠══════════════════════════════════════════════╣" + RESET);
        System.out.printf(CYAN + "  ║" + RESET + "  Productos: %-5d  |  Movimientos: %-8d" + CYAN + "║%n" + RESET,
            inventarioService.getNumeroProductos(),
            inventarioService.getNumeroMovimientos());
        System.out.printf(CYAN + "  ║" + RESET + "  Almacen activo: %-29s" + CYAN + "║%n" + RESET,
            inventarioService.getIdAlmacenActivo());
        System.out.println(CYAN + BOLD +
            "  ╠══════════════════════════════════════════════╣" + RESET);
        System.out.println(CYAN + "  ║" + RESET + GREEN + "  1." + RESET + " Gestion de Productos              " + CYAN + "║" + RESET);
        System.out.println(CYAN + "  ║" + RESET + GREEN + "  2." + RESET + " Gestion de Stock (Entrada/Salida) " + CYAN + "║" + RESET);
        System.out.println(CYAN + "  ║" + RESET + GREEN + "  3." + RESET + " Reportes y Consultas              " + CYAN + "║" + RESET);
        System.out.println(CYAN + "  ║" + RESET + GREEN + "  4." + RESET + " Red Logistica (Almacenes/Rutas)   " + CYAN + "║" + RESET);
        System.out.println(CYAN + "  ║" + RESET + RED   + "  5." + RESET + " Salir                             " + CYAN + "║" + RESET);
        System.out.println(CYAN + BOLD +
            "  ╚══════════════════════════════════════════════╝" + RESET);
    }

    // ================================================================
    // SUBMENÚ 1: GESTIÓN DE PRODUCTOS
    // ================================================================

    private void menuGestionProductos() {
        boolean volviendo = false;
        while (!volviendo) {
            System.out.println();
            System.out.println(BLUE + BOLD + "  ── GESTIÓN DE PRODUCTOS ──" + RESET);
            System.out.println(GREEN + "  1." + RESET + " Registrar nuevo producto");
            System.out.println(GREEN + "  2." + RESET + " Buscar producto por codigo");
            System.out.println(GREEN + "  3." + RESET + " Eliminar producto");
            System.out.println(GREEN + "  4." + RESET + " Listar todos los productos (Inorden BST)");
            System.out.println(RED   + "  5." + RESET + " Volver al menu principal");

            int opcion = leerEnteroSeguro("Opcion: ", 1, 5);
            switch (opcion) {
                case 1 -> flujoRegistrarProducto();
                case 2 -> flujoBuscarProducto();
                case 3 -> flujoEliminarProducto();
                case 4 -> flujoListarProductos();
                case 5 -> volviendo = true;
            }
        }
    }

    /** Flujo: Registrar un producto nuevo en el BST. */
    private void flujoRegistrarProducto() {
        System.out.println();
        System.out.println(BOLD + "  -- Registrar Nuevo Producto --" + RESET);

        // Elegir tipo de producto (POLIMORFISMO - OOP)
        System.out.println("  Tipo de producto:");
        System.out.println(GREEN + "  1." + RESET + " General (no perecible)");
        System.out.println(GREEN + "  2." + RESET + " Perecible (con fecha de vencimiento)");
        int tipo = leerEnteroSeguro("  Tipo: ", 1, 2);

        // Datos comunes
        System.out.print("  Codigo (ej. PROD-001): ");
        String codigo = scanner.nextLine().trim().toUpperCase();

        System.out.print("  Nombre: ");
        String nombre = scanner.nextLine().trim();

        System.out.print("  Categoria: ");
        String categoria = scanner.nextLine().trim();

        double precio = leerDoubleSeguro("  Precio unitario (S/): ", 0.01, 999999.99);
        int stock     = leerEnteroSeguro("  Stock inicial: ", 0, 999999);
        int minimo    = leerEnteroSeguro("  Stock minimo (umbral alerta): ", 0, 999999);

        try {
            Producto producto;

            if (tipo == 1) {
                // Producto General
                System.out.print("  Marca: ");
                String marca = scanner.nextLine().trim();
                System.out.print("  Modelo: ");
                String modelo = scanner.nextLine().trim();
                System.out.print("  Pais de origen: ");
                String pais = scanner.nextLine().trim();
                double peso   = leerDoubleSeguro("  Peso por unidad (kg): ", 0.001, 99999.0);
                int garantia  = leerEnteroSeguro("  Garantia (meses): ", 0, 120);

                producto = new ProductoGeneral(
                    codigo, nombre, categoria, precio, stock, minimo,
                    marca, modelo, pais, peso, garantia
                );
            } else {
                // Producto Perecible
                LocalDate fechaVenc = leerFechaSegura("  Fecha de vencimiento (YYYY-MM-DD): ");
                double tempMax      = leerDoubleSeguro("  Temperatura max. almacenamiento (°C): ", -100.0, 200.0);
                System.out.print("  Requiere cadena de frio? (s/n): ");
                boolean cadenaFrio  = scanner.nextLine().trim().equalsIgnoreCase("s");

                producto = new ProductoPerecible(
                    codigo, nombre, categoria, precio, stock, minimo,
                    fechaVenc, tempMax, cadenaFrio
                );
            }

            // [REQUISITO RUBRICA: ÁRBOL BINARIO DE BÚSQUEDA] - Inserción desde la UI
            inventarioService.registrarProducto(producto);

        } catch (CodigoProductoDuplicadoException e) {
            // Manejo de excepción personalizada: código duplicado en BST
            System.out.println(RED + "  [ERROR] " + e.getMessage() + RESET);
        } catch (Exception e) {
            System.out.println(RED + "  [ERROR] Entrada invalida: " + e.getMessage() + RESET);
        }
    }

    /** Flujo: Buscar producto por código en el BST. */
    private void flujoBuscarProducto() {
        System.out.println();
        System.out.println(BOLD + "  -- Buscar Producto --" + RESET);
        System.out.print("  Ingrese el codigo del producto: ");
        String codigo = scanner.nextLine().trim().toUpperCase();

        try {
            // [REQUISITO RUBRICA: ÁRBOL BINARIO DE BÚSQUEDA] - Búsqueda desde la UI
            Producto producto = inventarioService.buscarProducto(codigo);
            System.out.println();
            System.out.println(producto.toString());
        } catch (ProductoNoEncontradoException e) {
            // Manejo de excepción personalizada: producto no hallado en BST
            System.out.println(RED + "  [ERROR] " + e.getMessage() + RESET);
        }
    }

    /** Flujo: Eliminar un producto del BST. */
    private void flujoEliminarProducto() {
        System.out.println();
        System.out.println(BOLD + "  -- Eliminar Producto --" + RESET);
        System.out.print("  Ingrese el codigo del producto a eliminar: ");
        String codigo = scanner.nextLine().trim().toUpperCase();

        System.out.print(YELLOW + "  ¿Confirma eliminacion de '" + codigo + "'? (s/n): " + RESET);
        String conf = scanner.nextLine().trim();
        if (!conf.equalsIgnoreCase("s")) {
            System.out.println("  Operacion cancelada.");
            return;
        }

        try {
            inventarioService.eliminarProducto(codigo);
        } catch (ProductoNoEncontradoException e) {
            System.out.println(RED + "  [ERROR] " + e.getMessage() + RESET);
        }
    }

    /** Flujo: Listar todos los productos en orden inorden del BST. */
    private void flujoListarProductos() {
        System.out.println();
        System.out.println(BOLD + BLUE + "  -- INVENTARIO COMPLETO (Recorrido INORDEN del BST) --" + RESET);

        // [REQUISITO RUBRICA: ÁRBOL BINARIO DE BÚSQUEDA] - Recorrido inorden en UI
        List<Producto> productos = inventarioService.listarTodosLosProductos();

        if (productos.isEmpty()) {
            System.out.println("  El inventario esta vacio.");
            return;
        }

        System.out.printf("  %-12s %-20s %-15s %-10s %-8s %-8s%n",
            "Código", "Nombre", "Categoría", "Precio", "Stock", "Mín.");
        System.out.println("  " + "─".repeat(78));

        for (Producto p : productos) {
            String alerta = p.estaEnStockCritico() ? RED + " ¡!" + RESET : "   ";
            System.out.printf("  %-12s %-20s %-15s S/%-8.2f %-8d %-8d%s%n",
                p.getCodigo(),
                truncar(p.getNombre(), 19),
                truncar(p.getCategoria(), 14),
                p.getPrecioUnitario(),
                p.getCantidadStock(),
                p.getStockMinimo(),
                alerta
            );
        }
        System.out.println("  " + "─".repeat(78));
        System.out.printf("  Total de productos: %d | Altura del BST: %d%n",
            productos.size(), inventarioService.getAlturaArbol());
    }

    // ================================================================
    // SUBMENÚ 2: GESTIÓN DE STOCK
    // ================================================================

    private void menuGestionStock() {
        boolean volviendo = false;
        while (!volviendo) {
            System.out.println();
            System.out.println(BLUE + BOLD + "  ── GESTIÓN DE STOCK ──" + RESET);
            System.out.println(GREEN  + "  1." + RESET + " Registrar ENTRADA de stock");
            System.out.println(YELLOW + "  2." + RESET + " Registrar SALIDA de stock");
            System.out.println(GREEN  + "  3." + RESET + " Cambiar almacen activo");
            System.out.println(RED    + "  4." + RESET + " Volver al menu principal");

            int opcion = leerEnteroSeguro("Opcion: ", 1, 4);
            switch (opcion) {
                case 1 -> flujoEntradaStock();
                case 2 -> flujoSalidaStock();
                case 3 -> flujoCambiarAlmacen();
                case 4 -> volviendo = true;
            }
        }
    }

    /** Flujo: Registrar entrada de stock. */
    private void flujoEntradaStock() {
        System.out.println();
        System.out.println(BOLD + GREEN + "  -- Registrar Entrada de Stock --" + RESET);
        System.out.print("  Codigo del producto: ");
        String codigo = scanner.nextLine().trim().toUpperCase();

        int cantidad = leerEnteroSeguro("  Cantidad a ingresar: ", 1, 999999);
        System.out.print("  Observacion (proveedor, lote, etc.): ");
        String obs = scanner.nextLine().trim();

        try {
            // [REQUISITO RUBRICA: PILAS] - Se generará un push a la pila de auditoría
            inventarioService.registrarEntrada(codigo, cantidad, obs);
        } catch (ProductoNoEncontradoException e) {
            System.out.println(RED + "  [ERROR] " + e.getMessage() + RESET);
        }
    }

    /** Flujo: Registrar salida de stock. */
    private void flujoSalidaStock() {
        System.out.println();
        System.out.println(BOLD + YELLOW + "  -- Registrar Salida de Stock --" + RESET);
        System.out.print("  Codigo del producto: ");
        String codigo = scanner.nextLine().trim().toUpperCase();

        // Mostrar stock actual antes de pedir cantidad
        try {
            Producto p = inventarioService.buscarProducto(codigo);
            System.out.printf("  Stock disponible: %d unidades%n", p.getCantidadStock());
        } catch (ProductoNoEncontradoException e) {
            System.out.println(RED + "  [ERROR] " + e.getMessage() + RESET);
            return;
        }

        int cantidad = leerEnteroSeguro("  Cantidad a retirar: ", 1, 999999);
        System.out.print("  Observacion (cliente, pedido, etc.): ");
        String obs = scanner.nextLine().trim();

        try {
            // [REQUISITO RUBRICA: PILAS] - Se generará un push a la pila de auditoría
            inventarioService.registrarSalida(codigo, cantidad, obs);
        } catch (ProductoNoEncontradoException e) {
            System.out.println(RED + "  [ERROR] " + e.getMessage() + RESET);
        } catch (StockInsuficienteException e) {
            // Manejo explícito de excepción de stock insuficiente
            System.out.println(RED + "  [ERROR] " + e.getMessage() + RESET);
            System.out.printf(YELLOW + "  Sugerencia: Registre una entrada de al menos %d unidades.%n" + RESET,
                e.getFaltante());
        }
    }

    /** Flujo: Cambiar el almacén activo. */
    private void flujoCambiarAlmacen() {
        System.out.println();
        logisticaService.listarAlmacenes();
        System.out.print("  Ingrese el ID del almacen a activar: ");
        String id = scanner.nextLine().trim().toUpperCase();

        if (logisticaService.existeAlmacen(id)) {
            inventarioService.setIdAlmacenActivo(id);
            Almacen alm = logisticaService.getAlmacen(id);
            System.out.printf(GREEN + "  [OK] Almacen activo cambiado a: [%s] %s%n" + RESET,
                id, alm.getNombre());
        } else {
            System.out.println(YELLOW +
                "  El ID ingresado no existe en la red, pero se usara como almacen activo de todos modos." + RESET);
            inventarioService.setIdAlmacenActivo(id);
        }
    }

    // ================================================================
    // SUBMENÚ 3: REPORTES Y CONSULTAS
    // ================================================================

    private void menuReportes() {
        boolean volviendo = false;
        while (!volviendo) {
            System.out.println();
            System.out.println(BLUE + BOLD + "  ── REPORTES Y CONSULTAS ──" + RESET);
            System.out.println(GREEN + "  1." + RESET + " Historial de movimientos (Pila LIFO)");
            System.out.println(GREEN + "  2." + RESET + " Desapilar ultimo movimiento");
            System.out.println(GREEN + "  3." + RESET + " Productos con stock critico");
            System.out.println(GREEN + "  4." + RESET + " Estadisticas del sistema");
            System.out.println(RED   + "  5." + RESET + " Volver al menu principal");

            int opcion = leerEnteroSeguro("Opcion: ", 1, 5);
            switch (opcion) {
                case 1 -> flujoHistorialMovimientos();
                case 2 -> flujoDesapilarMovimiento();
                case 3 -> flujoStockCritico();
                case 4 -> flujoEstadisticas();
                case 5 -> volviendo = true;
            }
        }
    }

    /** Flujo: Mostrar historial de movimientos de la pila. */
    private void flujoHistorialMovimientos() {
        System.out.println();
        System.out.println(BOLD + BLUE + "  -- HISTORIAL DE MOVIMIENTOS (Pila LIFO - más reciente primero) --" + RESET);

        int limite = leerEnteroSeguro("  Mostrar ultimos cuantos movimientos? (0 = todos): ", 0, 9999);

        // [REQUISITO RUBRICA: PILAS] - Recorrido de la pila de auditoría en UI
        List<Transaccion> historial = inventarioService.obtenerHistorialMovimientos(limite);

        if (historial.isEmpty()) {
            System.out.println("  No hay movimientos registrados aun.");
            return;
        }

        System.out.println("  " + "─".repeat(115));
        System.out.printf("  %-7s %-21s %-14s %-20s %-7s %-8s %-8s %-10s%n",
            "#Trans.", "Fecha/Hora", "Tipo", "Producto", "Cant.", "Ant.", "Post.", "Almacén");
        System.out.println("  " + "─".repeat(115));

        for (Transaccion t : historial) {
            String tipoStr = switch (t.getTipo()) {
                case ENTRADA  -> GREEN  + "[+] ENTRADA " + RESET;
                case SALIDA   -> YELLOW + "[-] SALIDA  " + RESET;
                case REGISTRO -> BLUE   + "[*] REGISTRO" + RESET;
                case AJUSTE   -> MAGENTA+ "[~] AJUSTE  " + RESET;
            };
            System.out.printf("  %-7d %-21s %s %-20s %-7d %-8d %-8d %-10s%n",
                t.getNumeroTransaccion(),
                t.getFechaHora().toString().substring(0, 19).replace("T", " "),
                tipoStr,
                truncar(t.getNombreProducto(), 19),
                t.getCantidad(),
                t.getStockAnterior(),
                t.getStockPosterior(),
                t.getIdAlmacen()
            );
            if (t.getObservacion() != null && !t.getObservacion().isEmpty()) {
                System.out.println("         Obs: " + t.getObservacion());
            }
        }
        System.out.println("  " + "─".repeat(115));
        System.out.printf("  Total mostrado: %d movimientos (Pila total: %d)%n",
            historial.size(), inventarioService.getNumeroMovimientos());
    }

    /** Flujo: Desapilar el último movimiento de la pila. */
    private void flujoDesapilarMovimiento() {
        System.out.println();
        System.out.println(BOLD + "  -- Desapilar Último Movimiento (POP) --" + RESET);
        try {
            // [REQUISITO RUBRICA: PILAS] - Operación POP explícita desde la UI
            Transaccion trx = inventarioService.desapilarUltimoMovimiento();
            System.out.println(GREEN + "  Movimiento desapilado:" + RESET);
            System.out.println("  " + trx.toString());
        } catch (IllegalStateException e) {
            System.out.println(RED + "  [ERROR] La pila de auditoria esta vacia." + RESET);
        }
    }

    /** Flujo: Mostrar productos con stock crítico. */
    private void flujoStockCritico() {
        System.out.println();
        System.out.println(BOLD + RED + "  -- PRODUCTOS CON STOCK CRÍTICO --" + RESET);

        List<Producto> criticos = inventarioService.obtenerProductosCriticos();
        if (criticos.isEmpty()) {
            System.out.println(GREEN + "  Todos los productos tienen stock suficiente. ¡Excelente!" + RESET);
            return;
        }

        System.out.printf("  %-12s %-20s %-10s %-10s %-10s%n",
            "Código", "Nombre", "Stock Act.", "Stock Mín.", "Faltante");
        System.out.println("  " + "─".repeat(65));
        for (Producto p : criticos) {
            System.out.printf(RED + "  %-12s %-20s %-10d %-10d %-10d%n" + RESET,
                p.getCodigo(),
                truncar(p.getNombre(), 19),
                p.getCantidadStock(),
                p.getStockMinimo(),
                p.getStockMinimo() - p.getCantidadStock()
            );
        }
        System.out.printf("  Total en estado crítico: %d productos%n", criticos.size());
    }

    /** Flujo: Estadísticas del sistema. */
    private void flujoEstadisticas() {
        System.out.println();
        System.out.println(BOLD + CYAN + "  ── ESTADÍSTICAS DEL SISTEMA ──" + RESET);
        System.out.println("  ─────────────────────────────────────────");
        System.out.printf("  Productos en inventario:   %d%n", inventarioService.getNumeroProductos());
        System.out.printf("  Movimientos en pila:       %d%n", inventarioService.getNumeroMovimientos());
        System.out.printf("  Altura del BST:            %d%n", inventarioService.getAlturaArbol());
        System.out.printf("  Productos en stock critico: %d%n", inventarioService.obtenerProductosCriticos().size());
        System.out.printf("  Almacen activo:            %s%n", inventarioService.getIdAlmacenActivo());
        System.out.printf("  Almacenes en red:          %d%n", logisticaService.getNumeroAlmacenes());
        System.out.printf("  Rutas en red:              %d%n", logisticaService.getNumeroRutas());
        System.out.println("  ─────────────────────────────────────────");
    }

    // ================================================================
    // SUBMENÚ 4: RED LOGÍSTICA
    // ================================================================

    private void menuRedLogistica() {
        boolean volviendo = false;
        while (!volviendo) {
            System.out.println();
            System.out.println(BLUE + BOLD + "  ── RED LOGÍSTICA (GRAFO) ──" + RESET);
            System.out.println(GREEN + "  1." + RESET + " Ver red logistica (Lista de Adyacencia)");
            System.out.println(GREEN + "  2." + RESET + " Agregar almacen a la red");
            System.out.println(GREEN + "  3." + RESET + " Conectar dos almacenes (agregar ruta)");
            System.out.println(GREEN + "  4." + RESET + " Calcular ruta optima (Dijkstra)");
            System.out.println(GREEN + "  5." + RESET + " Listar todos los almacenes");
            System.out.println(RED   + "  6." + RESET + " Volver al menu principal");

            int opcion = leerEnteroSeguro("Opcion: ", 1, 6);
            switch (opcion) {
                case 1 -> {
                    System.out.println();
                    // [REQUISITO RUBRICA: GRAFOS] - Visualización del grafo desde la UI
                    logisticaService.mostrarRedLogistica();
                }
                case 2 -> flujoAgregarAlmacen();
                case 3 -> flujoConectarAlmacenes();
                case 4 -> flujoRutaOptima();
                case 5 -> {
                    System.out.println();
                    logisticaService.listarAlmacenes();
                }
                case 6 -> volviendo = true;
            }
        }
    }

    /** Flujo: Agregar almacén al grafo. */
    private void flujoAgregarAlmacen() {
        System.out.println();
        System.out.println(BOLD + "  -- Agregar Almacén --" + RESET);

        System.out.print("  ID del almacen (ej. ALM-05): ");
        String id = scanner.nextLine().trim().toUpperCase();

        System.out.print("  Nombre del almacen: ");
        String nombre = scanner.nextLine().trim();

        System.out.print("  Ubicacion (distrito/ciudad): ");
        String ubicacion = scanner.nextLine().trim();

        System.out.println("  Tipo de almacen:");
        System.out.println("  1. CENTRAL  2. REGIONAL  3. TIENDA  4. HUB");
        int tipoNum  = leerEnteroSeguro("  Tipo: ", 1, 4);
        TipoAlmacen tipo = switch (tipoNum) {
            case 1 -> TipoAlmacen.CENTRAL;
            case 2 -> TipoAlmacen.REGIONAL;
            case 3 -> TipoAlmacen.TIENDA;
            default -> TipoAlmacen.HUB;
        };

        int capacidad = leerEnteroSeguro("  Capacidad maxima (unidades): ", 1, 9999999);

        try {
            // [REQUISITO RUBRICA: GRAFOS] - Agregar vértice al grafo desde la UI
            Almacen almacen = new Almacen(id, nombre, ubicacion, tipo, capacidad);
            logisticaService.agregarAlmacen(almacen);
        } catch (Exception e) {
            System.out.println(RED + "  [ERROR] " + e.getMessage() + RESET);
        }
    }

    /** Flujo: Conectar dos almacenes con una ruta. */
    private void flujoConectarAlmacenes() {
        System.out.println();
        System.out.println(BOLD + "  -- Conectar Almacenes (Agregar Arista al Grafo) --" + RESET);

        logisticaService.listarAlmacenes();

        System.out.print("  ID almacen ORIGEN: ");
        String idOrigen  = scanner.nextLine().trim().toUpperCase();
        System.out.print("  ID almacen DESTINO: ");
        String idDestino = scanner.nextLine().trim().toUpperCase();
        double distancia  = leerDoubleSeguro("  Distancia (km): ", 0.1, 99999.0);
        System.out.print("  Descripcion de la ruta (via/carretera): ");
        String desc = scanner.nextLine().trim();

        try {
            // [REQUISITO RUBRICA: GRAFOS] - Agregar arista ponderada al grafo desde la UI
            logisticaService.conectarAlmacenes(idOrigen, idDestino, distancia, desc);
        } catch (AlmacenNoEncontradoException e) {
            System.out.println(RED + "  [ERROR] " + e.getMessage() + RESET);
        }
    }

    /** Flujo: Calcular ruta óptima con Dijkstra. */
    private void flujoRutaOptima() {
        System.out.println();
        System.out.println(BOLD + "  -- Calcular Ruta Óptima (Dijkstra) --" + RESET);

        System.out.print("  ID almacen ORIGEN: ");
        String idOrigen  = scanner.nextLine().trim().toUpperCase();
        System.out.print("  ID almacen DESTINO: ");
        String idDestino = scanner.nextLine().trim().toUpperCase();

        try {
            // [REQUISITO RUBRICA: GRAFOS] - Algoritmo Dijkstra desde la UI
            logisticaService.calcularRutaOptima(idOrigen, idDestino);
        } catch (AlmacenNoEncontradoException e) {
            System.out.println(RED + "  [ERROR] " + e.getMessage() + RESET);
        }
    }

    // ================================================================
    // MÉTODOS AUXILIARES DE ENTRADA SEGURA
    // ================================================================

    /**
     * Lee un número entero del usuario dentro de un rango válido.
     * Repite la solicitud hasta obtener un valor correcto.
     *
     * @param mensaje Mensaje de solicitud al usuario.
     * @param min     Valor mínimo aceptado.
     * @param max     Valor máximo aceptado.
     * @return El entero válido ingresado por el usuario.
     */
    private int leerEnteroSeguro(String mensaje, int min, int max) {
        while (true) {
            try {
                System.out.print("  " + mensaje);
                String linea = scanner.nextLine().trim();
                int valor = Integer.parseInt(linea);
                if (valor >= min && valor <= max) {
                    return valor;
                }
                System.out.printf(YELLOW +
                    "  Valor fuera de rango. Ingrese entre %d y %d.%n" + RESET, min, max);
            } catch (NumberFormatException e) {
                System.out.println(YELLOW + "  Entrada invalida. Ingrese un numero entero." + RESET);
            }
        }
    }

    /**
     * Lee un número decimal del usuario dentro de un rango válido.
     *
     * @param mensaje Mensaje de solicitud.
     * @param min     Valor mínimo.
     * @param max     Valor máximo.
     * @return El double válido ingresado.
     */
    private double leerDoubleSeguro(String mensaje, double min, double max) {
        while (true) {
            try {
                System.out.print("  " + mensaje);
                double valor = Double.parseDouble(scanner.nextLine().trim().replace(",", "."));
                if (valor >= min && valor <= max) {
                    return valor;
                }
                System.out.printf(YELLOW +
                    "  Valor fuera de rango. Ingrese entre %.2f y %.2f.%n" + RESET, min, max);
            } catch (NumberFormatException e) {
                System.out.println(YELLOW + "  Entrada invalida. Ingrese un numero decimal." + RESET);
            }
        }
    }

    /**
     * Lee una fecha en formato YYYY-MM-DD del usuario.
     *
     * @param mensaje Mensaje de solicitud.
     * @return La fecha parseada.
     */
    private LocalDate leerFechaSegura(String mensaje) {
        while (true) {
            try {
                System.out.print("  " + mensaje);
                return LocalDate.parse(scanner.nextLine().trim());
            } catch (DateTimeParseException e) {
                System.out.println(YELLOW + "  Formato invalido. Use YYYY-MM-DD (ej. 2025-12-31)." + RESET);
            }
        }
    }

    /**
     * Trunca una cadena a una longitud máxima, añadiendo "..." si es necesario.
     *
     * @param texto     Cadena a truncar.
     * @param maxLength Longitud máxima.
     * @return Cadena truncada.
     */
    private String truncar(String texto, int maxLength) {
        if (texto == null) return "";
        if (texto.length() <= maxLength) return texto;
        return texto.substring(0, maxLength - 3) + "...";
    }

    // ================================================================
    // MENSAJES DE BIENVENIDA Y DESPEDIDA
    // ================================================================

    private void mostrarBienvenida() {
        System.out.println();
        System.out.println(CYAN + BOLD);
        System.out.println("  ╔══════════════════════════════════════════════════════╗");
        System.out.println("  ║                                                      ║");
        System.out.println("  ║   SISTEMA DE CONTROL DE INVENTARIO v1.0              ║");
        System.out.println("  ║   Empresa Distribuidora S.A.C.                       ║");
        System.out.println("  ║                                                      ║");
        System.out.println("  ║   Estructuras implementadas:                         ║");
        System.out.println("  ║   ✓ Árbol Binario de Búsqueda (BST) - Productos      ║");
        System.out.println("  ║   ✓ Pila (Stack) enlazada - Auditoría LIFO           ║");
        System.out.println("  ║   ✓ Grafo + Dijkstra - Red Logística                 ║");
        System.out.println("  ║                                                      ║");
        System.out.println("  ║   Ingeniería de Sistemas - UNMSM 2026-I              ║");
        System.out.println("  ║   Proyecto 4 - Equipo de 9 integrantes               ║");
        System.out.println("  ║                                                      ║");
        System.out.println("  ╚══════════════════════════════════════════════════════╝");
        System.out.println(RESET);
    }

    private void mostrarDespedida() {
        System.out.println();
        System.out.println(CYAN + BOLD);
        System.out.println("  ╔══════════════════════════════════════════╗");
        System.out.println("  ║   Gracias por usar el Sistema de         ║");
        System.out.println("  ║   Control de Inventario.                 ║");
        System.out.println("  ║   ¡Hasta pronto!                         ║");
        System.out.println("  ╚══════════════════════════════════════════╝");
        System.out.println(RESET);
    }
}
