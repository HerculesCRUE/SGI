package org.crue.hercules.sgi.csp.service;

import java.util.List;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoJustificacionDocumento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar
 * {@link ProyectoSocioPeriodoJustificacionDocumento}.
 */
public interface ProyectoSocioPeriodoJustificacionDocumentoService {

  /**
   * Actualiza el listado de {@link ProyectoSocioPeriodoJustificacionDocumento}
   * dela {@link ProyectoSocioPeriodoJustificacion} con el listado
   * pIdroyectoSocioPeriodoJustificaciones añadiendo, editando o eliminando los
   * elementos segun proceda.
   *
   * @param proyectoSocioPeriodoJustificacionId          Id de la
   *                                                     {@link ProyectoSocioPeriodoJustificacion}.
   * @param proyectoSocioPeriodoJustificacionDocumentoes lista con los nuevos
   *                                                     {@link ProyectoSocioPeriodoJustificacionDocumento}
   *                                                     a guardar.
   * @return la entidad {@link ProyectoSocioPeriodoJustificacionDocumento}
   *         persistida.
   */
  List<ProyectoSocioPeriodoJustificacionDocumento> update(Long proyectoSocioPeriodoJustificacionId,
      List<ProyectoSocioPeriodoJustificacionDocumento> proyectoSocioPeriodoJustificacionDocumentoes);

  /**
   * Obtiene {@link ProyectoSocioPeriodoJustificacionDocumento} por su id.
   *
   * @param id el id de la entidad
   *           {@link ProyectoSocioPeriodoJustificacionDocumento}.
   * @return la entidad {@link ProyectoSocioPeriodoJustificacionDocumento}.
   */
  ProyectoSocioPeriodoJustificacionDocumento findById(Long id);

  /**
   * Obtiene las {@link ProyectoSocioPeriodoJustificacionDocumento} para una
   * {@link ProyectoSocioPeriodoJustificacion}.
   *
   * @param idProyectoSocioPeriodoJustificacion el id de la
   *                                            {@link ProyectoSocioPeriodoJustificacion}.
   * @param query                               la información del filtro.
   * @param pageable                            la información de la paginación.
   * @return la lista de entidades
   *         {@link ProyectoSocioPeriodoJustificacionDocumento} de la
   *         {@link ProyectoSocioPeriodoJustificacion} paginadas.
   */
  Page<ProyectoSocioPeriodoJustificacionDocumento> findAllByProyectoSocioPeriodoJustificacion(
      Long idProyectoSocioPeriodoJustificacion, String query, Pageable pageable);

  /**
   * Obtiene las {@link ProyectoSocioPeriodoJustificacionDocumento} para una
   * {@link Proyecto}.
   *
   * @param idProyecto el id de la {@link Proyecto}.
   * @return la lista de entidades
   *         {@link ProyectoSocioPeriodoJustificacionDocumento} de la
   *         {@link Proyecto}.
   */
  List<ProyectoSocioPeriodoJustificacionDocumento> findAllByProyecto(Long idProyecto);

  /**
   * Comprueba si existen datos vinculados a {@link Proyecto} de
   * {@link ProyectoSocioPeriodoJustificacionDocumento}
   *
   * @param proyectoId Id del {@link Proyecto}.
   * @return true si existe y false en caso contrario.
   */
  boolean existsByProyecto(Long proyectoId);
}