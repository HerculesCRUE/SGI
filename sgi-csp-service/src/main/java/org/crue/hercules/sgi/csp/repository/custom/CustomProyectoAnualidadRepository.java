package org.crue.hercules.sgi.csp.repository.custom;

import java.math.BigDecimal;
import java.util.List;

import org.crue.hercules.sgi.csp.dto.AnualidadResumen;
import org.crue.hercules.sgi.csp.dto.ProyectoAnualidadNotificacionSge;
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

  /**
   * Recupera los {@link ProyectoAnualidadNotificacionSge} que cumplan las
   * condiciones de búsqueda y tengan a true el indicador presupuestar.
   * 
   * @param query           filtro de búsqueda.
   * @param unidadesGestion unidades de gestión.
   * @return Listado de {@link ProyectoAnualidadNotificacionSge}.
   */
  List<ProyectoAnualidadNotificacionSge> findAllNotificacionSge(String query, List<String> unidadesGestion);

  /**
   * Obtiene la suma de importe concedido de cada {@link AnualidadGasto}
   * asociados a un {@link Proyecto}
   * cuyo id coincide con el indicado.
   * 
   * @param proyectoId el identificador del {@link Proyecto}
   * @return suma de puntos del campo importeConcedido
   */
  BigDecimal getTotalImporteConcedidoAnualidadGasto(Long proyectoId);

  /**
   * Obtiene la suma de importe concedido de cada {@link AnualidadGasto} de costes
   * indirectos
   * asociados a un {@link Proyecto} cuyo id coincide con el indicado.
   * 
   * @param proyectoId el identificador del {@link Proyecto}
   * @return suma de puntos del campo importeConcedido
   */
  BigDecimal getTotalImporteConcedidoAnualidadGastoCostesIndirectos(Long proyectoId);
}
