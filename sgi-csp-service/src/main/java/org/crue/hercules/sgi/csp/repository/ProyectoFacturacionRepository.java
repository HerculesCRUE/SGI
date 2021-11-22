package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.ProyectoFacturacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProyectoFacturacionRepository
    extends JpaRepository<ProyectoFacturacion, Long>, JpaSpecificationExecutor<ProyectoFacturacion> {

  Page<ProyectoFacturacion> findByProyectoId(Long proyectoId, Pageable paging);
}
