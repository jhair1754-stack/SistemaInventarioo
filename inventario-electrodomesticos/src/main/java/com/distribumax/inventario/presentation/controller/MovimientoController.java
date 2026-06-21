package com.distribumax.inventario.presentation.controller;

import com.distribumax.inventario.application.dto.MovimientoDTO;
import com.distribumax.inventario.application.service.MovimientoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/movimientos")
public class MovimientoController {

    private final MovimientoService movimientoService;

    public MovimientoController(MovimientoService movimientoService) {
        this.movimientoService = movimientoService;
    }

    @GetMapping
    public ResponseEntity<List<MovimientoDTO>> listar(
            @RequestParam(required = false) Long productoId,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {

        List<MovimientoDTO> movimientos;
        if (productoId != null || tipo != null || desde != null || hasta != null) {
            movimientos = movimientoService.listarMovimientos(productoId, tipo, desde, hasta);
        } else {
            movimientos = movimientoService.listarTodos();
        }
        return ResponseEntity.ok(movimientos);
    }
}
