package org.crue.hercules.sgi.pii.repository;

import org.crue.hercules.sgi.pii.model.InvencionAreaConocimiento;
import org.crue.hercules.sgi.pii.repository.custom.CustomInvencionAreaConocimientoRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InvencionAreaConocimientoRepository extends JpaRepository<InvencionAreaConocimiento, Long>,
    JpaSpecificationExecutor<InvencionAreaConocimiento>, CustomInvencionAreaConocimientoRepository {

}
