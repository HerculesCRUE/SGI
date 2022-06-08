package org.crue.hercules.sgi.csp.repository;

import java.util.List;

import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.GrupoEnlace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GrupoEnlaceRepository
    extends JpaRepository<GrupoEnlace, Long>, JpaSpecificationExecutor<GrupoEnlace> {

  /**
   * Devuelve un listado de {@link GrupoEnlace} asociados a un
   * {@link Grupo}.
   * 
   * @param grupoId Identificador de {@link Grupo}.
   * @return listado de {@link GrupoEnlace}.
   */
  List<GrupoEnlace> findAllByGrupoId(Long grupoId);

}
