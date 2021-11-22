package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.TipoFacturacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TipoFacturacionRepository
    extends JpaRepository<TipoFacturacion, Long>, JpaSpecificationExecutor<TipoFacturacion> {
}
