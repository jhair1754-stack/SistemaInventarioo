package com.distribumax.inventario.application.dto;

import java.math.BigDecimal;

public class ProductoDTO {
    private Long id;
    private String codigoSku;
    private String nombre;
    private String modelo;
    private Long marcaId;
    private String marcaNombre;
    private Long subcategoriaId;
    private String subcategoriaNombre;
    private Long categoriaId;
    private String categoriaNombre;
    private BigDecimal precioUnitario;
    private BigDecimal pesoKg;
    private BigDecimal anchoCm;
    private BigDecimal altoCm;
    private BigDecimal profundidadCm;
    private Integer stockMinimo;
    private Integer stockActual;
    private Boolean activo;
    private Boolean stockBajo;
    private Integer deficit;

    public ProductoDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCodigoSku() { return codigoSku; }
    public void setCodigoSku(String codigoSku) { this.codigoSku = codigoSku; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public Long getMarcaId() { return marcaId; }
    public void setMarcaId(Long marcaId) { this.marcaId = marcaId; }
    public String getMarcaNombre() { return marcaNombre; }
    public void setMarcaNombre(String marcaNombre) { this.marcaNombre = marcaNombre; }
    public Long getSubcategoriaId() { return subcategoriaId; }
    public void setSubcategoriaId(Long subcategoriaId) { this.subcategoriaId = subcategoriaId; }
    public String getSubcategoriaNombre() { return subcategoriaNombre; }
    public void setSubcategoriaNombre(String subcategoriaNombre) { this.subcategoriaNombre = subcategoriaNombre; }
    public Long getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }
    public String getCategoriaNombre() { return categoriaNombre; }
    public void setCategoriaNombre(String categoriaNombre) { this.categoriaNombre = categoriaNombre; }
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
    public Integer getStockActual() { return stockActual; }
    public void setStockActual(Integer stockActual) { this.stockActual = stockActual; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    public Boolean getStockBajo() { return stockBajo; }
    public void setStockBajo(Boolean stockBajo) { this.stockBajo = stockBajo; }
    public Integer getDeficit() { return deficit; }
    public void setDeficit(Integer deficit) { this.deficit = deficit; }
}
