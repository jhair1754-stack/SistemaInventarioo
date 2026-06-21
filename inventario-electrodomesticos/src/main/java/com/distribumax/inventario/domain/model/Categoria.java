package com.distribumax.inventario.domain.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categorias")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(length = 255)
    private String descripcion;

    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Subcategoria> subcategorias = new ArrayList<>();

    public Categoria() {}

    public Categoria(Long id, String nombre, String descripcion, List<Subcategoria> subcategorias) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.subcategorias = subcategorias;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public List<Subcategoria> getSubcategorias() { return subcategorias; }
    public void setSubcategorias(List<Subcategoria> subcategorias) { this.subcategorias = subcategorias; }
}
