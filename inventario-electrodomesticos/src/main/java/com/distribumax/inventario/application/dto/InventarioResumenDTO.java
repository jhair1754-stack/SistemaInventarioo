package com.distribumax.inventario.application.dto;

import java.math.BigDecimal;

public class InventarioResumenDTO {
    private Long totalProductos;
    private Long totalUnidades;
    private BigDecimal valorTotalInventario;
    private Long movimientosHoy;
    private Long productosStockBajo;

    public InventarioResumenDTO() {}

    public Long getTotalProductos() { return totalProductos; }
    public void setTotalProductos(Long totalProductos) { this.totalProductos = totalProductos; }
    public Long getTotalUnidades() { return totalUnidades; }
    public void setTotalUnidades(Long totalUnidades) { this.totalUnidades = totalUnidades; }
    public BigDecimal getValorTotalInventario() { return valorTotalInventario; }
    public void setValorTotalInventario(BigDecimal valorTotalInventario) { this.valorTotalInventario = valorTotalInventario; }
    public Long getMovimientosHoy() { return movimientosHoy; }
    public void setMovimientosHoy(Long movimientosHoy) { this.movimientosHoy = movimientosHoy; }
    public Long getProductosStockBajo() { return productosStockBajo; }
    public void setProductosStockBajo(Long productosStockBajo) { this.productosStockBajo = productosStockBajo; }
}
