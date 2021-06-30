package org.crue.hercules.sgi.csp.service;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.ProyectoSocio;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoPago;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Servicio interfaz para la gestión de {@link ProyectoSocioPeriodoPago}.
 */
public interface ProyectoSocioPeriodoPagoService {

  /**
   * Obtiene una entidad {@link ProyectoSocioPeriodoPago} por id.
   * 
   * @param id Identificador de la entidad {@link ProyectoSocioPeriodoPago}.
   * @return ProyectoSocioPeriodoPago la entidad {@link ProyectoSocioPeriodoPago}.
   */
  ProyectoSocioPeriodoPago findById(final Long id);

  /**
   * Recupera la lista paginada de socios colaborativos de un
   * {@link ProyectoSocio}.
   * 
   * @param idProyectoSocio Identificador del {@link ProyectoSocio}.
   * @param query           parámentros de búsqueda.
   * @param paging          parámetros de paginación.
   * @return lista paginada.
   */
  Page<ProyectoSocioPeriodoPago> findAllByProyectoSocio(Long idProyectoSocio, String query, Pageable paging);

  /**
   * Actualiza el listado de {@link ProyectoSocioPeriodoPago} del
   * {@link ProyectoSocio} con el listado proyectoSocioPeriodoPagos añadiendo,
   * editando o eliminando los elementos segun proceda.
   *
   * @param proyectoSocioId           Id de {@link ProyectoSocio}.
   * @param proyectoSocioPeriodoPagos lista con los nuevos
   *                                  {@link ProyectoSocioPeriodoPago} a guardar.
   * @return la entidad {@link ProyectoSocioPeriodoPago} persistida.
   */
  List<ProyectoSocioPeriodoPago> update(Long proyectoSocioId,
      @Valid List<ProyectoSocioPeriodoPago> proyectoSocioPeriodoPagos);

}
