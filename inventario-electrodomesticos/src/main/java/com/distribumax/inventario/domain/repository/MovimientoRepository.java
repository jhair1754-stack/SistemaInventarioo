package com.distribumax.inventario.domain.repository;

import com.distribumax.inventario.domain.model.MovimientoInventario;
import com.distribumax.inventario.domain.model.enums.TipoMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimientoRepository extends JpaRepository<MovimientoInventario, Long> {

    List<MovimientoInventario> findByProductoIdOrderByFechaMovimientoDesc(Long productoId);

    @Query("SELECT m FROM MovimientoInventario m " +
           "WHERE (:productoId IS NULL OR m.producto.id = :productoId) " +
           "AND (:tipo IS NULL OR m.tipoMovimiento = :tipo) " +
           "AND (:desde IS NULL OR m.fechaMovimiento >= :desde) " +
           "AND (:hasta IS NULL OR m.fechaMovimiento <= :hasta) " +
           "ORDER BY m.fechaMovimiento DESC")
    List<MovimientoInventario> buscarConFiltros(
        @Param("productoId") Long productoId,
        @Param("tipo") TipoMovimiento tipo,
        @Param("desde") LocalDateTime desde,
        @Param("hasta") LocalDateTime hasta
    );

    long countByFechaMovimientoBetween(LocalDateTime desde, LocalDateTime hasta);

}
