package org.crue.hercules.sgi.prc.repository;

import java.util.List;

import org.crue.hercules.sgi.prc.model.ConvocatoriaBaremacionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link ConvocatoriaBaremacionLog}.
 */

@Repository
public interface ConvocatoriaBaremacionLogRepository
    extends JpaRepository<ConvocatoriaBaremacionLog, Long>, JpaSpecificationExecutor<ConvocatoriaBaremacionLog> {

  List<ConvocatoriaBaremacionLog> findAllByConvocatoriaBaremacionId(Long convocatoriaBaremacionId);

}
