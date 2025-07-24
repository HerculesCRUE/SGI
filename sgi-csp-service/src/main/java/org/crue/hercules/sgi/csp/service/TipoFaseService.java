package org.crue.hercules.sgi.csp.service;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.BaseEntity;
import org.crue.hercules.sgi.csp.model.TipoFase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;

/**
 * Service Interface para gestionar {@link TipoFase}.
 */
public interface TipoFaseService {

  /**
   * Guardar {@link TipoFase}.
   *
   * @param tipoFase la entidad {@link TipoFase} a guardar.
   * @return la entidad {@link TipoFase} persistida.
   */
  @Validated({ BaseEntity.Create.class })
  TipoFase create(@Valid TipoFase tipoFase);

  /**
   * Actualizar {@link TipoFase}.
   *
   * @param tipoFase la entidad {@link TipoFase} a actualizar.
   * @return la entidad {@link TipoFase} persistida.
   */
  @Validated({ BaseEntity.Update.class })
  TipoFase update(@Valid TipoFase tipoFase);

  /**
   * Obtener todas las entidades {@link TipoFase} activas paginadas y/o filtradas.
   * Obtener todas las entidades {@link TipoFase} paginadas y/o filtradas
   *
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link TipoFase} paginadas y/o filtradas.
   */
  Page<TipoFase> findAll(String query, Pageable pageable);

  /**
   * Obtener todas las entidades {@link TipoFase} paginadas y/o filtradas. Obtener
   * todas las entidades {@link TipoFase} paginadas y/o filtradas
   *
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link TipoFase} paginadas y/o filtradas.
   */
  Page<TipoFase> findAllTodos(String query, Pageable pageable);

  /**
   * Reactiva el {@link TipoFase}.
   *
   * @param id Id del {@link TipoFase}.
   * @return la entidad {@link TipoFase} persistida.
   */
  TipoFase enable(Long id);

  /**
   * Desactiva el {@link TipoFase}.
   *
   * @param id Id del {@link TipoFase}.
   * @return la entidad {@link TipoFase} persistida.
   */
  TipoFase disable(Long id);

}
