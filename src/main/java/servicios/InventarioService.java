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
 * SERVICIO: InventarioService
 * Capa de lógica de negocio que orquesta el inventario, ahora
 * interactuando con los inventarios independientes de cada Almacén.
 *
 * @author Equipo 2
 * @version Beta
 */
public class InventarioService {

    private final LogisticaService logisticaService;
    private String idAlmacenActivo;

    public InventarioService(LogisticaService logisticaService, String idAlmacenActivo) {
        this.logisticaService = logisticaService;
        this.idAlmacenActivo = idAlmacenActivo;
    }

    private Almacen getAlmacenActual() {
        if (idAlmacenActivo == null || idAlmacenActivo.isEmpty()) {
            throw new IllegalStateException("No hay un almacén activo seleccionado.");
        }
        Almacen almacen = logisticaService.getAlmacen(idAlmacenActivo);
        if (almacen == null) {
            throw new IllegalStateException("El almacén activo '" + idAlmacenActivo + "' no existe en la red logística.");
        }
        return almacen;
    }

    public ArbolBinarioBusqueda getArbolProductos() {
        return getAlmacenActual().getArbolProductos();
    }

    public PilaAuditoria getPilaAuditoria() {
        return getAlmacenActual().getPilaAuditoria();
    }

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

    public Producto buscarProducto(String codigoProducto) {
        return getArbolProductos().buscar(codigoProducto);
    }

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

    public Transaccion desapilarUltimo() {
        return getPilaAuditoria().pop();
    }

    public ListaEnlazada<Producto> listarTodosLosProductos() {
        try {
            return getArbolProductos().recorrerInorden();
        } catch (Exception e) {
            return new ListaEnlazada<>();
        }
    }

    public ListaEnlazada<Producto> obtenerProductosCriticos() {
        return getArbolProductos().obtenerProductosCriticos();
    }

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
            // Si falla al obtener productos (ej. no hay almacén activo)
        }
        
        return String.format("%s-%03d", prefijo, max + 1);
    }

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

    public int getNumeroProductos() { return getArbolProductos().getTamanio(); }
    public int getNumeroMovimientos() { return getPilaAuditoria().getTamanio(); }
    public int getAlturaArbol() { return getArbolProductos().obtenerAltura(); }
    public boolean estaVacio() { return getArbolProductos().estaVacio(); }

    public String getIdAlmacenActivo() { return idAlmacenActivo; }
    public void setIdAlmacenActivo(String id) { this.idAlmacenActivo = id; }
}
