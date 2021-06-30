package org.crue.hercules.sgi.csp.service;

import java.util.List;

import org.crue.hercules.sgi.csp.model.ProyectoSocio;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoJustificacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link ProyectoSocioPeriodoJustificacion}.
 */
public interface ProyectoSocioPeriodoJustificacionService {

  /**
   * Actualiza el listado de {@link ProyectoSocioPeriodoJustificacion} de la
   * {@link ProyectoSocio} con el listado
   * convocatoriaPeriodoJustificacioneseliminando los elementos segun proceda.
   *
   * @param proyectoSocioId                     Id de la {@link ProyectoSocio}.
   * @param proyectoSocioPeriodoJustificaciones lista con los nuevos
   *                                            {@link ProyectoSocioPeriodoJustificacion}
   *                                            a guardar.
   * @return la entidad {@link ProyectoSocioPeriodoJustificacion} persistida.
   */
  List<ProyectoSocioPeriodoJustificacion> delete(Long proyectoSocioId,
      List<ProyectoSocioPeriodoJustificacion> proyectoSocioPeriodoJustificaciones);

  /**
   * Actualiza {@link ProyectoSocioPeriodoJustificacion}.
   *
   * 
   * @param proyectoSocioPeriodoJustificacion {@link ProyectoSocioPeriodoJustificacion}
   *                                          a guardar.
   * @param id                                Identificador de
   *                                          {@link ProyectoSocioPeriodoJustificacion}
   *                                          a guardar.
   * @return la entidad {@link ProyectoSocioPeriodoJustificacion} persistida.
   */
  ProyectoSocioPeriodoJustificacion update(ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion,
      Long id);

  /**
   * Crea {@link ProyectoSocioPeriodoJustificacion}.
   *
   * 
   * @param proyectoSocioPeriodoJustificacion {@link ProyectoSocioPeriodoJustificacion}
   *                                          a guardar.
   * @return la entidad {@link ProyectoSocioPeriodoJustificacion} persistida.
   */
  ProyectoSocioPeriodoJustificacion create(ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion);

  /**
   * Obtiene {@link ProyectoSocioPeriodoJustificacion} por su id.
   *
   * @param id el id de la entidad {@link ProyectoSocioPeriodoJustificacion}.
   * @return la entidad {@link ProyectoSocioPeriodoJustificacion}.
   */
  ProyectoSocioPeriodoJustificacion findById(Long id);

  /**
   * Comprueba la existencia del {@link ProyectoSocioPeriodoJustificacion} por id.
   *
   * @param id el id de la entidad {@link ProyectoSocioPeriodoJustificacion}.
   * @return true si existe y false en caso contrario.
   */
  boolean existsById(Long id);

  /**
   * Obtiene las {@link ProyectoSocioPeriodoJustificacion} para una
   * {@link ProyectoSocio}.
   *
   * @param idProyectoSocio el id de la {@link ProyectoSocio}.
   * @param query           la información del filtro.
   * @param pageable        la información de la paginación.
   * @return la lista de entidades {@link ProyectoSocioPeriodoJustificacion} de la
   *         {@link ProyectoSocio} paginadas.
   */
  Page<ProyectoSocioPeriodoJustificacion> findAllByProyectoSocio(Long idProyectoSocio, String query, Pageable pageable);

}