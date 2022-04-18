package org.crue.hercules.sgi.pii.repository.custom;

import java.time.Instant;
import java.util.List;

import org.crue.hercules.sgi.pii.dto.InvencionDto.SolicitudProteccionDto;
import org.crue.hercules.sgi.pii.model.Invencion;

public interface CustomSolicitudProteccionRepository {

  /**
   * Devuelve una lista de {@link SolicitudProteccionDto} que están en el rango de
   * baremación
   * 
   * @param invencionId           id de {@link Invencion}
   * @param fechaInicioBaremacion fecha inicio de baremación
   * @param fechaFinBaremacion    fecha fin de baremación
   * 
   * @return Lista de {@link SolicitudProteccionDto}
   */
  List<SolicitudProteccionDto> findSolicitudProteccionInRangoBaremacion(Long invencionId,
      Instant fechaInicioBaremacion, Instant fechaFinBaremacion);

}
