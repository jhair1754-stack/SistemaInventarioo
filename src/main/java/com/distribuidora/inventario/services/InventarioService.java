package com.distribuidora.inventario.services;

import com.distribuidora.inventario.exceptions.CodigoProductoDuplicadoException;
import com.distribuidora.inventario.exceptions.ProductoNoEncontradoException;
import com.distribuidora.inventario.exceptions.StockInsuficienteException;
import com.distribuidora.inventario.models.Producto;
import com.distribuidora.inventario.models.Transaccion;
import com.distribuidora.inventario.models.Transaccion.TipoMovimiento;
import com.distribuidora.inventario.structures.ArbolBinarioBusqueda;
import com.distribuidora.inventario.structures.PilaAuditoria;

import java.util.List;

/**
 * ============================================================
 * SERVICIO: InventarioService
 * ============================================================
 * Capa de lógica de negocio que orquesta el
 * {@link ArbolBinarioBusqueda} y la {@link PilaAuditoria}.
 *
 * <p>Es el "cerebro" del inventario: recibe solicitudes de la UI,
 * las valida con excepciones personalizadas y delega a las
 * estructuras de datos subyacentes.</p>
 *
 * @author  Equipo Proyecto 4 – Ingeniería de Sistemas UNMSM
 * @version 2.0
 */
public class InventarioService {

    // ─────────────────────────────────────────────────────────
    //  ESTRUCTURAS DE DATOS INTERNAS
    // ─────────────────────────────────────────────────────────

    /**
     * ÁRBOL BINARIO DE BÚSQUEDA donde se almacenan todos los productos.
     * La búsqueda por código tiene complejidad promedio O(log n).
     *
     * [REQUISITO RUBRICA: ÁRBOL BINARIO DE BÚSQUEDA] - Instancia del árbol
     * usado para almacenar, buscar, insertar y eliminar productos.
     */
    private final ArbolBinarioBusqueda arbolProductos;

    /**
     * PILA DE AUDITORÍA donde se apilan todas las transacciones.
     * El movimiento más reciente siempre está en el tope (LIFO).
     *
     * [REQUISITO RUBRICA: PILAS] - Instancia de la PilaAuditoria
     * usada para el historial cronológico inverso de movimientos.
     */
    private final PilaAuditoria pilaAuditoria;

    /** ID del almacén actualmente activo (usado en las transacciones). */
    private String idAlmacenActivo;

    // ─────────────────────────────────────────────────────────
    //  CONSTRUCTOR
    // ─────────────────────────────────────────────────────────

    /**
     * Crea el servicio de inventario e inicializa las estructuras.
     *
     * @param idAlmacenActivo ID del almacén predeterminado.
     */
    public InventarioService(String idAlmacenActivo) {
        // [REQUISITO RUBRICA: ÁRBOL BINARIO DE BÚSQUEDA] - Inicialización del árbol
        this.arbolProductos  = new ArbolBinarioBusqueda();

        // [REQUISITO RUBRICA: PILAS] - Inicialización de la pila de auditoría
        this.pilaAuditoria   = new PilaAuditoria();

        this.idAlmacenActivo = idAlmacenActivo;
    }

    // ====================================================================
    // FUNCIONALIDAD 1: REGISTRAR PRODUCTO NUEVO
    // ====================================================================

    /**
     * Registra un producto nuevo en el árbol BST y apila la
     * transacción de registro en la pila de auditoría.
     *
     * @param producto El producto a registrar.
     * @throws CodigoProductoDuplicadoException si el código ya existe.
     */
    public void registrarProducto(Producto producto) {
        try {
            // [REQUISITO RUBRICA: ÁRBOL BINARIO DE BÚSQUEDA] - Inserción en el árbol
            arbolProductos.insertar(producto);

            // [REQUISITO RUBRICA: PILAS] - Push de transacción de REGISTRO
            Transaccion trx = new Transaccion(
                TipoMovimiento.REGISTRO,
                producto.getCodigo(),
                producto.getNombre(),
                producto.getCantidadStock(),
                0, producto.getCantidadStock(),
                idAlmacenActivo,
                "Registro inicial del producto"
            );
            pilaAuditoria.push(trx);

        } catch (CodigoProductoDuplicadoException e) {
            throw e;  // Re-lanzamos para que la UI la muestre
        }
    }

    // ====================================================================
    // FUNCIONALIDAD 2: REGISTRAR ENTRADA DE STOCK
    // ====================================================================

