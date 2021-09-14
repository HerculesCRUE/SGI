package org.crue.hercules.sgi.csp.repository.custom;

import org.springframework.data.jpa.repository.Modifying;

public interface CustomSolicitudProyectoSocioPeriodoJustificacionRepository {
  /**
   * Elimina todas los SolicitudProyectoSocioPeriodoJustificacion cuyo
   * solicitudProyectoSocioId coincide con el indicado.
   * 
   * @param solicitudProyectoSocioId el identificador de la Convocatoria cuyos
   *                                 periodos de justificación se desean eliminar
   * @return el número de registros eliminados
   */
  @Modifying
  int deleteInBulkBySolicitudProyectoSocioId(long solicitudProyectoSocioId);
}
