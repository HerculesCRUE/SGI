package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.EstadoProyectoPeriodoJustificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EstadoProyectoPeriodoJustificacionRepository
    extends JpaRepository<EstadoProyectoPeriodoJustificacion, Long>,
    JpaSpecificationExecutor<EstadoProyectoPeriodoJustificacion> {

}