    /**
     * Registra una entrada de stock para un producto existente.
     * Busca el producto en el BST, actualiza su stock y apila
     * la transacción en la pila de auditoría.
     *
     * @param codigoProducto Código del producto.
     * @param cantidad       Unidades a ingresar.
     * @param observacion    Nota del movimiento (proveedor, lote, etc.).
     * @throws ProductoNoEncontradoException si el código no existe.
     */
    public void registrarEntrada(String codigoProducto,
                                  int cantidad,
                                  String observacion) {
        try {
            // [REQUISITO RUBRICA: ÁRBOL BINARIO DE BÚSQUEDA] - Búsqueda en el árbol
            Producto producto = arbolProductos.buscar(codigoProducto);

            int stockAnterior = producto.getCantidadStock();
            producto.aumentarStock(cantidad);
            int stockPosterior = producto.getCantidadStock();

            // [REQUISITO RUBRICA: PILAS] - Push de transacción de ENTRADA
            pilaAuditoria.push(new Transaccion(
                TipoMovimiento.ENTRADA,
                codigoProducto, producto.getNombre(),
                cantidad, stockAnterior, stockPosterior,
                idAlmacenActivo, observacion
            ));

            System.out.printf("  [OK] Entrada registrada: +%d ud. a '%s'. Stock: %d%n",
                cantidad, codigoProducto, stockPosterior);

            verificarStockCritico(producto);

        } catch (ProductoNoEncontradoException e) {
            throw e;
        }
    }

    // ====================================================================
    // FUNCIONALIDAD 3: REGISTRAR SALIDA DE STOCK
    // ====================================================================

    /**
     * Registra una salida (despacho) de stock para un producto existente.
     * Valida que haya stock suficiente ANTES de modificar el inventario.
     *
     * <p>Si tras la salida el stock cae por debajo del mínimo, se emite
     * automáticamente una alerta en consola.</p>
     *
     * @param codigoProducto Código del producto.
     * @param cantidad       Unidades a retirar.
     * @param observacion    Nota del movimiento (cliente, pedido, etc.).
     * @throws ProductoNoEncontradoException si el código no existe.
     * @throws StockInsuficienteException    si la cantidad supera el stock actual.
     */
    public void registrarSalida(String codigoProducto,
                                 int cantidad,
                                 String observacion)
            throws StockInsuficienteException {
        try {
            // [REQUISITO RUBRICA: ÁRBOL BINARIO DE BÚSQUEDA] - Búsqueda en el árbol
            Producto producto = arbolProductos.buscar(codigoProducto);

            int stockAnterior = producto.getCantidadStock();

            // Validar stock ANTES de modificar el inventario
            if (cantidad > stockAnterior) {
                throw new StockInsuficienteException(
                    codigoProducto, stockAnterior, cantidad);
            }

            producto.disminuirStock(cantidad);
            int stockPosterior = producto.getCantidadStock();

            // [REQUISITO RUBRICA: PILAS] - Push de transacción de SALIDA
            pilaAuditoria.push(new Transaccion(
                TipoMovimiento.SALIDA,
                codigoProducto, producto.getNombre(),
                cantidad, stockAnterior, stockPosterior,
                idAlmacenActivo, observacion
            ));

            System.out.printf("  [OK] Salida registrada: -%d ud. de '%s'. Stock: %d%n",
                cantidad, codigoProducto, stockPosterior);

            verificarStockCritico(producto);

        } catch (ProductoNoEncontradoException | StockInsuficienteException e) {
            throw e;
        }
    }

    // ====================================================================
    // FUNCIONALIDAD 4: BUSCAR PRODUCTO
    // ====================================================================

    /**
     * Busca y retorna un producto por código usando el BST.
     *
     * @param codigoProducto Código a buscar.
     * @return El producto encontrado.
     * @throws ProductoNoEncontradoException si no existe.
     */
    public Producto buscarProducto(String codigoProducto) {
        // [REQUISITO RUBRICA: ÁRBOL BINARIO DE BÚSQUEDA] - Búsqueda eficiente O(log n)
        return arbolProductos.buscar(codigoProducto);
    }

    // ====================================================================
    // FUNCIONALIDAD 5: ELIMINAR PRODUCTO
    // ====================================================================

    /**
     * Elimina un producto del árbol BST y apila el ajuste en la pila.
     *
     * @param codigoProducto Código a eliminar.
     * @throws ProductoNoEncontradoException si el código no existe.
     */
    public void eliminarProducto(String codigoProducto) {
        // [REQUISITO RUBRICA: ÁRBOL BINARIO DE BÚSQUEDA] - Eliminación del nodo en el árbol
        Producto producto = arbolProductos.buscar(codigoProducto);
        arbolProductos.eliminar(codigoProducto);

        // [REQUISITO RUBRICA: PILAS] - Push del ajuste de eliminación
        pilaAuditoria.push(new Transaccion(
            TipoMovimiento.AJUSTE,
            codigoProducto, producto.getNombre(),
            producto.getCantidadStock(),
            producto.getCantidadStock(), 0,
            idAlmacenActivo, "Producto eliminado del sistema"
        ));
    }

