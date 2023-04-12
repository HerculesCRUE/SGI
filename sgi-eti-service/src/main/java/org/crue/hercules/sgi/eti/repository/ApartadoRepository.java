package org.crue.hercules.sgi.eti.repository;

import org.crue.hercules.sgi.eti.model.Apartado;
import org.crue.hercules.sgi.eti.model.Bloque;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link Apartado}.
 */

@Repository
public interface ApartadoRepository extends JpaRepository<Apartado, Long>, JpaSpecificationExecutor<Apartado> {

  /**
   * Obtiene las entidades {@link Apartado} filtradas y paginadas según por el id
   * de su {@link Bloque}. Solamente se devuelven los Apartados de primer nivel
   * (sin padre).
   *
   * @param id       id del {@link Bloque}.
   * @param pageable pageable
   * @return el listado de entidades {@link Apartado} paginadas y filtradas.
   */
  Page<Apartado> findByBloqueIdAndPadreIsNull(Long id, Pageable pageable);

  /**
   * Obtiene las entidades {@link Apartado} filtradas y paginadas según por el id
   * de su padre {@link Apartado}.
   *
   * @param id       id del {@link Apartado}.
   * @param pageable pageable
   * @return el listado de entidades {@link Apartado} paginadas y filtradas.
   */
  Page<Apartado> findByPadreId(Long id, Pageable pageable);

}