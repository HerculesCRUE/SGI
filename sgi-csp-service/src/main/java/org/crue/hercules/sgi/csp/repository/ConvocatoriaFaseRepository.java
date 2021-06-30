package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.ConvocatoriaFase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ConvocatoriaFaseRepository
    extends JpaRepository<ConvocatoriaFase, Long>, JpaSpecificationExecutor<ConvocatoriaFase> {

  /**
   * Comprueba si existe algún {@link ConvocatoriaFase} relacionado con el Id de
   * Convocatoria recibido
   * 
   * @param convocatoriaId Id de Convocatoria
   * @return <code>true</code> Si existe algúna relación, <code>false</code> en
   *         cualquier otro caso.
   */
  boolean existsByConvocatoriaId(Long convocatoriaId);
}
