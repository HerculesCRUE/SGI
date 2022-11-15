package org.crue.hercules.sgi.csp.repository.custom;

import java.util.List;

import org.crue.hercules.sgi.csp.dto.SeguimientoJustificacionAnualidad;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoJustificacionSeguimiento;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Component;

/**
 * Custom repository para {@link ProyectoPeriodoJustificacionSeguimiento}.
 */
@Component
public interface CustomProyectoPeriodoJustificacionSeguimientoRepository {

  /**
   * Obtiene todos los {@link SeguimientoJustificacionAnualidad}
   * relacionados con un ProyectoSGE
   * 
   * @param proyectoSgeRef identificador del ProyectoSGE
   * @return una lista de {@link SeguimientoJustificacionAnualidad}
   */
  List<SeguimientoJustificacionAnualidad> findSeguimientosJustificacionAnualidadByProyectoSgeRef(
      String proyectoSgeRef);

  /**
   * Elimina todos las entidades {@link ProyectoPeriodoJustificacionSeguimiento}
   * cuyo proyectoPeriodoJustificacionId coincide con el indicado.
   * 
   * @param proyectoPeriodoJustificacionId el identificador de la
   *                                       {@link ProyectoPeriodoJustificacion}.
   * @return el número de registros eliminados.
   */
  @Modifying
  int deleteInBulkByProyectoPeriodoJustificacionId(long proyectoPeriodoJustificacionId);
}
