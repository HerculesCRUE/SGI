package org.crue.hercules.sgi.prc.repository.custom;

import org.crue.hercules.sgi.prc.model.Acreditacion;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.springframework.data.jpa.repository.Modifying;

public interface CustomAcreditacionRepository {
  /**
   * Elimina todos los {@link Acreditacion} cuyo produccionCientificaId
   * coincide con el indicado.
   * 
   * @param produccionCientificaId el identificador de la
   *                               {@link ProduccionCientifica}
   * @return el número de registros eliminados
   */
  @Modifying
  int deleteInBulkByProduccionCientificaId(long produccionCientificaId);
}
