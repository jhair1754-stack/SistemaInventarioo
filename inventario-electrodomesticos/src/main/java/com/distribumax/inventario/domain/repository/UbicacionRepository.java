package com.distribumax.inventario.domain.repository;

import com.distribumax.inventario.domain.model.Ubicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UbicacionRepository extends JpaRepository<Ubicacion, Long> {

    List<Ubicacion> findByAlmacenId(Long almacenId);

}
