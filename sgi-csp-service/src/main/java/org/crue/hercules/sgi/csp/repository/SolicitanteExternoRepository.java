package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.crue.hercules.sgi.csp.model.SolicitanteExterno;
import org.crue.hercules.sgi.csp.repository.custom.CustomGrupoRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SolicitanteExternoRepository
    extends JpaRepository<SolicitanteExterno, Long>, JpaSpecificationExecutor<SolicitanteExterno>,
    CustomGrupoRepository {

  Optional<SolicitanteExterno> findBySolicitudId(Long solicitudId);

}
