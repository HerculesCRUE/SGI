package org.crue.hercules.sgi.prc.repository;

import org.crue.hercules.sgi.prc.model.ConvocatoriaBaremacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link ConvocatoriaBaremacion}.
 */

@Repository
public interface ConvocatoriaBaremacionRepository
    extends JpaRepository<ConvocatoriaBaremacion, Long>, JpaSpecificationExecutor<ConvocatoriaBaremacion> {

}
