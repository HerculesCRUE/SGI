package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.TipoRequerimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TipoRequerimientoRepository
    extends JpaRepository<TipoRequerimiento, Long>, JpaSpecificationExecutor<TipoRequerimiento> {
}
