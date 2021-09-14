package org.crue.hercules.sgi.csp.repository.custom;

import org.springframework.data.jpa.repository.Modifying;

public interface CustomConvocatoriaPeriodoJustificacionRepository {
  /**
   * Elimina todas los ConvocatoriaPeriodoJustificacion cuyo convocatoriaId
   * coincide con el indicado.
   * 
   * @param convocatoriaId el identificador de la Convocatoria cuyos periodos de
   *                       justificación se desean eliminar
   * @return el número de registros eliminados
   */
  @Modifying
  int deleteInBulkByConvocatoriaId(long convocatoriaId);
}
