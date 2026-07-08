package servicios;

import excepciones.CodigoProductoDuplicadoException;
import excepciones.ProductoNoEncontradoException;
import excepciones.StockInsuficienteException;
import modelos.Almacen;
import modelos.Producto;
import modelos.Transaccion;
import modelos.Transaccion.TipoMovimiento;
import estructuras.ArbolBinarioBusqueda;
import estructuras.PilaAuditoria;

import estructuras.ListaEnlazada;

/**
 * Capa de logica de negocio que orquesta el inventario,
 * interactuando con los inventarios independientes de cada Almacen.
 * 
 * @author Equipo 2
 * @version Beta
 */
public class InventarioService {

    private final LogisticaService logisticaService;
    private String idAlmacenActivo;

    /**
     * Constructor principal del servicio de inventario.
     * 
     * @param logisticaService Servicio logistico asociado.
     * @param idAlmacenActivo  ID del almacen inicialmente activo.
     */
    public InventarioService(LogisticaService logisticaService, String idAlmacenActivo) {
        this.logisticaService = logisticaService;
        this.idAlmacenActivo = idAlmacenActivo;
    }

    /**
     * Obtiene el almacen actualmente activo.
     * 
     * @return El almacen activo.
     * @throws IllegalStateException si no hay almacen activo o no existe.
     */
    private Almacen getAlmacenActual() {
        if (idAlmacenActivo == null || idAlmacenActivo.isEmpty()) {
            throw new IllegalStateException("No hay un almacen activo seleccionado.");
        }
        Almacen almacen = logisticaService.getAlmacen(idAlmacenActivo);
        if (almacen == null) {
            throw new IllegalStateException("El almacen activo '" + idAlmacenActivo + "' no existe en la red logistica.");
        }
        return almacen;
    }

    /**
     * @return El arbol binario de busqueda de productos del almacen activo.
     */
    public ArbolBinarioBusqueda getArbolProductos() {
        return getAlmacenActual().getArbolProductos();
    }

    /**
     * @return La pila de auditoria del almacen activo.
     */
    public PilaAuditoria getPilaAuditoria() {
        return getAlmacenActual().getPilaAuditoria();
    }

    /**
     * Registra un nuevo producto en el inventario.
     * 
     * @param producto Producto a registrar.
     */
    public void registrarProducto(Producto producto) {
        try {
            getArbolProductos().insertar(producto);

            Transaccion trx = new Transaccion(
                TipoMovimiento.REGISTRO,
                producto.getCodigo(),
                producto.getNombre(),
                producto.getCantidadStock(),
                0, producto.getCantidadStock(),
                idAlmacenActivo,
                "Registro inicial del producto"
            );
            getPilaAuditoria().push(trx);
        } catch (CodigoProductoDuplicadoException e) {
            throw e;
        }
    }

