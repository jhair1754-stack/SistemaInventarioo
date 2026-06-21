package com.distribumax.inventario.presentation.controller;

import com.distribumax.inventario.domain.model.*;
import com.distribumax.inventario.domain.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/catalogo")
public class CatalogoController {

    private final CategoriaRepository categoriaRepository;
    private final SubcategoriaRepository subcategoriaRepository;
    private final MarcaRepository marcaRepository;
    private final ProveedorRepository proveedorRepository;
    private final AlmacenRepository almacenRepository;

    public CatalogoController(CategoriaRepository categoriaRepository, SubcategoriaRepository subcategoriaRepository,
                              MarcaRepository marcaRepository, ProveedorRepository proveedorRepository,
                              AlmacenRepository almacenRepository) {
        this.categoriaRepository = categoriaRepository;
        this.subcategoriaRepository = subcategoriaRepository;
        this.marcaRepository = marcaRepository;
        this.proveedorRepository = proveedorRepository;
        this.almacenRepository = almacenRepository;
    }

    @GetMapping("/categorias")
    public ResponseEntity<List<Map<String, Object>>> listarCategorias() {
        List<Map<String, Object>> result = categoriaRepository.findAll().stream()
                .map(c -> Map.<String, Object>of("id", c.getId(), "nombre", c.getNombre()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/subcategorias")
    public ResponseEntity<List<Map<String, Object>>> listarSubcategorias() {
        List<Map<String, Object>> result = subcategoriaRepository.findAll().stream()
                .map(s -> Map.<String, Object>of("id", s.getId(), "nombre", s.getNombre(),
                        "categoriaNombre", s.getCategoria().getNombre()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/marcas")
    public ResponseEntity<List<Map<String, Object>>> listarMarcas() {
        List<Map<String, Object>> result = marcaRepository.findAll().stream()
                .map(m -> Map.<String, Object>of("id", m.getId(), "nombre", m.getNombre()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/proveedores")
    public ResponseEntity<List<Map<String, Object>>> listarProveedores() {
        List<Map<String, Object>> result = proveedorRepository.findAll().stream()
                .map(p -> Map.<String, Object>of("id", p.getId(), "razonSocial", p.getRazonSocial(),
                        "ruc", p.getRuc()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/almacenes")
    public ResponseEntity<List<Map<String, Object>>> listarAlmacenes() {
        List<Map<String, Object>> result = almacenRepository.findAll().stream()
                .map(a -> Map.<String, Object>of("id", a.getId(), "nombre", a.getNombre(),
                        "ciudad", a.getCiudad()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
}
