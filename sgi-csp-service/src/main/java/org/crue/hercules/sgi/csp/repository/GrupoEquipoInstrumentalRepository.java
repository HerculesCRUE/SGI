package org.crue.hercules.sgi.csp.repository;

import java.util.List;

import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.GrupoEquipoInstrumental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GrupoEquipoInstrumentalRepository
    extends JpaRepository<GrupoEquipoInstrumental, Long>, JpaSpecificationExecutor<GrupoEquipoInstrumental> {

  /**
   * Devuelve un listado de {@link GrupoEquipoInstrumental} asociados a un
   * {@link Grupo}.
   * 
   * @param grupoId Identificador de {@link Grupo}.
   * @return listado de {@link GrupoEquipoInstrumental}.
   */
  List<GrupoEquipoInstrumental> findAllByGrupoId(Long grupoId);

}
