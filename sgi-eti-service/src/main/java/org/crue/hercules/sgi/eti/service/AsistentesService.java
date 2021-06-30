package org.crue.hercules.sgi.eti.service;

import org.crue.hercules.sgi.eti.exceptions.AsistentesNotFoundException;
import org.crue.hercules.sgi.eti.model.Asistentes;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link Asistentes}.
 */
public interface AsistentesService {
  /**
   * Guardar {@link Asistentes}.
   *
   * @param asistentes la entidad {@link Asistentes} a guardar.
   * @return la entidad {@link Asistentes} persistida.
   */
  Asistentes create(Asistentes asistentes);

  /**
   * Actualizar {@link Asistentes}.
   *
   * @param asistentes la entidad {@link Asistentes} a actualizar.
   * @return la entidad {@link Asistentes} persistida.
   */
  Asistentes update(Asistentes asistentes);

  /**
   * Obtener todas las entidades {@link Asistentes} paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link Asistentes} paginadas y/o filtradas.
   */
  Page<Asistentes> findAll(String query, Pageable pageable);

  /**
   * Obtener todas las entidades paginadas {@link Asistentes} activas para una
   * determinada {@link ConvocatoriaReunion}.
   *
   * @param id       Id de {@link ConvocatoriaReunion}.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link Asistentes} paginadas.
   */
  Page<Asistentes> findAllByConvocatoriaReunionId(Long id, Pageable pageable);

  /**
   * Obtiene {@link Asistentes} por id.
   *
   * @param id el id de la entidad {@link Asistentes}.
   * @return la entidad {@link Asistentes}.
   */
  Asistentes findById(Long id);

  /**
   * Elimina el {@link Asistentes} por id.
   *
   * @param id el id de la entidad {@link Asistentes}.
   */
  void delete(Long id) throws AsistentesNotFoundException;

  /**
   * Elimina todos los {@link Asistentes}.
   */
  void deleteAll();

}