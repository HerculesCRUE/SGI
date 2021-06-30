package org.crue.hercules.sgi.eti.service;

import org.crue.hercules.sgi.eti.exceptions.FormularioNotFoundException;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link Formulario}.
 */
public interface FormularioService {
  /**
   * Guardar {@link Formulario}.
   *
   * @param Formulario la entidad {@link Formulario} a guardar.
   * @return la entidad {@link Formulario} persistida.
   */
  Formulario create(Formulario Formulario);

  /**
   * Actualizar {@link Formulario}.
   *
   * @param Formulario la entidad {@link Formulario} a actualizar.
   * @return la entidad {@link Formulario} persistida.
   */
  Formulario update(Formulario Formulario);

  /**
   * Obtener todas las entidades {@link Formulario} paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link Formulario} paginadas y/o filtradas.
   */
  Page<Formulario> findAll(String query, Pageable pageable);

  /**
   * Obtiene {@link Formulario} por id.
   *
   * @param id el id de la entidad {@link Formulario}.
   * @return la entidad {@link Formulario}.
   */
  Formulario findById(Long id);

  /**
   * Elimina el {@link Formulario} por id.
   *
   * @param id el id de la entidad {@link Formulario}.
   */
  void delete(Long id) throws FormularioNotFoundException;

  /**
   * Elimina todos los {@link Formulario}.
   */
  void deleteAll();

}