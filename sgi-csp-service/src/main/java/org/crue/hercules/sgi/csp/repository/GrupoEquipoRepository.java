package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.GrupoEquipo;
import org.crue.hercules.sgi.csp.repository.custom.CustomGrupoEquipoRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GrupoEquipoRepository extends JpaRepository<GrupoEquipo, Long>, JpaSpecificationExecutor<GrupoEquipo>,
    CustomGrupoEquipoRepository {

}
