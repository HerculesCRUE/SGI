package org.crue.hercules.sgi.csp.repository.custom;

import org.springframework.data.jpa.repository.Modifying;

public interface CustomRequisitoIPNivelAcademicoRepository {
  /**
   * Elimina todos los RequisitoIPNivelAcademico cuyo requisitoIPId coincide con
   * el indicado.
   * 
   * @param requisitoIPId el identificador del RequisitoIP cuyos niveles se desean
   *                      eliminar
   * @return el número de registros eliminados
   */
  @Modifying
  int deleteInBulkByRequisitoIPId(long requisitoIPId);
}
