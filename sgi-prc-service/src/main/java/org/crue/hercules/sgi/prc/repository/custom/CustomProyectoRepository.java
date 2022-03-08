package org.crue.hercules.sgi.prc.repository.custom;

import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.crue.hercules.sgi.prc.model.Proyecto;
import org.springframework.data.jpa.repository.Modifying;

public interface CustomProyectoRepository {
  /**
   * Elimina todos los {@link Proyecto} cuyo produccionCientificaId
   * coincide con el indicado.
   * 
   * @param produccionCientificaId el identificador de la
   *                               {@link ProduccionCientifica}
   * @return el número de registros eliminados
   */
  @Modifying
  int deleteInBulkByProduccionCientificaId(long produccionCientificaId);
}
