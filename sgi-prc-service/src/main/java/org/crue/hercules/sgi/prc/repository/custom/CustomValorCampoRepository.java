package org.crue.hercules.sgi.prc.repository.custom;

import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica;
import org.crue.hercules.sgi.prc.model.ValorCampo;
import org.springframework.data.jpa.repository.Modifying;

public interface CustomValorCampoRepository {
  /**
   * Elimina todos los {@link ValorCampo} cuyo campoProduccionCientificaId
   * coincide con
   * el indicado.
   * 
   * @param campoProduccionCientificaId el identificador de la
   *                                    {@link CampoProduccionCientifica}
   * @return el número de registros eliminados
   */
  @Modifying
  int deleteInBulkByCampoProduccionCientificaId(long campoProduccionCientificaId);
}
