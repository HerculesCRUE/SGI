package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.crue.hercules.sgi.csp.model.RequisitoIP;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RequisitoIPRepository extends JpaRepository<RequisitoIP, Long>, JpaSpecificationExecutor<RequisitoIP> {

  /**
   * Obtiene el {@link RequisitoIP} de una {@link Convocatoria}
   * 
   * @param id identificador de la {@link Convocatoria}
   * @return la entidad {@link RequisitoIP}
   */
  Optional<RequisitoIP> findByConvocatoriaId(Long id);

}
