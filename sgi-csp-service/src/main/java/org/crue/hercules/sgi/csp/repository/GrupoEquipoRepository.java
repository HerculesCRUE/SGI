package org.crue.hercules.sgi.csp.repository;

import java.util.List;

import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.GrupoEquipo;
import org.crue.hercules.sgi.csp.repository.custom.CustomGrupoEquipoRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GrupoEquipoRepository extends JpaRepository<GrupoEquipo, Long>, JpaSpecificationExecutor<GrupoEquipo>,
    CustomGrupoEquipoRepository {

  /**
   * Devuelve un listado de {@link GrupoEquipo} asociados a un
   * {@link Grupo}.
   * 
   * @param grupoId Identificador de {@link Grupo}.
   * @return listado de {@link GrupoEquipo}.
   */
  List<GrupoEquipo> findAllByGrupoId(Long grupoId);

}
