package org.crue.hercules.sgi.prc.repository;

import java.util.Optional;

import org.crue.hercules.sgi.prc.enums.TipoFuenteImpacto;
import org.crue.hercules.sgi.prc.model.TipoFuenteImpactoCuartil;
import org.crue.hercules.sgi.prc.model.TipoFuenteImpactoCuartil.Cuartil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link TipoFuenteImpactoCuartil}.
 */

@Repository
public interface TipoFuenteImpactoCuartilRepository
    extends JpaRepository<TipoFuenteImpactoCuartil, Long>, JpaSpecificationExecutor<TipoFuenteImpactoCuartil> {

  Boolean existsByFuenteImpactoAndAnioAndCuartil(TipoFuenteImpacto fuenteImpacto, Integer anio,
      Cuartil cuartil);

  Optional<TipoFuenteImpactoCuartil> findByFuenteImpactoAndAnio(TipoFuenteImpacto fuenteImpacto, Integer anio);
}
