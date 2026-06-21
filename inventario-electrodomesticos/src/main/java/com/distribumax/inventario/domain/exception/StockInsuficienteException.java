package com.distribumax.inventario.domain.exception;

/**
 * Excepción de dominio lanzada cuando se intenta realizar una salida
 * de inventario con una cantidad mayor al stock disponible.
 */
public class StockInsuficienteException extends RuntimeException {

    private final String productoNombre;
    private final int stockDisponible;
    private final int cantidadSolicitada;

    public StockInsuficienteException(String productoNombre, int stockDisponible, int cantidadSolicitada) {
        super(String.format(
            "Stock insuficiente para '%s'. Disponible: %d, Solicitado: %d",
            productoNombre, stockDisponible, cantidadSolicitada
        ));
        this.productoNombre = productoNombre;
        this.stockDisponible = stockDisponible;
        this.cantidadSolicitada = cantidadSolicitada;
    }

    public String getProductoNombre() { return productoNombre; }
    public int getStockDisponible() { return stockDisponible; }
    public int getCantidadSolicitada() { return cantidadSolicitada; }

}
