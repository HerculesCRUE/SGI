package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.ConvocatoriaDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ConvocatoriaDocumentoRepository
    extends JpaRepository<ConvocatoriaDocumento, Long>, JpaSpecificationExecutor<ConvocatoriaDocumento> {

  /**
   * Comprueba si existe algún {@link ConvocatoriaDocumento} relacionado con el Id
   * de Convocatoria recibido
   * 
   * @param convocatoriaId Id de Convocatoria
   * @return <code>true</code> Si existe algúna relación, <code>false</code> en
   *         cualquier otro caso.
   */
  boolean existsByConvocatoriaId(Long convocatoriaId);
}
