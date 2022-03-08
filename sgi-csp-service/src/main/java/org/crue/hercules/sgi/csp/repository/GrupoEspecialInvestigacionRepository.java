package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.GrupoEspecialInvestigacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GrupoEspecialInvestigacionRepository
    extends JpaRepository<GrupoEspecialInvestigacion, Long>, JpaSpecificationExecutor<GrupoEspecialInvestigacion> {

}
