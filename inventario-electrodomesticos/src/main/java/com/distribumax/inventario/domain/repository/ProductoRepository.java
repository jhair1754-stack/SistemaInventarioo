package com.distribumax.inventario.domain.repository;

import com.distribumax.inventario.domain.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de acceso a datos para Producto.
 * Spring Data JPA genera la implementación automáticamente.
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    Optional<Producto> findByCodigoSku(String codigoSku);

    boolean existsByCodigoSku(String codigoSku);

    List<Producto> findByActivoTrue();

    @Query("SELECT p FROM Producto p WHERE p.activo = true AND p.stockActual < p.stockMinimo")
    List<Producto> findProductosConStockBajo();

    @Query("SELECT p FROM Producto p WHERE p.activo = true " +
           "AND (:categoriaId IS NULL OR p.subcategoria.categoria.id = :categoriaId) " +
           "AND (:marcaId IS NULL OR p.marca.id = :marcaId) " +
           "AND (:subcategoriaId IS NULL OR p.subcategoria.id = :subcategoriaId) " +
           "AND (:busqueda IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) " +
           "     OR LOWER(p.codigoSku) LIKE LOWER(CONCAT('%', :busqueda, '%')))")
    List<Producto> buscarConFiltros(
        @Param("categoriaId") Long categoriaId,
        @Param("marcaId") Long marcaId,
        @Param("subcategoriaId") Long subcategoriaId,
        @Param("busqueda") String busqueda
    );

}
