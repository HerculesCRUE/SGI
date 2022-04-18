package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.SolicitudGrupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SolicitudGrupoRepository
    extends JpaRepository<SolicitudGrupo, Long>, JpaSpecificationExecutor<SolicitudGrupo> {

  SolicitudGrupo findBySolicitudId(Long solicitudId);
}