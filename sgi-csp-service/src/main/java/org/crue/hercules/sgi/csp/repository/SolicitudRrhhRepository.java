package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.crue.hercules.sgi.csp.model.SolicitudRrhh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SolicitudRrhhRepository
    extends JpaRepository<SolicitudRrhh, Long>, JpaSpecificationExecutor<SolicitudRrhh> {

  Optional<SolicitudRrhh> findBySolicitudId(Long solicitudId);

}
