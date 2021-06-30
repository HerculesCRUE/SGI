package org.crue.hercules.sgi.eti.service;

import org.crue.hercules.sgi.eti.exceptions.EstadoRetrospectivaNotFoundException;
import org.crue.hercules.sgi.eti.model.EstadoRetrospectiva;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link EstadoRetrospectiva}.
 */
public interface EstadoRetrospectivaService {

  /**
   * Crea {@link EstadoRetrospectiva}.
   *
   * @param estadoRetrospectiva La entidad {@link EstadoRetrospectiva} a crear.
   * @return La entidad {@link EstadoRetrospectiva} creada.
   * @throws IllegalArgumentException Si la entidad {@link EstadoRetrospectiva}
   *                                  tiene id.
   */
  EstadoRetrospectiva create(EstadoRetrospectiva estadoRetrospectiva) throws IllegalArgumentException;

  /**
   * Actualiza {@link EstadoRetrospectiva}.
   *
   * @param estadoRetrospectivaActualizar La entidad {@link EstadoRetrospectiva} a
   *                                      actualizar.
   * @return La entidad {@link EstadoRetrospectiva} actualizada.
   * @throws EstadoRetrospectivaNotFoundException Si no existe ninguna entidad
   *                                              {@link EstadoRetrospectiva} con
   *                                              ese id.
   * @throws IllegalArgumentException             Si la entidad
   *                                              {@link EstadoRetrospectiva} no
   *                                              tiene id.
   */
  EstadoRetrospectiva update(EstadoRetrospectiva estadoRetrospectivaActualizar)
      throws EstadoRetrospectivaNotFoundException, IllegalArgumentException;

  /**
   * Elimina todas las entidades {@link EstadoRetrospectiva}.
   *
   */
  void deleteAll();

  /**
   * Elimina {@link EstadoRetrospectiva} por id.
   *
   * @param id El id de la entidad {@link EstadoRetrospectiva}.
   * @throws EstadoRetrospectivaNotFoundException Si no existe ninguna entidad
   *                                              {@link EstadoRetrospectiva} con
   *                                              ese id.
   * @throws IllegalArgumentException             Si no se informa Id.
   */
  void delete(Long id) throws EstadoRetrospectivaNotFoundException, IllegalArgumentException;

  /**
   * Obtiene las entidades {@link EstadoRetrospectiva} filtradas y paginadas según
   * los criterios de búsqueda.
   *
   * @param query  filtro de búsqueda.
   * @param paging pageable
   * @return el listado de entidades {@link EstadoRetrospectiva} paginadas y
   *         filtradas.
   */
  Page<EstadoRetrospectiva> findAll(String query, Pageable paging);

  /**
   * Obtiene {@link EstadoRetrospectiva} por id.
   *
   * @param id El id de la entidad {@link EstadoRetrospectiva}.
   * @return La entidad {@link EstadoRetrospectiva}.
   * @throws EstadoRetrospectivaNotFoundException Si no existe ninguna entidad
   *                                              {@link EstadoRetrospectiva} con
   *                                              ese id.
   * @throws IllegalArgumentException             Si no se informa Id.
   */
  EstadoRetrospectiva findById(Long id) throws EstadoRetrospectivaNotFoundException, IllegalArgumentException;

}