    /**
     * Registra una entrada de stock para un producto existente.
     * 
     * @param codigoProducto Codigo del producto.
     * @param cantidad       Cantidad a incrementar.
     * @param observacion    Detalle de la transaccion.
     */
    public void registrarEntrada(String codigoProducto, int cantidad, String observacion) {
        try {
            Producto producto = getArbolProductos().buscar(codigoProducto);
            int stockAnterior = producto.getCantidadStock();
            producto.aumentarStock(cantidad);
            int stockPosterior = producto.getCantidadStock();

            getPilaAuditoria().push(new Transaccion(
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

    /**
     * Registra una salida de stock para un producto existente.
     * 
     * @param codigoProducto Codigo del producto.
     * @param cantidad       Cantidad a disminuir.
     * @param observacion    Detalle de la transaccion.
     * @throws StockInsuficienteException si no hay stock suficiente.
     */
    public void registrarSalida(String codigoProducto, int cantidad, String observacion)
            throws StockInsuficienteException {
        try {
            Producto producto = getArbolProductos().buscar(codigoProducto);
            int stockAnterior = producto.getCantidadStock();

            if (cantidad > stockAnterior) {
                throw new StockInsuficienteException(codigoProducto, stockAnterior, cantidad);
            }

            producto.disminuirStock(cantidad);
            int stockPosterior = producto.getCantidadStock();

            getPilaAuditoria().push(new Transaccion(
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

    /**
     * Busca un producto por su codigo.
     * 
     * @param codigoProducto Codigo del producto a buscar.
     * @return El producto encontrado.
     */
    public Producto buscarProducto(String codigoProducto) {
        return getArbolProductos().buscar(codigoProducto);
    }

    /**
     * Elimina un producto del inventario.
     * 
     * @param codigoProducto Codigo del producto a eliminar.
     */
    public void eliminarProducto(String codigoProducto) {
        Producto producto = getArbolProductos().buscar(codigoProducto);
        getArbolProductos().eliminar(codigoProducto);

        getPilaAuditoria().push(new Transaccion(
            TipoMovimiento.AJUSTE,
            codigoProducto, producto.getNombre(),
            producto.getCantidadStock(),
            producto.getCantidadStock(), 0,
            idAlmacenActivo, "Producto eliminado del sistema"
        ));
    }

    /**
     * Obtiene el historial de transacciones realizadas en el almacen activo.
     * 
     * @param limite Numero maximo de transacciones a retornar (0 para todas).
     * @return Lista con el historial de transacciones.
     */
    public ListaEnlazada<Transaccion> obtenerHistorial(int limite) {
        ListaEnlazada<Transaccion> hist = getPilaAuditoria().toList();
        if (limite > 0 && hist.size() > limite) {
            ListaEnlazada<Transaccion> reducida = new ListaEnlazada<>();
            for(int i = 0; i < limite; i++) {
                reducida.add(hist.get(i));
            }
            return reducida;
        }
        return hist;
    }

    /**
     * Extrae la ultima transaccion registrada en la pila de auditoria.
     * 
     * @return La transaccion extraida.
     */
    public Transaccion desapilarUltimo() {
        return getPilaAuditoria().pop();
    }

    /**
     * Obtiene una lista con todos los productos del inventario ordenados.
     * 
     * @return Lista de todos los productos.
     */
    public ListaEnlazada<Producto> listarTodosLosProductos() {
        try {
            return getArbolProductos().recorrerInorden();
        } catch (Exception e) {
            return new ListaEnlazada<>();
        }
    }

    /**
     * Obtiene la lista de productos que estan en stock critico.
     * 
     * @return Lista de productos en estado critico.
     */
    public ListaEnlazada<Producto> obtenerProductosCriticos() {
        return getArbolProductos().obtenerProductosCriticos();
    }

    /**
     * Obtiene la lista de productos cuya fecha de vencimiento ya paso.
     * 
     * @return Lista de productos vencidos.
     */
    public ListaEnlazada<Producto> obtenerProductosVencidos() {
        ListaEnlazada<Producto> todos = getArbolProductos().recorrerInorden();
        ListaEnlazada<Producto> vencidos = new ListaEnlazada<>();
        for (Producto p : todos) {
            if (p.estaVencido()) {
                vencidos.add(p);
            }
        }
        return vencidos;
    }

    /**
     * Genera un codigo correlativo para un nuevo producto basado en su categoria.
     * 
     * @param categoria Categoria del producto.
     * @return Codigo autogenerado (ej. GEN-001).
     */
    public String generarCodigoProducto(String categoria) {
        if (categoria == null || categoria.trim().isEmpty()) {
            categoria = "GEN";
        }
        String prefijo = categoria.toUpperCase().substring(0, Math.min(4, categoria.length()));
        int max = 0;
        
        try {
            ListaEnlazada<Producto> todos = getArbolProductos().recorrerInorden();
            for (Producto p : todos) {
                if (p.getCodigo().startsWith(prefijo + "-")) {
                    try {
                        int num = Integer.parseInt(p.getCodigo().substring(prefijo.length() + 1));
                        if (num > max) max = num;
                    } catch (NumberFormatException ignored) {}
                }
            }
        } catch (Exception ignored) {
            // Si falla al obtener productos (ej. no hay almacen activo)
        }
        
        return String.format("%s-%03d", prefijo, max + 1);
    }

    /**
     * Muestra por consola una alerta si el producto tiene stock menor o igual al minimo.
     * 
     * @param producto Producto a verificar.
     */
    private void verificarStockCritico(Producto producto) {
        if (producto.estaEnStockCritico()) {
            System.out.println();
            System.out.println("  =================================");
            System.out.println("    ALERTA: STOCK CRITICO");
            System.out.printf("     Producto: %-31s%n", producto.getCodigo());
            System.out.printf("     Stock Actual : %-25d%n", producto.getCantidadStock());
            System.out.printf("     Stock Minimo : %-25d%n", producto.getStockMinimo());
            System.out.printf("     Faltante     : %-25d%n",
                producto.getStockMinimo() - producto.getCantidadStock());
            System.out.println("  =================================");
        }
    }

    /** Obtiene el valor. @return El numero total de productos en el almacen activo. */
    public int getNumeroProductos() { return getArbolProductos().getTamanio(); }

    /** Obtiene el valor. @return El numero total de transacciones registradas. */
    public int getNumeroMovimientos() { return getPilaAuditoria().getTamanio(); }

    /** Obtiene el valor. @return La altura del arbol binario de busqueda. */
    public int getAlturaArbol() { return getArbolProductos().obtenerAltura(); }

    /** Obtiene el valor. @return true si no hay productos registrados, false en caso contrario. */
    public boolean estaVacio() { return getArbolProductos().estaVacio(); }

    /** Obtiene el valor. @return ID del almacen activo. */
    public String getIdAlmacenActivo() { return idAlmacenActivo; }

    /**
     * Establece el almacen activo.
     * 
     * @param id ID del almacen.
     */
    public void setIdAlmacenActivo(String id) { this.idAlmacenActivo = id; }
}
