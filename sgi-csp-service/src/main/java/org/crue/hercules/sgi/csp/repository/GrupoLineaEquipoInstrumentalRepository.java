package org.crue.hercules.sgi.csp.repository;

import java.util.List;

import org.crue.hercules.sgi.csp.model.GrupoLineaEquipoInstrumental;
import org.crue.hercules.sgi.csp.model.GrupoLineaInvestigacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GrupoLineaEquipoInstrumentalRepository
    extends JpaRepository<GrupoLineaEquipoInstrumental, Long>, JpaSpecificationExecutor<GrupoLineaEquipoInstrumental> {

  /**
   * Devuelve un listado de {@link GrupoLineaEquipoInstrumental} asociados a un
   * {@link GrupoLineaInvestigacion}.
   * 
   * @param grupoLineaInvestigacionId Identificador de
   *                                  {@link GrupoLineaInvestigacion}.
   * @return listado de {@link GrupoLineaEquipoInstrumental}.
   */
  List<GrupoLineaEquipoInstrumental> findAllByGrupoLineaInvestigacionId(Long grupoLineaInvestigacionId);

}
