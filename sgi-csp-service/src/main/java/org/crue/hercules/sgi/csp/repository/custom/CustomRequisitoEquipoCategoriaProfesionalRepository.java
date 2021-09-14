package org.crue.hercules.sgi.csp.repository.custom;

import org.springframework.data.jpa.repository.Modifying;

public interface CustomRequisitoEquipoCategoriaProfesionalRepository {
  /**
   * Elimina todas las RequisitoEquipoCategoriaProfesional cuyo requisitoEquipoId
   * coincide con el indicado.
   * 
   * @param requisitoEquipoId el identificador del RequisitoEquipo cuyas
   *                          categoraías se desean eliminar
   * @return el número de registros eliminados
   */
  @Modifying
  int deleteInBulkByRequisitoEquipoId(long requisitoEquipoId);
}
