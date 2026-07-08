package modelos;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Registro inmutable de un movimiento de inventario. Es el
 * elemento que se apila en la {@code PilaAuditoria}.
 *
 * Una vez construida, ningún campo puede cambiar, garantizando
 * la integridad del historial de auditoría.
 *
 * <h2>Tipos de movimiento</h2>
 * 
 *   {@code ENTRADA}  – Ingreso de mercancía.
 *   {@code SALIDA}   – Despacho de mercancía.
 *   {@code REGISTRO} – Alta de un producto nuevo.
 *   {@code AJUSTE}   – Corrección manual de stock.
 * 
@author Equipo 2
 * @version Beta
 */
public class Transaccion {

    // ENUM: TIPO DE MOVIMIENTO
    /**
     * Tipos de movimiento que puede registrar una transacción.
     */
    public enum TipoMovimiento {
        /** Ingreso de mercancía al inventario. */
        ENTRADA,
        /** Despacho o venta de mercancía. */
        SALIDA,
        /** Alta de un producto nuevo en el sistema. */
        REGISTRO,
        /** Ajuste o corrección manual del stock. */
        AJUSTE
    }

    // CAMPOS INMUTABLES (final)
    /** Número de secuencia único, asignado automáticamente. */
    private final long numero;

    /** Tipo del movimiento. */
    private final TipoMovimiento tipo;

    /** Código del producto afectado (snapshot). */
    private final String codigoProducto;

    /** Nombre del producto al momento de la transacción (snapshot). */
    private final String nombreProducto;

    /** Unidades involucradas en el movimiento. */
    private final int cantidad;

    /** Stock del producto antes del movimiento. */
    private final int stockAnterior;

    /** Stock del producto después del movimiento. */
    private final int stockPosterior;

    /** ID del almacén donde ocurrió el movimiento. */
    private final String idAlmacen;

    /** Observación libre: proveedor, cliente, motivo, etc. */
    private final String observacion;

    /** Fecha y hora exacta de la transacción. */
    private final LocalDateTime fechaHora;

    /** Contador global para asignar números de transacción únicos. */
    private static long contadorSecuencial = 1;

    // CONSTRUCTOR
    /**
     * Crea un registro de transacción. El número de secuencia
     * se asigna automáticamente y de forma incremental.
 * 
 * @param tipo           Tipo de movimiento.
     * @param codigoProducto Código del producto afectado.
     * @param nombreProducto Nombre del producto (snapshot).
     * @param cantidad       Unidades involucradas.
     * @param stockAnterior  Stock previo al movimiento.
     * @param stockPosterior Stock resultante tras el movimiento.
     * @param idAlmacen      ID del almacén donde ocurrió.
     * @param observacion    Nota libre (puede ser cadena vacía).
     */
    public Transaccion(TipoMovimiento tipo,
                       String codigoProducto,
                       String nombreProducto,
                       int cantidad,
                       int stockAnterior,
                       int stockPosterior,
                       String idAlmacen,
                       String observacion) {
        this.numero = contadorSecuencial++;
        this.tipo = tipo;
        this.codigoProducto = codigoProducto;
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.stockAnterior = stockAnterior;
        this.stockPosterior = stockPosterior;
        this.idAlmacen = idAlmacen;
        this.observacion = (observacion == null || observacion.isBlank())
                               ? "-" : observacion;
        this.fechaHora = LocalDateTime.now();
    }

    // GETTERS (sin setters: objeto inmutable)
    /** Obtiene el valor. @return Número de secuencia de la transacción. */
    public long getNumero() { return numero; }

    /** Obtiene el valor. @return Tipo del movimiento. */
    public TipoMovimiento getTipo() { return tipo; }

    /** Obtiene el valor. @return Código del producto afectado. */
    public String getCodigoProducto() { return codigoProducto; }

    /** Obtiene el valor. @return Nombre del producto (snapshot). */
    public String getNombreProducto() { return nombreProducto; }

    /** Obtiene el valor. @return Unidades involucradas. */
    public int getCantidad() { return cantidad; }

    /** Obtiene el valor. @return Stock antes del movimiento. */
    public int getStockAnterior() { return stockAnterior; }

    /** Obtiene el valor. @return Stock después del movimiento. */
    public int getStockPosterior() { return stockPosterior; }

    /** Obtiene el valor. @return ID del almacén. */
    public String getIdAlmacen() { return idAlmacen; }

    /** Obtiene el valor. @return Observación libre. */
    public String getObservacion() { return observacion; }

    /** Obtiene el valor. @return Fecha y hora de la transacción. */
    public LocalDateTime getFechaHora() { return fechaHora; }

    // REPRESENTACIÓN EN CADENA
    /**
     * Formato compacto para mostrar en el historial de la pila.
 * 
 * @return Cadena con los datos más relevantes de la transacción.
     */
    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String signo = (tipo == TipoMovimiento.SALIDA) ? "-" : "+";
        String tag = switch (tipo) {
            case ENTRADA  -> "[ENTRADA ] ";
            case SALIDA   -> "[SALIDA  ] ";
            case REGISTRO -> "[REGISTRO] ";
            case AJUSTE   -> "[AJUSTE  ] ";
        };
        return String.format(
            "  #%-5d | %s | %s | Cod: %-10s | %-18s | " +
            "Cant: %s%-5d | Stock: %d -> %d | Almacen: %s | Obs: %s",
            numero,
            fmt.format(fechaHora),
            tag,
            codigoProducto,
            nombreProducto.length() > 18
                ? nombreProducto.substring(0, 15) + "..."
                : nombreProducto,
            signo, cantidad,
            stockAnterior, stockPosterior,
            idAlmacen,
            observacion
        );
    }
}
