package org.crue.hercules.sgi.eti.service;

import org.crue.hercules.sgi.eti.model.Bloque;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link Bloque}.
 */
public interface BloqueService {

  /**
   * Obtener todas las entidades {@link Bloque} paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link Bloque} paginadas y/o filtradas.
   */
  Page<Bloque> findAll(String query, Pageable pageable);

  /**
   * Obtiene {@link Bloque} por id.
   *
   * @param id el id de la entidad {@link Bloque}.
   * @return la entidad {@link Bloque}.
   */
  Bloque findById(Long id);

  /**
   * Obtener todas las entidades {@link Bloque} paginadas de una
   * {@link Formulario}.
   * 
   * @param id       Id del formulario
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link Bloque} paginadas y/o filtradas.
   */
  Page<Bloque> findByFormularioId(Long id, Pageable pageable);

}