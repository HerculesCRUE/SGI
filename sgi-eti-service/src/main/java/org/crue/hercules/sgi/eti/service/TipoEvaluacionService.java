package org.crue.hercules.sgi.eti.service;

import java.util.List;

import org.crue.hercules.sgi.eti.exceptions.TipoEvaluacionNotFoundException;
import org.crue.hercules.sgi.eti.model.Dictamen;
import org.crue.hercules.sgi.eti.model.TipoEvaluacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link TipoEvaluacion}.
 */
public interface TipoEvaluacionService {
  /**
   * Guardar {@link TipoEvaluacion}.
   *
   * @param tipoEvaluacion la entidad {@link TipoEvaluacion} a guardar.
   * @return la entidad {@link TipoEvaluacion} persistida.
   */
  TipoEvaluacion create(TipoEvaluacion tipoEvaluacion);

  /**
   * Actualizar {@link TipoEvaluacion}.
   *
   * @param tipoEvaluacion la entidad {@link TipoEvaluacion} a actualizar.
   * @return la entidad {@link TipoEvaluacion} persistida.
   */
  TipoEvaluacion update(TipoEvaluacion tipoEvaluacion);

  /**
   * Obtener todas las entidades {@link TipoEvaluacion} paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link TipoEvaluacion} paginadas y/o filtradas.
   */
  Page<TipoEvaluacion> findAll(String query, Pageable pageable);

  /**
   * Obtiene {@link TipoEvaluacion} por id.
   *
   * @param id el id de la entidad {@link TipoEvaluacion}.
   * @return la entidad {@link TipoEvaluacion}.
   */
  TipoEvaluacion findById(Long id);

  /**
   * Elimina el {@link TipoEvaluacion} por id.
   *
   * @param id el id de la entidad {@link TipoEvaluacion}.
   */
  void delete(Long id) throws TipoEvaluacionNotFoundException;

  /**
   * Elimina todos los {@link TipoEvaluacion}.
   */
  void deleteAll();

  /**
   * Devuelve el listado de dictamenes dependiendo del tipo de Evaluación y si es
   * Revisión mínima
   * 
   * @param esRevisionMinima Boolean para saber si la evaluación es revisión
   *                         mínima
   * @param idTipoEvaluacion Identificador {@link TipoEvaluacion}.
   * @return listado de dictamenes.
   */
  List<Dictamen> findAllDictamenByTipoEvaluacionAndRevisionMinima(Long idTipoEvaluacion, Boolean esRevisionMinima);

  /**
   * Devuelve el listado de tipo de evaluaciones: Memoria y Retrospectiva.
   * 
   * @return listado de tipo de evaluaciones.
   */
  List<TipoEvaluacion> findTipoEvaluacionMemoriaRetrospectiva();

  /**
   * Devuelve el listado de tipo de evaluaciones: Seguimiento Anual y Seguimiento
   * Final.
   * 
   * @return listado de tipo de evaluaciones.
   */
  List<TipoEvaluacion> findTipoEvaluacionSeguimientoAnualFinal();
}