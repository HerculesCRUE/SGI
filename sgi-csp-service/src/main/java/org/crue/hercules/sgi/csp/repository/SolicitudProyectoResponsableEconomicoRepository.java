package org.crue.hercules.sgi.csp.repository;

import java.util.List;

import org.crue.hercules.sgi.csp.model.SolicitudProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoResponsableEconomico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para
 * {@link SolicitudProyectoResponsableEconomico}.
 */
@Repository
public interface SolicitudProyectoResponsableEconomicoRepository
    extends JpaRepository<SolicitudProyectoResponsableEconomico, Long>,
    JpaSpecificationExecutor<SolicitudProyectoResponsableEconomico> {

  /**
   * Obtiene los {@link SolicitudProyectoResponsableEconomico} asociadas a un
   * {@link SolicitudProyecto}
   * 
   * @param solicitudProyectoId Identificador del {@link SolicitudProyecto}
   * @return Listado de {@link SolicitudProyectoResponsableEconomico} del
   *         {@link SolicitudProyecto}
   */
  List<SolicitudProyectoResponsableEconomico> findAllBySolicitudProyectoId(Long solicitudProyectoId);

}
