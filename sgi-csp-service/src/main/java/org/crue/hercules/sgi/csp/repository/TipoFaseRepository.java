package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.crue.hercules.sgi.csp.model.TipoFase;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link TipoFase}.
 */

@Repository
public interface TipoFaseRepository extends JpaRepository<TipoFase, Long>, JpaSpecificationExecutor<TipoFase> {

  /**
   * Obtiene la entidad {@link TipoFase} activa con el nombre e idioma
   * indicados
   *
   * @param lang   el language sobre el que buscar
   * @param nombre el nombre del {@link TipoFase}.
   * @return el {@link TipoFase} activo con el nombre indicado
   */
  Optional<TipoFase> findByNombreLangAndNombreValueAndActivoIsTrue(Language lang, String nombre);

}
