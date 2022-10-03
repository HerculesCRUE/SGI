package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.IncidenciaDocumentacionRequerimiento;
import org.crue.hercules.sgi.csp.repository.custom.CustomIncidenciaDocumentacionRequerimientoRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IncidenciaDocumentacionRequerimientoRepository
    extends JpaRepository<IncidenciaDocumentacionRequerimiento, Long>,
    JpaSpecificationExecutor<IncidenciaDocumentacionRequerimiento>,
    CustomIncidenciaDocumentacionRequerimientoRepository {
}
