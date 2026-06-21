package com.distribumax.inventario.presentation.controller;

import com.distribumax.inventario.application.dto.InventarioResumenDTO;
import com.distribumax.inventario.application.dto.MovimientoCreateDTO;
import com.distribumax.inventario.application.dto.MovimientoDTO;
import com.distribumax.inventario.application.service.InventarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    private final InventarioService inventarioService;

    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    @PostMapping("/entrada")
    public ResponseEntity<MovimientoDTO> registrarEntrada(@Valid @RequestBody MovimientoCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventarioService.registrarEntrada(dto));
    }

    @PostMapping("/salida")
    public ResponseEntity<MovimientoDTO> registrarSalida(@Valid @RequestBody MovimientoCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventarioService.registrarSalida(dto));
    }

    @GetMapping("/resumen")
    public ResponseEntity<InventarioResumenDTO> obtenerResumen() {
        return ResponseEntity.ok(inventarioService.obtenerResumen());
    }
}
