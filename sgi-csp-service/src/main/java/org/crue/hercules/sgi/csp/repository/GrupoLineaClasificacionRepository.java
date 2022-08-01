package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.GrupoLineaClasificacion;
import org.crue.hercules.sgi.csp.model.GrupoLineaInvestigacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link GrupoLineaClasificacion}.
 */
@Repository
public interface GrupoLineaClasificacionRepository
    extends JpaRepository<GrupoLineaClasificacion, Long>, JpaSpecificationExecutor<GrupoLineaClasificacion> {

  /**
   * Se eliminan todos los {@link GrupoLineaClasificacion} a partir de un
   * {@link GrupoLineaInvestigacion}
   * 
   * @param grupoLineaInvestigacionId Identificador de
   *                                  {@link GrupoLineaInvestigacion}
   */
  void deleteAllByGrupoLineaInvestigacionId(Long grupoLineaInvestigacionId);
}
