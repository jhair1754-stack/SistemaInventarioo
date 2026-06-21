package com.distribumax.inventario.application.dto;

import jakarta.validation.constraints.*;
import java.util.List;

public class MovimientoCreateDTO {
    @NotNull(message = "El producto es obligatorio")
    private Long productoId;
    @NotNull(message = "El almacén es obligatorio")
    private Long almacenId;
    private Long proveedorId;
    @NotBlank(message = "El tipo de movimiento es obligatorio")
    private String tipoMovimiento;
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;
    private String numeroLote;
    private String observaciones;
    private List<String> numerosSerie;

    public MovimientoCreateDTO() {}

    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }
    public Long getAlmacenId() { return almacenId; }
    public void setAlmacenId(Long almacenId) { this.almacenId = almacenId; }
    public Long getProveedorId() { return proveedorId; }
    public void setProveedorId(Long proveedorId) { this.proveedorId = proveedorId; }
    public String getTipoMovimiento() { return tipoMovimiento; }
    public void setTipoMovimiento(String tipoMovimiento) { this.tipoMovimiento = tipoMovimiento; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public String getNumeroLote() { return numeroLote; }
    public void setNumeroLote(String numeroLote) { this.numeroLote = numeroLote; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public List<String> getNumerosSerie() { return numerosSerie; }
    public void setNumerosSerie(List<String> numerosSerie) { this.numerosSerie = numerosSerie; }
}
