package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.ProyectoSeguimientoJustificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProyectoSeguimientoJustificacionRepository
    extends JpaRepository<ProyectoSeguimientoJustificacion, Long>,
    JpaSpecificationExecutor<ProyectoSeguimientoJustificacion> {

}
