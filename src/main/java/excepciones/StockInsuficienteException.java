package excepciones;

/**
 * EXCEPCIÓN PERSONALIZADA: StockInsuficienteException
 * Se lanza cuando se intenta registrar una salida de stock mayor
 * a la cantidad disponible en el inventario para un producto.
 *
 * Extiende {@code Exception} (checked) para forzar al código
 * cliente a manejar explícitamente este caso de error.
 * 
 * @author Equipo 2
 * @version Beta
 */
public class StockInsuficienteException extends Exception {

    /** Código del producto que no tiene stock suficiente. */
    private final String codigoProducto;

    /** Stock disponible al momento de lanzar la excepción. */
    private final int stockDisponible;

    /** Cantidad solicitada que excede el stock. */
    private final int cantidadSolicitada;

    /**
     * Constructor principal.
     * 
     * @param codigoProducto    Código del producto afectado.
     * @param stockDisponible   Stock actual disponible.
     * @param cantidadSolicitada Cantidad que se intentó retirar.
     */
    public StockInsuficienteException(String codigoProducto,
                                      int stockDisponible,
                                      int cantidadSolicitada) {
        super(String.format(
            "Stock insuficiente para el producto '%s'. " +
            "Disponible: %d unidades. Solicitado: %d unidades. Faltante: %d unidades.",
            codigoProducto,
            stockDisponible,
            cantidadSolicitada,
            cantidadSolicitada - stockDisponible
        ));
        this.codigoProducto = codigoProducto;
        this.stockDisponible = stockDisponible;
        this.cantidadSolicitada = cantidadSolicitada;
    }

    /** Obtiene el valor. @return Código del producto con stock insuficiente. */
    public String getCodigoProducto() { return codigoProducto; }

    /** Obtiene el valor. @return Stock disponible al momento del error. */
    public int getStockDisponible() { return stockDisponible; }

    /** Obtiene el valor. @return Cantidad solicitada que causó el error. */
    public int getCantidadSolicitada() { return cantidadSolicitada; }

    /** Obtiene el valor. @return Número de unidades faltantes para satisfacer la solicitud. */
    public int getFaltante() { return cantidadSolicitada - stockDisponible; }
}
