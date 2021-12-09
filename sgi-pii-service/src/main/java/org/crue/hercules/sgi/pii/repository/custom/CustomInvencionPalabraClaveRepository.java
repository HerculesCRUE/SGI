package org.crue.hercules.sgi.pii.repository.custom;

import org.crue.hercules.sgi.pii.model.Invencion;
import org.crue.hercules.sgi.pii.model.InvencionPalabraClave;
import org.springframework.data.jpa.repository.Modifying;

public interface CustomInvencionPalabraClaveRepository {
  /**
   * Elimina todos los {@link InvencionPalabraClave} cuyo invencionId coincide
   * con el indicado.
   * 
   * @param invencionId el identificador de la {@link Invencion} cuyas palabras
   *                    claves se desean eliminar
   * @return el número de registros eliminados
   */
  @Modifying
  int deleteInBulkByInvencionId(long invencionId);
}
