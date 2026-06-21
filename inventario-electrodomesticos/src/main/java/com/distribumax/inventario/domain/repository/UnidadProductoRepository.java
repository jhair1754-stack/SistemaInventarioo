package com.distribumax.inventario.domain.repository;

import com.distribumax.inventario.domain.model.UnidadProducto;
import com.distribumax.inventario.domain.model.enums.EstadoUnidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UnidadProductoRepository extends JpaRepository<UnidadProducto, Long> {

    Optional<UnidadProducto> findByNumeroSerie(String numeroSerie);

    boolean existsByNumeroSerie(String numeroSerie);

    List<UnidadProducto> findByProductoId(Long productoId);

    List<UnidadProducto> findByProductoIdAndEstado(Long productoId, EstadoUnidad estado);

    long countByProductoIdAndEstado(Long productoId, EstadoUnidad estado);

}
