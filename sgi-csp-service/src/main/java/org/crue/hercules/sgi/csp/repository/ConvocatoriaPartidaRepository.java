package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.ConvocatoriaPartida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link ConvocatoriaPartida}.
 */
@Repository
public interface ConvocatoriaPartidaRepository
    extends JpaRepository<ConvocatoriaPartida, Long>, JpaSpecificationExecutor<ConvocatoriaPartida> {

  /**
   * Comprueba si existe algún {@link ConvocatoriaPartida} relacionado con el Id
   * de Convocatoria recibido
   * 
   * @param convocatoriaId Id de Convocatoria
   * @return <code>true</code> Si existe algúna relación, <code>false</code> en
   *         cualquier otro caso.
   */
  boolean existsByConvocatoriaId(Long convocatoriaId);

}