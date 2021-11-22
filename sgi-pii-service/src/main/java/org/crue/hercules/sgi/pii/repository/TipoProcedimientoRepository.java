package org.crue.hercules.sgi.pii.repository;

import org.crue.hercules.sgi.pii.model.TipoProcedimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TipoProcedimientoRepository
    extends JpaRepository<TipoProcedimiento, Long>, JpaSpecificationExecutor<TipoProcedimiento> {

}
