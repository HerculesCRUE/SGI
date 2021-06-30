package org.crue.hercules.sgi.eti.service;

import java.util.List;

import org.crue.hercules.sgi.eti.exceptions.DictamenNotFoundException;
import org.crue.hercules.sgi.eti.model.Dictamen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link Dictamen}.
 */
public interface DictamenService {
  /**
   * Guardar {@link Dictamen}.
   *
   * @param dictamen la entidad {@link Dictamen} a guardar.
   * @return la entidad {@link Dictamen} persistida.
   */
  Dictamen create(Dictamen dictamen);

  /**
   * Actualizar {@link Dictamen}.
   *
   * @param dictamen la entidad {@link Dictamen} a actualizar.
   * @return la entidad {@link Dictamen} persistida.
   */
  Dictamen update(Dictamen dictamen);

  /**
   * Obtener todas las entidades {@link Dictamen} paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link Dictamen} paginadas y/o filtradas.
   */
  Page<Dictamen> findAll(String query, Pageable pageable);

  /**
   * Obtiene {@link Dictamen} por id.
   *
   * @param id el id de la entidad {@link Dictamen}.
   * @return la entidad {@link Dictamen}.
   */
  Dictamen findById(Long id);

  /**
   * Elimina el {@link Dictamen} por id.
   *
   * @param id el id de la entidad {@link Dictamen}.
   */
  void delete(Long id) throws DictamenNotFoundException;

  /**
   * Elimina todos los {@link Dictamen}.
   */
  void deleteAll();

  /**
   * Devuelve los dictamenes para los que el TipoEvaluación sea Memoria y el
   * TipoEstadoMemoria sea En secretaría revisión mínima
   * 
   * @return el listado de Dictámenes
   */
  List<Dictamen> findAllByMemoriaRevisionMinima();

  /**
   * Devuelve los dictamenes para los que el TipoEvaluación sea Memoria y el
   * TipoEstadoMemoria NO esté En secretaría revisión mínima
   * 
   * @return el listado de Dictámenes
   */
  List<Dictamen> findAllByMemoriaNoRevisionMinima();

  /**
   * Devuelve los dictamenes para los que el TipoEvaluación sea Retrospectiva
   * 
   * @return el listado de Dictámenes.
   */
  List<Dictamen> findAllByRetrospectiva();

}