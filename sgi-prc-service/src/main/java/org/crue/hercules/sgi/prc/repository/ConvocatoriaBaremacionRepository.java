package org.crue.hercules.sgi.prc.repository;

import java.util.Optional;

import org.crue.hercules.sgi.prc.model.ConvocatoriaBaremacion;
import org.crue.hercules.sgi.prc.repository.custom.CustomConvocatoriaBaremacionRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link ConvocatoriaBaremacion}.
 */

@Repository
public interface ConvocatoriaBaremacionRepository
    extends JpaRepository<ConvocatoriaBaremacion, Long>, JpaSpecificationExecutor<ConvocatoriaBaremacion>,
    CustomConvocatoriaBaremacionRepository {

  /**
   * Obtiene la entidad {@link ConvocatoriaBaremacion} activa con el año
   * indicado
   *
   * @param anio el año de {@link ConvocatoriaBaremacion}.
   * @return el {@link ConvocatoriaBaremacion} con el año indicado
   */
  Optional<ConvocatoriaBaremacion> findByAnioAndActivoIsTrue(Integer anio);
}
