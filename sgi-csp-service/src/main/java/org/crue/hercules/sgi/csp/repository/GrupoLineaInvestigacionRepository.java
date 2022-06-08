package org.crue.hercules.sgi.csp.repository;

import java.util.List;

import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.GrupoLineaInvestigacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GrupoLineaInvestigacionRepository
    extends JpaRepository<GrupoLineaInvestigacion, Long>, JpaSpecificationExecutor<GrupoLineaInvestigacion> {

  /**
   * Devuelve un listado de {@link GrupoLineaInvestigacion} asociados a un
   * {@link Grupo}.
   * 
   * @param grupoId Identificador de {@link Grupo}.
   * @return listado de {@link GrupoLineaInvestigacion}.
   */
  List<GrupoLineaInvestigacion> findAllByGrupoId(Long grupoId);

}
