package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.crue.hercules.sgi.csp.model.TipoHito;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link TipoHito}.
 */

@Repository
public interface TipoHitoRepository extends JpaRepository<TipoHito, Long>, JpaSpecificationExecutor<TipoHito> {

  /**
   * Obtiene la entidad {@link TipoHito} activa con el nombre e idioma
   * indicados
   *
   * @param lang   el language sobre el que buscar
   * @param nombre el nombre del {@link TipoHito}.
   * @return el {@link TipoHito} activo con el nombre indicado
   */
  Optional<TipoHito> findByNombreLangAndNombreValueAndActivoIsTrue(Language lang, String nombre);

}
