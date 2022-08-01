package org.crue.hercules.sgi.pii.repository;

import java.time.Instant;
import java.util.List;

import org.crue.hercules.sgi.framework.data.jpa.repository.support.SgiRepository;
import org.crue.hercules.sgi.pii.model.Procedimiento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link Procedimiento}.
 */
@Repository
public interface ProcedimientoRepository
    extends SgiRepository<Procedimiento, Long>, JpaSpecificationExecutor<Procedimiento> {

  Page<Procedimiento> findAllBySolicitudProteccionId(Long solicitudProteccionId, Pageable pageable);

  List<Procedimiento> findByFechaLimiteAccionBetweenAndGenerarAvisoTrue(Instant fechaLimiteFrom, Instant fechaLimiteTo);

}
