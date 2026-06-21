package com.distribumax.inventario.application.service;

import com.distribumax.inventario.application.dto.InventarioResumenDTO;
import com.distribumax.inventario.application.dto.MovimientoCreateDTO;
import com.distribumax.inventario.application.dto.MovimientoDTO;
import com.distribumax.inventario.domain.exception.*;
import com.distribumax.inventario.domain.model.*;
import com.distribumax.inventario.domain.model.enums.EstadoUnidad;
import com.distribumax.inventario.domain.model.enums.TipoMovimiento;
import com.distribumax.inventario.domain.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@Transactional
public class InventarioService {

    private final ProductoRepository productoRepository;
    private final AlmacenRepository almacenRepository;
    private final ProveedorRepository proveedorRepository;
    private final MovimientoRepository movimientoRepository;
    private final UnidadProductoRepository unidadProductoRepository;
    private final UbicacionRepository ubicacionRepository;

    public InventarioService(ProductoRepository productoRepository, AlmacenRepository almacenRepository,
                             ProveedorRepository proveedorRepository, MovimientoRepository movimientoRepository,
                             UnidadProductoRepository unidadProductoRepository, UbicacionRepository ubicacionRepository) {
        this.productoRepository = productoRepository;
        this.almacenRepository = almacenRepository;
        this.proveedorRepository = proveedorRepository;
        this.movimientoRepository = movimientoRepository;
        this.unidadProductoRepository = unidadProductoRepository;
        this.ubicacionRepository = ubicacionRepository;
    }

    public MovimientoDTO registrarEntrada(MovimientoCreateDTO dto) {
        Producto producto = productoRepository.findById(dto.getProductoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", dto.getProductoId()));
        Almacen almacen = almacenRepository.findById(dto.getAlmacenId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Almacén", dto.getAlmacenId()));
        Proveedor proveedor = null;
        if (dto.getProveedorId() != null) {
            proveedor = proveedorRepository.findById(dto.getProveedorId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Proveedor", dto.getProveedorId()));
        }

        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setProducto(producto);
        movimiento.setAlmacen(almacen);
        movimiento.setProveedor(proveedor);
        movimiento.setTipoMovimiento(TipoMovimiento.ENTRADA);
        movimiento.setCantidad(dto.getCantidad());
        movimiento.setNumeroLote(dto.getNumeroLote());
        movimiento.setObservaciones(dto.getObservaciones());
        movimiento.setFechaMovimiento(LocalDateTime.now());
        movimientoRepository.save(movimiento);

        producto.incrementarStock(dto.getCantidad());
        productoRepository.save(producto);

        if (dto.getNumerosSerie() != null && !dto.getNumerosSerie().isEmpty()) {
            Ubicacion ubicacionDefault = ubicacionRepository.findByAlmacenId(almacen.getId())
                    .stream().findFirst().orElse(null);
            for (String serie : dto.getNumerosSerie()) {
                if (unidadProductoRepository.existsByNumeroSerie(serie)) {
                    throw new NumeroSerieExistenteException(serie);
                }
                UnidadProducto unidad = new UnidadProducto();
                unidad.setNumeroSerie(serie);
                unidad.setProducto(producto);
                unidad.setUbicacion(ubicacionDefault);
                unidad.setEstado(EstadoUnidad.DISPONIBLE);
                unidad.setFechaIngreso(LocalDate.now());
                unidad.setFechaGarantiaFin(LocalDate.now().plusYears(1));
                unidadProductoRepository.save(unidad);
            }
        }
        return toMovimientoDTO(movimiento);
    }

    public MovimientoDTO registrarSalida(MovimientoCreateDTO dto) {
        Producto producto = productoRepository.findById(dto.getProductoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", dto.getProductoId()));
        if (producto.getStockActual() < dto.getCantidad()) {
            throw new StockInsuficienteException(producto.getNombre(), producto.getStockActual(), dto.getCantidad());
        }
        Almacen almacen = almacenRepository.findById(dto.getAlmacenId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Almacén", dto.getAlmacenId()));

        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setProducto(producto);
        movimiento.setAlmacen(almacen);
        movimiento.setTipoMovimiento(TipoMovimiento.SALIDA);
        movimiento.setCantidad(dto.getCantidad());
        movimiento.setNumeroLote(dto.getNumeroLote());
        movimiento.setObservaciones(dto.getObservaciones());
        movimiento.setFechaMovimiento(LocalDateTime.now());
        movimientoRepository.save(movimiento);

        producto.decrementarStock(dto.getCantidad());
        productoRepository.save(producto);

        if (dto.getNumerosSerie() != null) {
            for (String serie : dto.getNumerosSerie()) {
                unidadProductoRepository.findByNumeroSerie(serie).ifPresent(unidad -> {
                    unidad.setEstado(EstadoUnidad.VENDIDO);
                    unidadProductoRepository.save(unidad);
                });
            }
        }
        return toMovimientoDTO(movimiento);
    }

    @Transactional(readOnly = true)
    public InventarioResumenDTO obtenerResumen() {
        List<Producto> productosActivos = productoRepository.findByActivoTrue();
        long totalProductos = productosActivos.size();
        long totalUnidades = unidadProductoRepository.count();
        BigDecimal valorTotal = productosActivos.stream()
                .map(p -> p.getPrecioUnitario().multiply(BigDecimal.valueOf(p.getStockActual())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        LocalDateTime inicioHoy = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime finHoy = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        long movimientosHoy = movimientoRepository.countByFechaMovimientoBetween(inicioHoy, finHoy);
        long productosStockBajo = productosActivos.stream().filter(Producto::tieneStockBajo).count();

        InventarioResumenDTO resumen = new InventarioResumenDTO();
        resumen.setTotalProductos(totalProductos);
        resumen.setTotalUnidades(totalUnidades);
        resumen.setValorTotalInventario(valorTotal);
        resumen.setMovimientosHoy(movimientosHoy);
        resumen.setProductosStockBajo(productosStockBajo);
        return resumen;
    }

    private MovimientoDTO toMovimientoDTO(MovimientoInventario m) {
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
