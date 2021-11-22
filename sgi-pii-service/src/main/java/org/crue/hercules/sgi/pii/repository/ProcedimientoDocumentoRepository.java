package org.crue.hercules.sgi.pii.repository;

import org.crue.hercules.sgi.pii.model.ProcedimientoDocumento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link ProcedimientoDocumento}.
 */
@Repository
public interface ProcedimientoDocumentoRepository
    extends JpaRepository<ProcedimientoDocumento, Long>, JpaSpecificationExecutor<ProcedimientoDocumento> {

  Page<ProcedimientoDocumento> findAllByProcedimientoId(Long procedimientoId, Pageable pageable);

  void deleteAllByProcedimientoId(Long procedimientoId);

}
