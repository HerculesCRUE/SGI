package org.crue.hercules.sgi.prc.service;

import org.crue.hercules.sgi.prc.dto.BaremacionInput;

public interface BaremacionItemService {

  /**
   * Evalua la produccion científica de un determinado tipo y año.
   * 
   * @param baremacionInput {@link BaremacionInput}
   */
  void evaluateProduccionCientificaByTypeAndAnio(BaremacionInput baremacionInput);
}
