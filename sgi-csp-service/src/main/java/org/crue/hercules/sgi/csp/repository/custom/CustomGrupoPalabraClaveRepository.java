package org.crue.hercules.sgi.csp.repository.custom;

import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.GrupoPalabraClave;
import org.springframework.data.jpa.repository.Modifying;

public interface CustomGrupoPalabraClaveRepository {

  /**
   * Elimina todos los {@link GrupoPalabraClave} cuyo grupoId
   * coincide con el indicado.
   * 
   * @param grupoId el identificador del {@link Grupo} cuyas palabras claves se
   *                desean eliminar
   * @return el número de registros eliminados
   */
  @Modifying
  int deleteInBulkByGrupoId(long grupoId);

}
