package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.GrupoPersonaAutorizada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GrupoPersonaAutorizadaRepository
    extends JpaRepository<GrupoPersonaAutorizada, Long>, JpaSpecificationExecutor<GrupoPersonaAutorizada> {

}
