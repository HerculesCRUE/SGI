package org.crue.hercules.sgi.pii.repository.custom;

import java.time.Instant;

import org.crue.hercules.sgi.pii.model.Invencion;
import org.crue.hercules.sgi.pii.model.PeriodoTitularidadTitular;

public interface CustomPeriodoTitularidadTitularRepository {

  /**
   * Devuelve una lista de {@link PeriodoTitularidadTitular} que pertenecen a la
   * universidad a fecha 31 de diciembre de una determinada {@link Invencion}
   * 
   * @param invencionId     id de {@link Invencion}
   * @param fechaBaremacion fecha de baremación a 31 de diciembre
   * @param universidadId   id universidad
   * 
   * @return Lista de {@link PeriodoTitularidadTitular}
   */
  PeriodoTitularidadTitular findPeriodoTitularidadTitularesInFechaBaremacion(Long invencionId,
      Instant fechaBaremacion, String universidadId);

}
