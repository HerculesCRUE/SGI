package org.crue.hercules.sgi.csp.repository.custom;

import org.crue.hercules.sgi.csp.model.IncidenciaDocumentacionRequerimiento;
import org.crue.hercules.sgi.csp.model.RequerimientoJustificacion;
import org.springframework.data.jpa.repository.Modifying;

public interface CustomIncidenciaDocumentacionRequerimientoRepository {
  /**
   * Elimina todos las entidades {@link IncidenciaDocumentacionRequerimiento} cuyo
   * requerimientoJustificacionId coincide con el indicado.
   * 
   * @param requerimientoJustificacionId el identificador de la
   *                                     {@link RequerimientoJustificacion}.
   * @return el número de registros eliminados.
   */
  @Modifying
  int deleteInBulkByRequerimientoJustificacionId(long requerimientoJustificacionId);
}
