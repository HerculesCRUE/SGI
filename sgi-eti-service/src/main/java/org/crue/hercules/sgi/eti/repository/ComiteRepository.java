package org.crue.hercules.sgi.eti.repository;

import java.util.Optional;

import org.crue.hercules.sgi.eti.model.Comite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link Comite}.
 */

@Repository
public interface ComiteRepository extends JpaRepository<Comite, Long>, JpaSpecificationExecutor<Comite> {

  /**
   * Recupera un comité activo por identificador.
   * 
   * @param idComite Identificadro {@link Comite}
   * @return {@link Comite}
   */
  Optional<Comite> findByIdAndActivoTrue(Long idComite);

}