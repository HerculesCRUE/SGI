package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.crue.hercules.sgi.csp.model.TipoFase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link TipoFase}.
 */

@Repository
public interface TipoFaseRepository extends JpaRepository<TipoFase, Long>, JpaSpecificationExecutor<TipoFase> {

  /**
   * Busca un {@link TipoFase} activo por nombre.
   * 
   * @param nombre Nombre del {@link TipoFase}.
   * @return un {@link TipoFase} si tiene el nombre buscado.
   */
  Optional<TipoFase> findByNombreAndActivoIsTrue(String nombre);

}
