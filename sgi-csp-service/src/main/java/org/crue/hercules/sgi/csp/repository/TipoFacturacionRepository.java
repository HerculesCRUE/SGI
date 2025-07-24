package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.crue.hercules.sgi.csp.model.TipoFacturacion;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TipoFacturacionRepository
    extends JpaRepository<TipoFacturacion, Long>, JpaSpecificationExecutor<TipoFacturacion> {

  /**
   * Obtiene la entidad {@link TipoFacturacion} activo con el nombre
   * indicado
   *
   * @param nombre el nombre de {@link TipoFacturacion}.
   * @param lang el language de {@link TipoFacturacion}.
   * @return el {@link TipoFacturacion} con el nombre
   *         indicado
   */
  Optional<TipoFacturacion> findByNombreLangAndNombreValueAndActivoIsTrue(Language lang, String nombre);

}
