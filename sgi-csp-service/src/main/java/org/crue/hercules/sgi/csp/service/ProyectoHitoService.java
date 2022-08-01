package org.crue.hercules.sgi.csp.service;

import java.util.List;

import org.crue.hercules.sgi.csp.dto.ProyectoHitoInput;
import org.crue.hercules.sgi.csp.dto.com.Recipient;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoHito;
import org.crue.hercules.sgi.csp.model.ProyectoHitoAviso;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link ProyectoHito}.
 */

public interface ProyectoHitoService {

  /**
   * Guarda la entidad {@link ProyectoHito}.
   * 
   * @param proyectoHitoInput la entidad {@link ProyectoHitoInput} a guardar.
   * @return ProyectoHito la entidad {@link ProyectoHito} persistida.
   */
  ProyectoHito create(ProyectoHitoInput proyectoHitoInput);

  /**
   * @param proyectoHitoId         id del {@link ProyectoHito}
   * @param proyectoHitoActualizar datos a actualizar
   *                               Actualiza la entidad {@link ProyectoHito}.
   * @return ProyectoHito la entidad {@link ProyectoHito} persistida.
   */
  ProyectoHito update(Long proyectoHitoId, ProyectoHitoInput proyectoHitoActualizar);

  /**
   * Elimina la {@link ProyectoHito}.
   *
   * @param id Id del {@link ProyectoHito}.
   */
  void delete(Long id);

  /**
   * Obtiene {@link ProyectoHito} por su id.
   *
   * @param id el id de la entidad {@link ProyectoHito}.
   * @return la entidad {@link ProyectoHito}.
   */
  ProyectoHito findById(Long id);

  /**
   * Obtiene las {@link ProyectoHito} para una {@link Proyecto}.
   *
   * @param proyectoId el id del {@link Proyecto}.
   * @param query      la información del filtro.
   * @param pageable   la información de la paginación.
   * @return la lista de entidades {@link ProyectoHito} de la {@link Proyecto}
   *         paginadas.
   */
  Page<ProyectoHito> findAllByProyecto(Long proyectoId, String query, Pageable pageable);

  /**
   * Comprueba si existen datos vinculados a {@link Proyecto} de
   * {@link ProyectoHito}
   *
   * @param proyectoId Id del {@link Proyecto}.
   * @return si existe o no el proyecto
   */
  boolean existsByProyecto(Long proyectoId);

  /**
   * Obtiene el listado de destinatarios adicionales a los que enviar el email
   * generado por un hito en base al {@link ProyectoHitoAviso} relacionadao
   * 
   * @param proyectoHitoId identificador de {@link ProyectoHito}
   * @return listado de {@link Recipient}
   */
  List<Recipient> getDeferredRecipients(Long proyectoHitoId);

}
