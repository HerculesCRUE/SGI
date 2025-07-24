package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TipoFinalidadRepository
    extends JpaRepository<TipoFinalidad, Long>, JpaSpecificationExecutor<TipoFinalidad> {

  /**
   * Obtiene la entidad {@link TipoFinalidad} activa con el nombre e idioma
   * indicados
   *
   * @param lang   el language sobre el que buscar
   * @param nombre el nombre del {@link TipoFinalidad}.
   * @return el {@link TipoFinalidad} activo con el nombre indicado
   */
  Optional<TipoFinalidad> findByNombreLangAndNombreValueAndActivoIsTrue(Language lang, String nombre);

}
