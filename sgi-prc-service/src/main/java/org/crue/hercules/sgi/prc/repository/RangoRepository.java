package org.crue.hercules.sgi.prc.repository;

import java.util.List;

import org.crue.hercules.sgi.prc.model.Rango;
import org.crue.hercules.sgi.prc.repository.custom.CustomRangoRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link Rango}.
 */

@Repository
public interface RangoRepository
    extends JpaRepository<Rango, Long>, JpaSpecificationExecutor<Rango>, CustomRangoRepository {

  List<Rango> findByConvocatoriaBaremacionId(Long convocatoriaBaremacionId);

}
