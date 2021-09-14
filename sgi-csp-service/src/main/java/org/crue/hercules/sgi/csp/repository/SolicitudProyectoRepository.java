package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.SolicitudProyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SolicitudProyectoRepository
    extends JpaRepository<SolicitudProyecto, Long>, JpaSpecificationExecutor<SolicitudProyecto> {

}
