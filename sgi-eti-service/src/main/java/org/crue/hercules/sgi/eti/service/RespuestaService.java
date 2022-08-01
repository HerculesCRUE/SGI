package org.crue.hercules.sgi.eti.service;

import java.util.Optional;

import org.crue.hercules.sgi.eti.exceptions.RespuestaNotFoundException;
import org.crue.hercules.sgi.eti.model.Respuesta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link Respuesta}.
 */
public interface RespuestaService {
  /**
   * Guardar {@link Respuesta}.
   *
   * @param respuesta la entidad {@link Respuesta} a guardar.
   * @return la entidad {@link Respuesta} persistida.
   */
  Respuesta create(Respuesta respuesta);

  /**
   * Actualizar {@link Respuesta}.
   *
   * @param respuesta la entidad {@link Respuesta} a actualizar.
   * @return la entidad {@link Respuesta} persistida.
   */
  Respuesta update(Respuesta respuesta);

  /**
   * Obtener todas las entidades {@link Respuesta} paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link Respuesta} paginadas y/o filtradas.
   */
  Page<Respuesta> findAll(String query, Pageable pageable);

  /**
   * Obtiene {@link Respuesta} por id.
   *
   * @param id el id de la entidad {@link Respuesta}.
   * @return la entidad {@link Respuesta}.
   */
  Respuesta findById(Long id);

  /**
   * Elimina el {@link Respuesta} por id.
   *
   * @param id el id de la entidad {@link Respuesta}.
   */
  void delete(Long id) throws RespuestaNotFoundException;

  /**
   * Elimina todos los {@link Respuesta}.
   */
  void deleteAll();

  /**
   * Obtiene la Respuesta asociada a una Memoria y Apartado
   * 
   * @param idMemoria  Identificador del Memoria
   * @param idApartado Identificaro del Apartado
   * @return Respuesta
   */
  Respuesta findByMemoriaIdAndApartadoId(Long idMemoria, Long idApartado);

  Page<Respuesta> findByMemoriaId(Long idMemoria, Pageable page);

  /**
   * Obtiene la última Respuesta de una Memoria
   * 
   * @param idMemoria Identificador de la Memoria
   * @return Respuesta
   */
  Optional<Respuesta> findLastByMemoriaId(Long idMemoria);
}