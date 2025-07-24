package org.crue.hercules.sgi.pii.repository;

import java.util.Optional;

import org.crue.hercules.sgi.framework.i18n.Language;
import org.crue.hercules.sgi.pii.model.ResultadoInformePatentabilidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link ResultadoInformePatentabilidad}.
 */
@Repository
public interface ResultadoInformePatentabilidadRepository extends JpaRepository<ResultadoInformePatentabilidad, Long>,
    JpaSpecificationExecutor<ResultadoInformePatentabilidad> {

  /**
   * Obtiene la entidad {@link ResultadoInformePatentabilidad} activo con el
   * nombre e idioma indicado
   *
   * @param lang   el idioma sobre el que buscar
   * @param nombre el nombre de {@link ResultadoInformePatentabilidad}.
   * @return el {@link ResultadoInformePatentabilidad} con el nombre e idioma
   *         indicado
   */
  Optional<ResultadoInformePatentabilidad> findByNombreLangAndNombreValueAndActivoIsTrue(Language lang, String nombre);
}
