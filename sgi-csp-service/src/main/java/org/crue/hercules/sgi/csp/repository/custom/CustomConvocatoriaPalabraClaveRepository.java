package org.crue.hercules.sgi.csp.repository.custom;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPalabraClave;
import org.springframework.data.jpa.repository.Modifying;

public interface CustomConvocatoriaPalabraClaveRepository {
  /**
   * Elimina todos los {@link ConvocatoriaPalabraClave} cuyo convocatoriaId
   * coincide con el indicado.
   * 
   * @param convocatoriaId el identificador de la {@link Convocatoria} cuyas
   *                       palabras
   *                       claves se desean eliminar
   * @return el número de registros eliminados
   */
  @Modifying
  int deleteInBulkByConvocatoriaId(long convocatoriaId);
}
