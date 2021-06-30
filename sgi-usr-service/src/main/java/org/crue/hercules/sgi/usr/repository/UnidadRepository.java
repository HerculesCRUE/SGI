package org.crue.hercules.sgi.usr.repository;

import java.util.Optional;

import org.crue.hercules.sgi.usr.model.Unidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link Unidad}.
 */
@Repository
public interface UnidadRepository
    extends JpaRepository<Unidad, Long>, JpaSpecificationExecutor<Unidad> {

  /**
   * Obtiene la entidad {@link Unidad} con el nombre indicado
   *
   * @param nombre el nombre de {@link Unidad}.
   * @return el {@link Unidad} con el nombre indicado
   */
  Optional<Unidad> findByNombre(String nombre);


 /**
   * Obtiene la entidad {@link Unidad} con el acronimo indicado
   *
   * @param acronimo el acronimo de {@link Unidad}.
   * @return el {@link Unidad} con el acronimo indicado
   */
  Optional<Unidad> findByAcronimo(String acronimo);

}
