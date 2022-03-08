package org.crue.hercules.sgi.prc.repository.custom;

import org.crue.hercules.sgi.prc.model.Baremo;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.crue.hercules.sgi.prc.model.PuntuacionBaremoItem;
import org.springframework.data.jpa.repository.Modifying;

public interface CustomPuntuacionBaremoItemRepository {
  /**
   * Elimina todos los {@link PuntuacionBaremoItem} cuyo produccionCientificaId
   * coincide con el indicado.
   * 
   * @param produccionCientificaId el identificador de la
   *                               {@link ProduccionCientifica}
   * @return el número de registros eliminados
   */
  @Modifying
  int deleteInBulkByProduccionCientificaId(long produccionCientificaId);

  /**
   * Elimina todos los {@link PuntuacionBaremoItem} cuyo produccionCientificaId y
   * baremoId coincide con el indicado.
   * 
   * @param produccionCientificaId el identificador de la
   *                               {@link ProduccionCientifica}
   * @param baremoId               el identificador del {@link Baremo}
   * @return el número de registros eliminados
   */
  @Modifying
  int deleteInBulkByProduccionCientificaIdAndBaremoId(Long produccionCientificaId, Long baremoId);
}
