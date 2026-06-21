package com.distribumax.inventario.domain.model;

import jakarta.persistence.*;

@Entity
@Table(name = "ubicaciones")
public class Ubicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    private String pasillo;

    @Column(nullable = false, length = 10)
    private String estante;

    @Column(length = 10)
    private String nivel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "almacen_id", nullable = false)
    private Almacen almacen;

    public Ubicacion() {}

    public String getDescripcionCompleta() {
        String desc = "Pasillo " + pasillo + " - Estante " + estante;
        if (nivel != null && !nivel.isEmpty()) {
            desc += " - Nivel " + nivel;
        }
        if (almacen != null) {
            desc += " (" + almacen.getNombre() + ")";
        }
        return desc;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPasillo() { return pasillo; }
    public void setPasillo(String pasillo) { this.pasillo = pasillo; }
    public String getEstante() { return estante; }
    public void setEstante(String estante) { this.estante = estante; }
    public String getNivel() { return nivel; }
    public void setNivel(String nivel) { this.nivel = nivel; }
    public Almacen getAlmacen() { return almacen; }
    public void setAlmacen(Almacen almacen) { this.almacen = almacen; }
}
