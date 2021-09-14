package org.crue.hercules.sgi.csp.repository.custom;

import org.springframework.data.jpa.repository.Modifying;

public interface CustomRequisitoEquipoNivelAcademicoRepository {
  /**
   * Elimina todos los RequisitoEquipoNivelAcademico cuyo requisitoIPId coincide
   * con el indicado.
   * 
   * @param requisitoIPId el identificador del RequisitoEquipo cuyos niveles se
   *                      desean eliminar
   * @return el número de registros eliminados
   */
  @Modifying
  int deleteInBulkByRequisitoEquipoId(long requisitoIPId);
}
