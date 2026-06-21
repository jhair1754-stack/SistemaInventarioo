package com.distribumax.inventario.presentation.controller;

import com.distribumax.inventario.application.dto.ProductoCreateDTO;
import com.distribumax.inventario.application.dto.ProductoDTO;
import com.distribumax.inventario.application.dto.UnidadProductoDTO;
import com.distribumax.inventario.application.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public ResponseEntity<List<ProductoDTO>> listar(
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) Long marcaId,
            @RequestParam(required = false) Long subcategoriaId,
            @RequestParam(required = false) String busqueda) {

        List<ProductoDTO> productos;
        if (categoriaId != null || marcaId != null || subcategoriaId != null || busqueda != null) {
            productos = productoService.buscarConFiltros(categoriaId, marcaId, subcategoriaId, busqueda);
        } else {
            productos = productoService.listarTodos();
        }
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<ProductoDTO> crear(@Valid @RequestBody ProductoCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoDTO> actualizar(@PathVariable Long id, @Valid @RequestBody ProductoCreateDTO dto) {
        return ResponseEntity.ok(productoService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/unidades")
    public ResponseEntity<List<UnidadProductoDTO>> listarUnidades(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.listarUnidades(id));
    }
}
