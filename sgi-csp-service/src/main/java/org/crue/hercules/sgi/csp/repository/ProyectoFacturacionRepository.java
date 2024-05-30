package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.crue.hercules.sgi.csp.model.ProyectoFacturacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProyectoFacturacionRepository
    extends JpaRepository<ProyectoFacturacion, Long>, JpaSpecificationExecutor<ProyectoFacturacion> {

  Page<ProyectoFacturacion> findByProyectoId(Long proyectoId, Pageable paging);

  Optional<ProyectoFacturacion> findFirstByProyectoIdOrderByNumeroPrevisionDesc(Long proyectoId);

  boolean existsByProyectoProrrogaId(Long proyectoProrrogaId);
}
