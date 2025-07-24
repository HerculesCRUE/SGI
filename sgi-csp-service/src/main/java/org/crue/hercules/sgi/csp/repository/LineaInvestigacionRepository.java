package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.crue.hercules.sgi.csp.model.LineaInvestigacion;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link LineaInvestigacion}.
 */
@Repository
public interface LineaInvestigacionRepository
    extends JpaRepository<LineaInvestigacion, Long>, JpaSpecificationExecutor<LineaInvestigacion> {

  /**
   * Obtiene la entidad {@link LineaInvestigacion} activa con el nombre e idioma
   * indicados
   *
   * @param lang   el language sobre el que buscar
   * @param nombre el nombre de {@link LineaInvestigacion}.
   * @return el {@link LineaInvestigacion} con el nombre indicado
   */
  Optional<LineaInvestigacion> findByNombreLangAndNombreValueAndActivoIsTrue(Language lang, String nombre);
}
