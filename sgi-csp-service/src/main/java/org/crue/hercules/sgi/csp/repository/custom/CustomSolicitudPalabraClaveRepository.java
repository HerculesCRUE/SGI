package org.crue.hercules.sgi.csp.repository.custom;

import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudPalabraClave;
import org.springframework.data.jpa.repository.Modifying;

public interface CustomSolicitudPalabraClaveRepository {
  /**
   * Elimina todos los {@link SolicitudPalabraClave} cuyo solicitudId coincide
   * con el indicado.
   * 
   * @param solicitudId el identificador de la {@link Solicitud} cuyas palabras
   *                    claves se desean eliminar
   * @return el número de registros eliminados
   */
  @Modifying
  int deleteInBulkBySolicitudId(long solicitudId);
}
