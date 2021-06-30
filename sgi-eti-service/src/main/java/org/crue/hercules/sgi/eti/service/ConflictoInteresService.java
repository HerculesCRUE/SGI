package org.crue.hercules.sgi.eti.service;

import org.crue.hercules.sgi.eti.exceptions.ConflictoInteresNotFoundException;
import org.crue.hercules.sgi.eti.model.ConflictoInteres;
import org.crue.hercules.sgi.eti.model.Evaluador;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link ConflictoInteres}.
 */
public interface ConflictoInteresService {
  /**
   * Guardar {@link ConflictoInteres}.
   *
   * @param conflictoInteres la entidad {@link ConflictoInteres} a guardar.
   * @return la entidad {@link ConflictoInteres} persistida.
   */
  ConflictoInteres create(ConflictoInteres conflictoInteres);

  /**
   * Actualizar {@link ConflictoInteres}.
   *
   * @param conflictoInteres la entidad {@link ConflictoInteres} a actualizar.
   * @return la entidad {@link ConflictoInteres} persistida.
   */
  ConflictoInteres update(ConflictoInteres conflictoInteres);

  /**
   * Obtener todas las entidades {@link ConflictoInteres} paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link ConflictoInteres} paginadas y/o
   *         filtradas.
   */
  Page<ConflictoInteres> findAll(String query, Pageable pageable);

  /**
   * Obtiene {@link ConflictoInteres} por id.
   *
   * @param id el id de la entidad {@link ConflictoInteres}.
   * @return la entidad {@link ConflictoInteres}.
   */
  ConflictoInteres findById(Long id);

  /**
   * Elimina el {@link ConflictoInteres} por id.
   *
   * @param id el id de la entidad {@link ConflictoInteres}.
   */
  void delete(Long id) throws ConflictoInteresNotFoundException;

  /**
   * Elimina todos los {@link ConflictoInteres}.
   */
  void deleteAll();

  /**
   * Obtiene una lista {@link ConflictoInteres} paginada filtrada por el
   * evaluador.
   *
   * @param id       el id de la entidad {@link Evaluador}.
   * @param pageable datos paginación
   * @return la lista paginada de {@link ConflictoInteres}.
   */
  Page<ConflictoInteres> findAllByEvaluadorId(Long id, Pageable pageable);

}