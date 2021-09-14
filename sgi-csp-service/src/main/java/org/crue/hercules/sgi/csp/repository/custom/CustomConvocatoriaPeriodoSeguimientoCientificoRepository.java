package org.crue.hercules.sgi.csp.repository.custom;

import org.springframework.data.jpa.repository.Modifying;

public interface CustomConvocatoriaPeriodoSeguimientoCientificoRepository {
  /**
   * Elimina todas los ConvocatoriaPeriodoSeguimientoCientifico cuyo
   * convocatoriaId coincide con el indicado.
   * 
   * @param convocatoriaId el identificador de la Convocatoria cuyos periodos de
   *                       seguimiento científico se desean eliminar
   * @return el número de registros eliminados
   */
  @Modifying
  int deleteInBulkByConvocatoriaId(long convocatoriaId);
}
