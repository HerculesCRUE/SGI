package org.crue.hercules.sgi.prc.repository.custom;

import java.util.List;

import org.crue.hercules.sgi.prc.enums.EpigrafeCVN;
import org.crue.hercules.sgi.prc.model.ConvocatoriaBaremacion;

public interface CustomBaremoRepository {

  /**
   * Retorna una lista de {@link EpigrafeCVN} cuyo tipo de fuente es CVN o
   * CVN_OTRO_SISTEMA de la convocatoria de baremación del último año
   * 
   * @param convocatoriaBaremacionId Id de {@link ConvocatoriaBaremacion} del
   *                                 último año
   * @return Lista de {@link EpigrafeCVN}
   */
  List<EpigrafeCVN> findDistinctEpigrafesCVNByConvocatoriaBaremacionId(Long convocatoriaBaremacionId);

}
