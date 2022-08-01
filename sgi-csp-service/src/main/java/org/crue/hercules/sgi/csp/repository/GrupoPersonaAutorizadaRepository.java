package org.crue.hercules.sgi.csp.repository;

import java.util.List;

import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.GrupoPersonaAutorizada;
import org.crue.hercules.sgi.csp.repository.custom.CustomGrupoPersonaAutorizadaRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GrupoPersonaAutorizadaRepository
    extends JpaRepository<GrupoPersonaAutorizada, Long>, JpaSpecificationExecutor<GrupoPersonaAutorizada>,
    CustomGrupoPersonaAutorizadaRepository {

  /**
   * Devuelve un listado de {@link GrupoPersonaAutorizada} asociados a un
   * {@link Grupo}.
   * 
   * @param grupoId Identificador de {@link Grupo}.
   * @return listado de {@link GrupoPersonaAutorizada}.
   */
  List<GrupoPersonaAutorizada> findAllByGrupoId(Long grupoId);

}
