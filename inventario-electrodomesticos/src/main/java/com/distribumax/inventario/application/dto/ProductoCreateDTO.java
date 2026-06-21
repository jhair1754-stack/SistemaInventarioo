package com.distribumax.inventario.application.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class ProductoCreateDTO {
    @NotBlank(message = "El código SKU es obligatorio")
    private String codigoSku;
    @NotBlank(message = "El nombre del producto es obligatorio")
    private String nombre;
    private String modelo;
    @NotNull(message = "La marca es obligatoria")
    private Long marcaId;
    @NotNull(message = "La subcategoría es obligatoria")
    private Long subcategoriaId;
    @NotNull(message = "El precio unitario es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a cero")
    private BigDecimal precioUnitario;
    @DecimalMin(value = "0.0", message = "El peso no puede ser negativo")
    private BigDecimal pesoKg;
    @DecimalMin(value = "0.0", message = "El ancho no puede ser negativo")
    private BigDecimal anchoCm;
    @DecimalMin(value = "0.0", message = "El alto no puede ser negativo")
    private BigDecimal altoCm;
    @DecimalMin(value = "0.0", message = "La profundidad no puede ser negativa")
    private BigDecimal profundidadCm;
    @NotNull(message = "El stock mínimo es obligatorio")
    @Min(value = 0, message = "El stock mínimo no puede ser negativo")
    private Integer stockMinimo;

    public ProductoCreateDTO() {}

    public String getCodigoSku() { return codigoSku; }
    public void setCodigoSku(String codigoSku) { this.codigoSku = codigoSku; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public Long getMarcaId() { return marcaId; }
    public void setMarcaId(Long marcaId) { this.marcaId = marcaId; }
    public Long getSubcategoriaId() { return subcategoriaId; }
    public void setSubcategoriaId(Long subcategoriaId) { this.subcategoriaId = subcategoriaId; }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
    public BigDecimal getPesoKg() { return pesoKg; }
    public void setPesoKg(BigDecimal pesoKg) { this.pesoKg = pesoKg; }
    public BigDecimal getAnchoCm() { return anchoCm; }
    public void setAnchoCm(BigDecimal anchoCm) { this.anchoCm = anchoCm; }
    public BigDecimal getAltoCm() { return altoCm; }
    public void setAltoCm(BigDecimal altoCm) { this.altoCm = altoCm; }
    public BigDecimal getProfundidadCm() { return profundidadCm; }
    public void setProfundidadCm(BigDecimal profundidadCm) { this.profundidadCm = profundidadCm; }
    public Integer getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(Integer stockMinimo) { this.stockMinimo = stockMinimo; }
}
