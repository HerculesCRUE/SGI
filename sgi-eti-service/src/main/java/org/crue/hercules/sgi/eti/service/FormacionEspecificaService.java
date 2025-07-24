package org.crue.hercules.sgi.eti.service;

import org.crue.hercules.sgi.eti.model.FormacionEspecifica;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link FormacionEspecifica}.
 */
public interface FormacionEspecificaService {
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
}