package org.crue.hercules.sgi.csp.service;

import java.util.List;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoSocio;
import org.crue.hercules.sgi.csp.model.ProyectoSocioEquipo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link ProyectoSocioEquipo}.
 */

public interface ProyectoSocioEquipoService {

  /**
   * Actualiza los datos del {@link ProyectoSocioEquipo}.
   * 
   * @param proyectoSocioId             Id de la {@link ProyectoSocio}.
   * @param proyectoSocioEquipoServices lista con los nuevos
   *                                    {@link ProyectoSocioEquipo} a guardar.
   * @return ProyectoSocioEquipoService {@link ProyectoSocioEquipo} actualizado.
   */
  List<ProyectoSocioEquipo> update(Long proyectoSocioId, List<ProyectoSocioEquipo> proyectoSocioEquipoServices);

  /**
   * Obtiene una entidad {@link ProyectoSocioEquipo} por id.
   * 
   * @param id Identificador de la entidad {@link ProyectoSocioEquipo}.
   * @return ProyectoSocioEquipo la entidad {@link ProyectoSocioEquipo}.
   */
  ProyectoSocioEquipo findById(final Long id);

  /**
   * Recupera la lista paginada de equipos socio de una {@link ProyectoSocio}.
   * 
   * @param idProyectoSocio Identificador de la {@link ProyectoSocio}.
   * @param query           parámentros de búsqueda.
   * @param paging          parámetros de paginación.
   * @return lista paginada.
   */
  Page<ProyectoSocioEquipo> findAllByProyectoSocio(Long idProyectoSocio, String query, Pageable paging);

  /**
   * Recupera la lista de miembros del equipo del socio de una
   * {@link ProyectoSocio}.
   * 
   * @param proyectoSocioId Identificador de la {@link ProyectoSocio}.
   * @return lista de {@link ProyectoSocioEquipo}.
   */
  List<ProyectoSocioEquipo> findAllByProyectoSocio(Long proyectoSocioId);

  /**
   * Comprueba si alguno de los {@link ProyectoSocioEquipo} del {@link Proyecto}
   * tienen fechas
   * 
   * @param proyectoId el id del {@link Proyecto}.
   * @return true si existen y false en caso contrario.
   */
  boolean proyectoHasProyectoSocioEquipoWithDates(Long proyectoId);

}
