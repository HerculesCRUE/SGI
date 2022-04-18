package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.repository.custom.CustomGrupoRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GrupoRepository
    extends JpaRepository<Grupo, Long>, JpaSpecificationExecutor<Grupo>, CustomGrupoRepository {

}
