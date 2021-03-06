package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.AgrupacionGastoConcepto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AgrupacionGastoConceptoRepository
    extends JpaRepository<AgrupacionGastoConcepto, Long>, JpaSpecificationExecutor<AgrupacionGastoConcepto> {

}
