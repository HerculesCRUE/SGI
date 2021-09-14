package org.crue.hercules.sgi.pii.repository.custom;

import org.crue.hercules.sgi.pii.model.Invencion;
import org.crue.hercules.sgi.pii.model.InvencionSectorAplicacion;
import org.springframework.data.jpa.repository.Modifying;

public interface CustomInvencionSectorAplicacionRepository {
  /**
   * Elimina todos los {@link InvencionSectorAplicacion} cuyo invencionId coincide
   * con el indicado.
   * 
   * @param invencionId el identificador de la {@link Invencion} cuyos sectores de
   *                    aplicación se desean eliminar
   * @return el número de registros eliminados
   */
  @Modifying
  int deleteInBulkByInvencionId(long invencionId);
}
