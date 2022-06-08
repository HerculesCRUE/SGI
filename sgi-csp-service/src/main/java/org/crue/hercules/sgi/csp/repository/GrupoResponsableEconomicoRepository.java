package org.crue.hercules.sgi.csp.repository;

import java.util.List;

import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.GrupoResponsableEconomico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GrupoResponsableEconomicoRepository
    extends JpaRepository<GrupoResponsableEconomico, Long>, JpaSpecificationExecutor<GrupoResponsableEconomico> {

  /**
   * Devuelve un listado de {@link GrupoResponsableEconomico} asociados a un
   * {@link Grupo}.
   * 
   * @param grupoId Identificador de {@link Grupo}.
   * @return listado de {@link GrupoResponsableEconomico}.
   */
  List<GrupoResponsableEconomico> findAllByGrupoId(Long grupoId);

}
