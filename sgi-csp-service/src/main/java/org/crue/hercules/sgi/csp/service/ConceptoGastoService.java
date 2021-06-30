package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.ConceptoGasto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link ConceptoGasto}.
 */
public interface ConceptoGastoService {

  /**
   * Guardar un nuevo {@link ConceptoGasto}.
   *
   * @param conceptoGasto la entidad {@link ConceptoGasto} a guardar.
   * @return la entidad {@link ConceptoGasto} persistida.
   */
  ConceptoGasto create(ConceptoGasto conceptoGasto);

  /**
   * Actualizar {@link ConceptoGasto}.
   *
   * @param conceptoGastoActualizar la entidad {@link ConceptoGasto} a actualizar.
   * @return la entidad {@link ConceptoGasto} persistida.
   */
  ConceptoGasto update(ConceptoGasto conceptoGastoActualizar);

  /**
   * Reactiva el {@link ConceptoGasto}.
   *
   * @param id Id del {@link ConceptoGasto}.
   * @return la entidad {@link ConceptoGasto} persistida.
   */
  ConceptoGasto enable(Long id);

  /**
   * Desactiva el {@link ConceptoGasto}.
   *
   * @param id Id del {@link ConceptoGasto}.
   * @return la entidad {@link ConceptoGasto} persistida.
   */
  ConceptoGasto disable(Long id);

  /**
   * Obtener todas las entidades {@link ConceptoGasto} activos paginadas y/o
   * filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link ConceptoGasto} paginadas y/o filtradas.
   */
  Page<ConceptoGasto> findAll(String query, Pageable pageable);

  /**
   * Obtener todas las entidades {@link ConceptoGasto} paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link ConceptoGasto} paginadas y/o filtradas.
   */
  Page<ConceptoGasto> findAllTodos(String query, Pageable pageable);

  /**
   * Obtiene {@link ConceptoGasto} por su id.
   *
   * @param id el id de la entidad {@link ConceptoGasto}.
   * @return la entidad {@link ConceptoGasto}.
   */
  ConceptoGasto findById(Long id);

}
