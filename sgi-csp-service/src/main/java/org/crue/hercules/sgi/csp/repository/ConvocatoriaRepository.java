package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.repository.custom.CustomConvocatoriaRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ConvocatoriaRepository
    extends JpaRepository<Convocatoria, Long>, JpaSpecificationExecutor<Convocatoria>, CustomConvocatoriaRepository {

  /**
   * Obtiene la entidad {@link Convocatoria} con el código indicado
   *
   * @param codigo el nombre de {@link Convocatoria}.
   * @return el {@link Convocatoria} con el codigo indicado
   */
  Optional<Convocatoria> findByCodigo(String codigo);
}
