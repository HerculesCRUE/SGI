package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.GrupoTipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GrupoTipoRepository extends JpaRepository<GrupoTipo, Long>, JpaSpecificationExecutor<GrupoTipo> {

}
