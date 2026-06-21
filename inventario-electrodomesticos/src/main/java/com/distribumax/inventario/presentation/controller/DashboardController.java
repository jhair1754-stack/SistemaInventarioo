package com.distribumax.inventario.presentation.controller;

import com.distribumax.inventario.application.dto.AlertaStockDTO;
import com.distribumax.inventario.application.dto.InventarioResumenDTO;
import com.distribumax.inventario.application.service.AlertaService;
import com.distribumax.inventario.application.service.InventarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final InventarioService inventarioService;
    private final AlertaService alertaService;

    public DashboardController(InventarioService inventarioService, AlertaService alertaService) {
        this.inventarioService = inventarioService;
        this.alertaService = alertaService;
    }

    @GetMapping
    public ResponseEntity<InventarioResumenDTO> obtenerResumen() {
        return ResponseEntity.ok(inventarioService.obtenerResumen());
    }

    @GetMapping("/alertas")
    public ResponseEntity<List<AlertaStockDTO>> obtenerAlertas() {
        return ResponseEntity.ok(alertaService.obtenerAlertas());
    }
}
