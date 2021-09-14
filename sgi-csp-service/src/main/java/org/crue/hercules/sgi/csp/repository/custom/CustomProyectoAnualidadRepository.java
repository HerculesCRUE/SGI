package org.crue.hercules.sgi.csp.repository.custom;

import java.util.List;

import org.crue.hercules.sgi.csp.dto.AnualidadResumen;
import org.crue.hercules.sgi.csp.dto.ProyectoAnualidadResumen;
import org.crue.hercules.sgi.csp.model.AnualidadGasto;
import org.crue.hercules.sgi.csp.model.AnualidadIngreso;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoAnualidad;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

/**
 * Custom repository para {@link SolicitudProyectoSocio}.
 */
@Component
public interface CustomProyectoAnualidadRepository {

  /**
   * Recupera todas las {@link ProyectoAnualidad} del proyecto recibido,
   * calculando el total de de gastos presupuesto, gastos concedidos y total
   * ingresos por anualidad.
   * 
   * @param proyectoId Id de la {@link Proyecto}.
   * @param pageable   datos paginación.
   * @return Listado paginado de {@link ProyectoAnualidad}
   */
  Page<ProyectoAnualidadResumen> findAllResumenByProyectoId(Long proyectoId, Pageable pageable);

  /**
   * Recupera el resumen de {@link AnualidadGasto} y {@link AnualidadIngreso}
   * ({@link AnualidadResumen}) de una {@link ProyectoAnualidad}.
   * 
   * @param proyectoAnualidadId Identificador de {@link ProyectoAnualidad}.
   * @return Listado del resumen de {@link AnualidadResumen}.
   */
  List<AnualidadResumen> getPartidasResumen(Long proyectoAnualidadId);

}