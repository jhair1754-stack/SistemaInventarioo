package com.distribumax.inventario.domain.model;

import com.distribumax.inventario.domain.model.enums.EstadoUnidad;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "unidades_producto")
public class UnidadProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_serie", nullable = false, unique = true, length = 50)
    private String numeroSerie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ubicacion_id")
    private Ubicacion ubicacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoUnidad estado = EstadoUnidad.DISPONIBLE;

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDate fechaIngreso = LocalDate.now();

    @Column(name = "fecha_garantia_fin")
    private LocalDate fechaGarantiaFin;

    public UnidadProducto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNumeroSerie() { return numeroSerie; }
    public void setNumeroSerie(String numeroSerie) { this.numeroSerie = numeroSerie; }
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }
    public Ubicacion getUbicacion() { return ubicacion; }
    public void setUbicacion(Ubicacion ubicacion) { this.ubicacion = ubicacion; }
    public EstadoUnidad getEstado() { return estado; }
    public void setEstado(EstadoUnidad estado) { this.estado = estado; }
    public LocalDate getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(LocalDate fechaIngreso) { this.fechaIngreso = fechaIngreso; }
    public LocalDate getFechaGarantiaFin() { return fechaGarantiaFin; }
    public void setFechaGarantiaFin(LocalDate fechaGarantiaFin) { this.fechaGarantiaFin = fechaGarantiaFin; }
}
