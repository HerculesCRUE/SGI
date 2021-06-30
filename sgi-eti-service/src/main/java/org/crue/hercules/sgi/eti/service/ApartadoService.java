package org.crue.hercules.sgi.eti.service;

import org.crue.hercules.sgi.eti.exceptions.ApartadoNotFoundException;
import org.crue.hercules.sgi.eti.model.Apartado;
import org.crue.hercules.sgi.eti.model.Bloque;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link Apartado}.
 */
public interface ApartadoService {

  /**
   * Obtiene las entidades {@link Apartado} filtradas y paginadas según los
   * criterios de búsqueda.
   *
   * @param query  filtro de búsqueda.
   * @param paging pageable
   * @return el listado de entidades {@link Apartado} paginadas y filtradas.
   */
  Page<Apartado> findAll(String query, Pageable paging);

  /**
   * Obtiene {@link Apartado} por id.
   *
   * @param id El id de la entidad {@link Apartado}.
   * @return La entidad {@link Apartado}.
   * @throws ApartadoNotFoundException Si no existe ninguna entidad
   *                                   {@link Apartado} con ese id.
   * @throws IllegalArgumentException  Si no se informa Id.
   */
  Apartado findById(Long id) throws ApartadoNotFoundException, IllegalArgumentException;

  /**
   * Obtiene las entidades {@link Apartado} filtradas y paginadas según por el id
   * de su {@link Bloque}. Solamente se devuelven los Apartados de primer nivel
   * (sin padre).
   *
   * @param id       id del {@link Bloque}.
   * @param pageable pageable
   * @return el listado de entidades {@link Apartado} paginadas y filtradas.
   */
  Page<Apartado> findByBloqueId(Long id, Pageable pageable);

  /**
   * Obtiene las entidades {@link Apartado} filtradas y paginadas según por el id
   * de su padre {@link Apartado}.
   *
   * @param id       id del {@link Apartado} padre.
   * @param pageable pageable
   * @return el listado de entidades {@link Apartado} paginadas y filtradas.
   */
  Page<Apartado> findByPadreId(Long id, Pageable pageable);
}
