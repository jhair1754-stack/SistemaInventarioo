package com.distribumax.inventario.application.service;

import com.distribumax.inventario.application.dto.ProductoCreateDTO;
import com.distribumax.inventario.application.dto.ProductoDTO;
import com.distribumax.inventario.application.dto.UnidadProductoDTO;
import com.distribumax.inventario.domain.exception.RecursoNoEncontradoException;
import com.distribumax.inventario.domain.model.*;
import com.distribumax.inventario.domain.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final MarcaRepository marcaRepository;
    private final SubcategoriaRepository subcategoriaRepository;
    private final UnidadProductoRepository unidadProductoRepository;

    public ProductoService(ProductoRepository productoRepository, MarcaRepository marcaRepository,
                           SubcategoriaRepository subcategoriaRepository, UnidadProductoRepository unidadProductoRepository) {
        this.productoRepository = productoRepository;
        this.marcaRepository = marcaRepository;
        this.subcategoriaRepository = subcategoriaRepository;
        this.unidadProductoRepository = unidadProductoRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductoDTO> listarTodos() {
        return productoRepository.findByActivoTrue().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductoDTO buscarPorId(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", id));
        return toDTO(producto);
    }

    @Transactional(readOnly = true)
    public List<ProductoDTO> buscarConFiltros(Long categoriaId, Long marcaId, Long subcategoriaId, String busqueda) {
        return productoRepository.buscarConFiltros(categoriaId, marcaId, subcategoriaId, busqueda)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UnidadProductoDTO> listarUnidades(Long productoId) {
        productoRepository.findById(productoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", productoId));
        return unidadProductoRepository.findByProductoId(productoId)
                .stream().map(this::toUnidadDTO).collect(Collectors.toList());
    }

    public ProductoDTO crear(ProductoCreateDTO dto) {
        if (productoRepository.existsByCodigoSku(dto.getCodigoSku())) {
            throw new IllegalArgumentException("Ya existe un producto con el SKU: " + dto.getCodigoSku());
        }
        Marca marca = marcaRepository.findById(dto.getMarcaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Marca", dto.getMarcaId()));
        Subcategoria subcategoria = subcategoriaRepository.findById(dto.getSubcategoriaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Subcategoría", dto.getSubcategoriaId()));

        Producto producto = new Producto();
        producto.setCodigoSku(dto.getCodigoSku());
        producto.setNombre(dto.getNombre());
        producto.setModelo(dto.getModelo());
        producto.setMarca(marca);
        producto.setSubcategoria(subcategoria);
        producto.setPrecioUnitario(dto.getPrecioUnitario());
        producto.setPesoKg(dto.getPesoKg());
        producto.setAnchoCm(dto.getAnchoCm());
        producto.setAltoCm(dto.getAltoCm());
        producto.setProfundidadCm(dto.getProfundidadCm());
        producto.setStockMinimo(dto.getStockMinimo());
        producto.setStockActual(0);
        producto.setActivo(true);

        producto = productoRepository.save(producto);
        return toDTO(producto);
    }

    public ProductoDTO actualizar(Long id, ProductoCreateDTO dto) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", id));
        Marca marca = marcaRepository.findById(dto.getMarcaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Marca", dto.getMarcaId()));
        Subcategoria subcategoria = subcategoriaRepository.findById(dto.getSubcategoriaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Subcategoría", dto.getSubcategoriaId()));

        producto.setCodigoSku(dto.getCodigoSku());
        producto.setNombre(dto.getNombre());
        producto.setModelo(dto.getModelo());
        producto.setMarca(marca);
        producto.setSubcategoria(subcategoria);
        producto.setPrecioUnitario(dto.getPrecioUnitario());
        producto.setPesoKg(dto.getPesoKg());
        producto.setAnchoCm(dto.getAnchoCm());
        producto.setAltoCm(dto.getAltoCm());
        producto.setProfundidadCm(dto.getProfundidadCm());
        producto.setStockMinimo(dto.getStockMinimo());

        producto = productoRepository.save(producto);
        return toDTO(producto);
    }

    public void eliminar(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", id));
        producto.setActivo(false);
        productoRepository.save(producto);
    }

    private ProductoDTO toDTO(Producto p) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(p.getId());
        dto.setCodigoSku(p.getCodigoSku());
        dto.setNombre(p.getNombre());
        dto.setModelo(p.getModelo());
        dto.setMarcaId(p.getMarca().getId());
        dto.setMarcaNombre(p.getMarca().getNombre());
        dto.setSubcategoriaId(p.getSubcategoria().getId());
        dto.setSubcategoriaNombre(p.getSubcategoria().getNombre());
        dto.setCategoriaId(p.getSubcategoria().getCategoria().getId());
        dto.setCategoriaNombre(p.getSubcategoria().getCategoria().getNombre());
        dto.setPrecioUnitario(p.getPrecioUnitario());
        dto.setPesoKg(p.getPesoKg());
        dto.setAnchoCm(p.getAnchoCm());
        dto.setAltoCm(p.getAltoCm());
        dto.setProfundidadCm(p.getProfundidadCm());
        dto.setStockMinimo(p.getStockMinimo());
        dto.setStockActual(p.getStockActual());
        dto.setActivo(p.getActivo());
        dto.setStockBajo(p.tieneStockBajo());
        dto.setDeficit(p.calcularDeficit());
        return dto;
    }

    private UnidadProductoDTO toUnidadDTO(UnidadProducto u) {
        UnidadProductoDTO dto = new UnidadProductoDTO();
        dto.setId(u.getId());
        dto.setNumeroSerie(u.getNumeroSerie());
        dto.setProductoId(u.getProducto().getId());
        dto.setProductoNombre(u.getProducto().getNombre());
        dto.setUbicacionDescripcion(u.getUbicacion() != null ? u.getUbicacion().getDescripcionCompleta() : "Sin asignar");
        dto.setEstado(u.getEstado().name());
        dto.setFechaIngreso(u.getFechaIngreso());
        dto.setFechaGarantiaFin(u.getFechaGarantiaFin());
        return dto;
    }
}
