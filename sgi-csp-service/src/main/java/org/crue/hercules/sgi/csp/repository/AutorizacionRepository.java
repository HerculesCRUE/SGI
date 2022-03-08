package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.Autorizacion;
import org.crue.hercules.sgi.csp.repository.custom.CustomAutorizacionRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AutorizacionRepository
    extends JpaRepository<Autorizacion, Long>, JpaSpecificationExecutor<Autorizacion>, CustomAutorizacionRepository {

}
