package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.crue.hercules.sgi.csp.model.TipoHito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link TipoHito}.
 */

@Repository
public interface TipoHitoRepository extends JpaRepository<TipoHito, Long>, JpaSpecificationExecutor<TipoHito> {

  /**
   * Busca un {@link TipoHito} activo por su nombre.
   * 
   * @param nombre Nombre del {@link TipoHito}.
   * @return un {@link TipoHito} si tiene el nombre buscado.
   */
  Optional<TipoHito> findByNombreAndActivoIsTrue(String nombre);

}
