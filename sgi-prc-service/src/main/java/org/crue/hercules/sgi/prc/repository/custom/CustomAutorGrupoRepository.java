package org.crue.hercules.sgi.prc.repository.custom;

import org.crue.hercules.sgi.prc.model.Autor;
import org.crue.hercules.sgi.prc.model.AutorGrupo;
import org.springframework.data.jpa.repository.Modifying;

public interface CustomAutorGrupoRepository {
  /**
   * Elimina todos los {@link AutorGrupo} cuyo autorID coincide con
   * el indicado.
   * 
   * @param autorId el identificador de la {@link Autor}
   * @return el número de registros eliminados
   */
  @Modifying
  int deleteInBulkByAutorId(long autorId);
}
