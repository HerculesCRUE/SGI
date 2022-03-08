package org.crue.hercules.sgi.prc.repository.custom;

import org.crue.hercules.sgi.prc.model.ConvocatoriaBaremacion;
import org.crue.hercules.sgi.prc.model.Modulador;
import org.springframework.data.jpa.repository.Modifying;

public interface CustomModuladorRepository {
  /**
   * Elimina todos los {@link Modulador} cuyo convocatoriaBaremacionId
   * coincide con el indicado.
   * 
   * @param convocatoriaBaremacionId el identificador de la
   *                                 {@link ConvocatoriaBaremacion}
   * @return el número de registros eliminados
   */
  @Modifying
  int deleteInBulkByConvocatoriaBaremacionId(Long convocatoriaBaremacionId);
}
