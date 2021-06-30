package org.crue.hercules.sgi.eti.service;

import org.crue.hercules.sgi.eti.dto.PeticionEvaluacionWithIsEliminable;
import org.crue.hercules.sgi.eti.exceptions.PeticionEvaluacionNotFoundException;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link PeticionEvaluacion}.
 */
public interface PeticionEvaluacionService {
  /**
   * Guardar {@link PeticionEvaluacion}.
   *
   * @param peticionEvaluacion la entidad {@link PeticionEvaluacion} a guardar.
   * @return la entidad {@link PeticionEvaluacion} persistida.
   */
  PeticionEvaluacion create(PeticionEvaluacion peticionEvaluacion);

  /**
   * Actualizar {@link PeticionEvaluacion}.
   *
   * @param peticionEvaluacion la entidad {@link PeticionEvaluacion} a actualizar.
   * @return la entidad {@link PeticionEvaluacion} persistida.
   */
  PeticionEvaluacion update(PeticionEvaluacion peticionEvaluacion);

  /**
   * Obtener todas las entidades {@link PeticionEvaluacion} paginadas y/o
   * filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link PeticionEvaluacion} paginadas y/o
   *         filtradas.
   */
  Page<PeticionEvaluacion> findAll(String query, Pageable pageable);

  /**
   * Obtiene {@link PeticionEvaluacion} por id.
   *
   * @param id el id de la entidad {@link PeticionEvaluacion}.
   * @return la entidad {@link PeticionEvaluacion}.
   */
  PeticionEvaluacion findById(Long id);

  /**
   * Elimina el {@link PeticionEvaluacion} por id.
   *
   * @param id el id de la entidad {@link PeticionEvaluacion}.
   */
  void delete(Long id) throws PeticionEvaluacionNotFoundException;

  /**
   * Elimina todos los {@link PeticionEvaluacion}.
   */
  void deleteAll();

  /**
   * Obtener todas las entidades {@link PeticionEvaluacion} paginadas y/o
   * filtradas por la persona.
   *
   * @param pageable   la información de la paginación.
   * @param query      la información del filtro.
   * @param personaRef Referencia de la persona
   * @return la lista de entidades {@link PeticionEvaluacion} paginadas y/o
   *         filtradas.
   */
  Page<PeticionEvaluacion> findAllByPersonaRef(String query, Pageable pageable, String personaRef);

  /**
   * Obtiene una lista paginada y filtrada
   * {@link PeticionEvaluacionWithIsEliminable} de una persona responsable de
   * memorias o creador de peticiones de evaluacion
   * 
   * @param query      Criterios de búsqueda
   * @param pageable   datos paginación
   * @param personaRef usuario
   * @return las entidades {@link PeticionEvaluacionWithIsEliminable}
   */
  Page<PeticionEvaluacionWithIsEliminable> findAllPeticionesWithPersonaRefCreadorPeticionesEvaluacionOrResponsableMemoria(
      String query, Pageable pageable, String personaRef);

}