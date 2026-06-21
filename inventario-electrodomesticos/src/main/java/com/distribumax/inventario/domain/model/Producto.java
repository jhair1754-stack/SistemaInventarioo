package com.distribumax.inventario.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_sku", nullable = false, unique = true, length = 30)
    @NotBlank(message = "El código SKU es obligatorio")
    private String codigoSku;

    @Column(nullable = false, length = 200)
    @NotBlank(message = "El nombre del producto es obligatorio")
    private String nombre;

    @Column(length = 100)
    private String modelo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marca_id", nullable = false)
    private Marca marca;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subcategoria_id", nullable = false)
    private Subcategoria subcategoria;

    @Column(name = "precio_unitario", nullable = false, precision = 12, scale = 2)
    @DecimalMin(value = "0.0", message = "El precio no puede ser negativo")
    private BigDecimal precioUnitario;

    @Column(name = "peso_kg", precision = 8, scale = 2)
    private BigDecimal pesoKg;

    @Column(name = "ancho_cm", precision = 8, scale = 2)
    private BigDecimal anchoCm;

    @Column(name = "alto_cm", precision = 8, scale = 2)
    private BigDecimal altoCm;

    @Column(name = "profundidad_cm", precision = 8, scale = 2)
    private BigDecimal profundidadCm;

    @Column(name = "stock_minimo", nullable = false)
    @Min(value = 0, message = "El stock mínimo no puede ser negativo")
    private Integer stockMinimo = 5;

    @Column(name = "stock_actual", nullable = false)
    @Min(value = 0, message = "El stock actual no puede ser negativo")
    private Integer stockActual = 0;

    @Column(nullable = false)
    private Boolean activo = true;

    public Producto() {}

    // ─── Reglas de negocio embebidas ───

    public boolean tieneStockBajo() {
        return stockActual < stockMinimo;
    }

    public int calcularDeficit() {
        return Math.max(0, stockMinimo - stockActual);
    }

    public void incrementarStock(int cantidad) {
        if (cantidad <= 0) throw new IllegalArgumentException("La cantidad a ingresar debe ser positiva");
        this.stockActual += cantidad;
    }

    public void decrementarStock(int cantidad) {
        if (cantidad <= 0) throw new IllegalArgumentException("La cantidad a retirar debe ser positiva");
        if (this.stockActual < cantidad) {
            throw new IllegalStateException(
                "Stock insuficiente para " + nombre + ". Disponible: " + stockActual + ", Solicitado: " + cantidad
            );
        }
        this.stockActual -= cantidad;
    }

    // ─── Getters y Setters ───

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCodigoSku() { return codigoSku; }
    public void setCodigoSku(String codigoSku) { this.codigoSku = codigoSku; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public Marca getMarca() { return marca; }
    public void setMarca(Marca marca) { this.marca = marca; }
    public Subcategoria getSubcategoria() { return subcategoria; }
    public void setSubcategoria(Subcategoria subcategoria) { this.subcategoria = subcategoria; }
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
}
