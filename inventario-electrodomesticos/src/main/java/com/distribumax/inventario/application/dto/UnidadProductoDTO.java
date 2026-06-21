package com.distribumax.inventario.application.dto;

import java.time.LocalDate;

public class UnidadProductoDTO {
    private Long id;
    private String numeroSerie;
    private Long productoId;
    private String productoNombre;
    private String ubicacionDescripcion;
    private String estado;
    private LocalDate fechaIngreso;
    private LocalDate fechaGarantiaFin;

    public UnidadProductoDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNumeroSerie() { return numeroSerie; }
    public void setNumeroSerie(String numeroSerie) { this.numeroSerie = numeroSerie; }
    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }
    public String getProductoNombre() { return productoNombre; }
    public void setProductoNombre(String productoNombre) { this.productoNombre = productoNombre; }
    public String getUbicacionDescripcion() { return ubicacionDescripcion; }
    public void setUbicacionDescripcion(String ubicacionDescripcion) { this.ubicacionDescripcion = ubicacionDescripcion; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public LocalDate getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(LocalDate fechaIngreso) { this.fechaIngreso = fechaIngreso; }
    public LocalDate getFechaGarantiaFin() { return fechaGarantiaFin; }
    public void setFechaGarantiaFin(LocalDate fechaGarantiaFin) { this.fechaGarantiaFin = fechaGarantiaFin; }
}
