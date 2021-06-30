package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoEntidadFinanciadora;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link ProyectoEntidadFinanciadoraService}.
 */
public interface ProyectoEntidadFinanciadoraService {

  /**
   * Guardar un nuevo {@link ProyectoEntidadFinanciadora}.
   *
   * @param proyectoEntidadFinanciadora la entidad
   *                                    {@link ProyectoEntidadFinanciadora} a
   *                                    guardar.
   * @return la entidad {@link ProyectoEntidadFinanciadora} persistida.
   */
  ProyectoEntidadFinanciadora create(ProyectoEntidadFinanciadora proyectoEntidadFinanciadora);

  /**
   * Actualizar {@link ProyectoEntidadFinanciadora}.
   *
   * @param proyectoEntidadFinanciadora la entidad
   *                                    {@link ProyectoEntidadFinanciadora} a
   *                                    actualizar.
   * @return la entidad {@link ProyectoEntidadFinanciadora} persistida.
   */
  ProyectoEntidadFinanciadora update(ProyectoEntidadFinanciadora proyectoEntidadFinanciadora);

  /**
   * Elimina {@link ProyectoEntidadFinanciadora}.
   *
   * @param id Id de {@link ProyectoEntidadFinanciadora}.
   */
  void delete(Long id);

  /**
   * Obtiene {@link ProyectoEntidadFinanciadora} por su id.
   *
   * @param id el id de la entidad {@link ProyectoEntidadFinanciadora}.
   * @return la entidad {@link ProyectoEntidadFinanciadora}.
   */
  ProyectoEntidadFinanciadora findById(Long id);

  /**
   * Obtiene las {@link ProyectoEntidadFinanciadora} para un {@link Proyecto}.
   *
   * @param idProyecto el id de la {@link Proyecto}.
   * @param query      la información del filtro.
   * @param pageable   la información de la paginación.
   * @return la lista de entidades {@link ProyectoEntidadFinanciadora} de un
   *         {@link Proyecto} paginadas.
   */
  Page<ProyectoEntidadFinanciadora> findAllByProyecto(Long idProyecto, String query, Pageable pageable);
}