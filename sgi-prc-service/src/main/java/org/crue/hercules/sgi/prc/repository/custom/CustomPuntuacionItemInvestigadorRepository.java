package org.crue.hercules.sgi.prc.repository.custom;

import org.crue.hercules.sgi.prc.model.PuntuacionItemInvestigador;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.springframework.data.jpa.repository.Modifying;

public interface CustomPuntuacionItemInvestigadorRepository {
  /**
   * Elimina todos los {@link PuntuacionItemInvestigador} cuyo
   * produccionCientificaId
   * coincide con el indicado.
   * 
   * @param produccionCientificaId el identificador de la
   *                               {@link ProduccionCientifica}
   * @return el número de registros eliminados
   */
  @Modifying
  int deleteInBulkByProduccionCientificaId(long produccionCientificaId);
}
