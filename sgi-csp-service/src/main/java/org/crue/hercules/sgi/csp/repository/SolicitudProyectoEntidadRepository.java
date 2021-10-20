package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.SolicitudProyectoEntidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SolicitudProyectoEntidadRepository
    extends JpaRepository<SolicitudProyectoEntidad, Long>, JpaSpecificationExecutor<SolicitudProyectoEntidad> {

  SolicitudProyectoEntidad findBySolicitudProyectoEntidadFinanciadoraAjenaId(
      Long solicitudProyectoEntidadFinanciadoraAjenaId);

}
