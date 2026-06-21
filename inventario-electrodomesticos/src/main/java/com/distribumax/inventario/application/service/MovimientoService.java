package com.distribumax.inventario.application.service;

import com.distribumax.inventario.application.dto.MovimientoDTO;
import com.distribumax.inventario.domain.model.MovimientoInventario;
import com.distribumax.inventario.domain.model.enums.TipoMovimiento;
import com.distribumax.inventario.domain.repository.MovimientoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class MovimientoService {

    private final MovimientoRepository movimientoRepository;

    public MovimientoService(MovimientoRepository movimientoRepository) {
        this.movimientoRepository = movimientoRepository;
    }

    public List<MovimientoDTO> listarMovimientos(Long productoId, String tipo, LocalDate desde, LocalDate hasta) {
        TipoMovimiento tipoEnum = null;
        if (tipo != null && !tipo.isEmpty()) {
            tipoEnum = TipoMovimiento.valueOf(tipo.toUpperCase());
        }
        LocalDateTime desdeDateTime = (desde != null) ? LocalDateTime.of(desde, LocalTime.MIN) : null;
        LocalDateTime hastaDateTime = (hasta != null) ? LocalDateTime.of(hasta, LocalTime.MAX) : null;
        return movimientoRepository.buscarConFiltros(productoId, tipoEnum, desdeDateTime, hastaDateTime)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<MovimientoDTO> listarTodos() {
        return movimientoRepository.findAll().stream()
                .sorted((a, b) -> b.getFechaMovimiento().compareTo(a.getFechaMovimiento()))
                .map(this::toDTO).collect(Collectors.toList());
    }

    private MovimientoDTO toDTO(MovimientoInventario m) {
        MovimientoDTO dto = new MovimientoDTO();
        dto.setId(m.getId());
        dto.setProductoId(m.getProducto().getId());
        dto.setProductoNombre(m.getProducto().getNombre());
        dto.setProductoSku(m.getProducto().getCodigoSku());
        dto.setAlmacenNombre(m.getAlmacen().getNombre());
        dto.setProveedorNombre(m.getProveedor() != null ? m.getProveedor().getRazonSocial() : null);
        dto.setTipoMovimiento(m.getTipoMovimiento().name());
        dto.setCantidad(m.getCantidad());
        dto.setNumeroLote(m.getNumeroLote());
        dto.setObservaciones(m.getObservaciones());
        dto.setFechaMovimiento(m.getFechaMovimiento());
        return dto;
    }
}
