package org.crue.hercules.sgi.eti.service;

import org.crue.hercules.sgi.eti.exceptions.FormacionEspecificaNotFoundException;
import org.crue.hercules.sgi.eti.model.FormacionEspecifica;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link FormacionEspecifica}.
 */
public interface FormacionEspecificaService {
  /**
   * Guardar {@link FormacionEspecifica}.
   *
   * @param FormacionEspecifica la entidad {@link FormacionEspecifica} a guardar.
   * @return la entidad {@link FormacionEspecifica} persistida.
   */
  FormacionEspecifica create(FormacionEspecifica FormacionEspecifica);

  /**
   * Actualizar {@link FormacionEspecifica}.
   *
   * @param FormacionEspecifica la entidad {@link FormacionEspecifica} a
   *                            actualizar.
   * @return la entidad {@link FormacionEspecifica} persistida.
   */
  FormacionEspecifica update(FormacionEspecifica FormacionEspecifica);

  /**
   * Obtener todas las entidades {@link FormacionEspecifica} paginadas y/o
   * filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link FormacionEspecifica} paginadas y/o
   *         filtradas.
   */
  Page<FormacionEspecifica> findAll(String query, Pageable pageable);

  /**
   * Obtiene {@link FormacionEspecifica} por id.
   *
   * @param id el id de la entidad {@link FormacionEspecifica}.
   * @return la entidad {@link FormacionEspecifica}.
   */
  FormacionEspecifica findById(Long id);

  /**
   * Elimina el {@link FormacionEspecifica} por id.
   *
   * @param id el id de la entidad {@link FormacionEspecifica}.
   */
  void delete(Long id) throws FormacionEspecificaNotFoundException;

  /**
   * Elimina todos los {@link FormacionEspecifica}.
   */
  void deleteAll();

}