    // ====================================================================
    // FUNCIONALIDAD 6: HISTORIAL (RECORRIDO DE LA PILA)
    // ====================================================================

    /**
     * Retorna el historial completo de movimientos recorriendo
     * la pila en orden LIFO sin desapilar.
     *
     * @param limite Máximo de registros a retornar (0 = todos).
     * @return Lista de transacciones; el índice 0 es el más reciente.
     */
    public List<Transaccion> obtenerHistorial(int limite) {
        // [REQUISITO RUBRICA: PILAS] - Recorrido LIFO sin desapilar (toList)
        List<Transaccion> hist = pilaAuditoria.toList();
        if (limite > 0 && hist.size() > limite)
            return hist.subList(0, limite);
        return hist;
    }

    /**
     * Desapila y retorna la transacción más reciente.
     *
     * @return La transacción del tope.
     * @throws IllegalStateException si la pila está vacía.
     */
    public Transaccion desapilarUltimo() {
        // [REQUISITO RUBRICA: PILAS] - Operación POP explícita
        return pilaAuditoria.pop();
    }

    // ====================================================================
    // FUNCIONALIDAD 7: LISTAR TODOS (INORDEN DEL BST)
    // ====================================================================

    /**
     * Lista todos los productos ordenados alfabéticamente por código
     * usando el recorrido INORDEN del árbol BST.
     *
     * @return Lista de productos en orden alfabético.
     */
    public List<Producto> listarTodosLosProductos() {
        // [REQUISITO RUBRICA: ÁRBOL BINARIO DE BÚSQUEDA] - Recorrido INORDEN
        return arbolProductos.recorrerInorden();
    }

    // ====================================================================
    // FUNCIONALIDAD 8: STOCK CRÍTICO
    // ====================================================================

    /**
     * Retorna los productos cuyo stock está por debajo del mínimo.
     *
     * @return Lista de productos en estado crítico.
     */
    public List<Producto> obtenerProductosCriticos() {
        return arbolProductos.obtenerProductosCriticos();
    }

    // ─────────────────────────────────────────────────────────
    //  MÉTODO PRIVADO: ALERTA DE STOCK CRÍTICO
    // ─────────────────────────────────────────────────────────

    /**
     * Verifica si un producto está en estado crítico y, si lo está,
     * imprime una alerta vistosa en consola.
     *
     * @param producto Producto a verificar.
     */
    private void verificarStockCritico(Producto producto) {
        if (producto.estaEnStockCritico()) {
            System.out.println();
            System.out.println("  ╔══════════════════════════════════════════╗");
            System.out.println("  ║  *** ALERTA: STOCK CRITICO ***           ║");
            System.out.printf ("  ║  Producto: %-31s║%n", producto.getCodigo());
            System.out.printf ("  ║  Stock Actual : %-25d║%n", producto.getCantidadStock());
            System.out.printf ("  ║  Stock Minimo : %-25d║%n", producto.getStockMinimo());
            System.out.printf ("  ║  Faltante     : %-25d║%n",
                producto.getStockMinimo() - producto.getCantidadStock());
            System.out.println("  ╚══════════════════════════════════════════╝");
        }
    }

    // ─────────────────────────────────────────────────────────
    //  GETTERS DE ESTADO
    // ─────────────────────────────────────────────────────────

    /** @return Número de productos en el árbol BST. */
    public int  getNumeroProductos()   { return arbolProductos.getTamanio(); }

    /** @return Número de transacciones en la pila. */
    public int  getNumeroMovimientos() { return pilaAuditoria.getTamanio(); }

    /** @return Altura actual del árbol BST. */
    public int  getAlturaArbol()       { return arbolProductos.obtenerAltura(); }

    /** @return {@code true} si el inventario no tiene productos. */
    public boolean estaVacio()         { return arbolProductos.estaVacio(); }

    /** @return ID del almacén activo. */
    public String getIdAlmacenActivo() { return idAlmacenActivo; }

    /** @param id Nuevo almacén activo. */
    public void setIdAlmacenActivo(String id) { this.idAlmacenActivo = id; }

    /** @return Referencia al árbol BST (uso avanzado). */
    public ArbolBinarioBusqueda getArbolProductos() { return arbolProductos; }

    /** @return Referencia a la pila de auditoría. */
    public PilaAuditoria getPilaAuditoria()         { return pilaAuditoria; }
}
