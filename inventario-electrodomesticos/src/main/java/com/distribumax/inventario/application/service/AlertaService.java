package com.distribumax.inventario.application.service;

import com.distribumax.inventario.application.dto.AlertaStockDTO;
import com.distribumax.inventario.domain.model.Producto;
import com.distribumax.inventario.domain.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AlertaService {

    private final ProductoRepository productoRepository;

    public AlertaService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public List<AlertaStockDTO> obtenerAlertas() {
        return productoRepository.findProductosConStockBajo()
                .stream().map(this::toAlertaDTO).collect(Collectors.toList());
    }

    private AlertaStockDTO toAlertaDTO(Producto p) {
        AlertaStockDTO dto = new AlertaStockDTO();
        dto.setProductoId(p.getId());
        dto.setProductoNombre(p.getNombre());
        dto.setCodigoSku(p.getCodigoSku());
        dto.setCategoriaNombre(p.getSubcategoria().getCategoria().getNombre());
        dto.setMarcaNombre(p.getMarca().getNombre());
        dto.setStockActual(p.getStockActual());
        dto.setStockMinimo(p.getStockMinimo());
        dto.setDeficit(p.calcularDeficit());
        return dto;
    }
}
