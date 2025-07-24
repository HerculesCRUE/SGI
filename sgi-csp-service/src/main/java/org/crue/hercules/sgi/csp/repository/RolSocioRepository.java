package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.crue.hercules.sgi.csp.model.RolSocio;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RolSocioRepository extends JpaRepository<RolSocio, Long>, JpaSpecificationExecutor<RolSocio> {

  /**
   * Obtiene la entidad {@link RolSocio} activa con la abreviatura indicada y el lang
   *
   * @param abreviatura la abreviatura de {@link RolSocio}.
   * @param lang la language de {@link RolSocio}.
   * @return el {@link RolSocio} activo con la abreviatura indicada
   */
  Optional<RolSocio> findByAbreviaturaLangAndAbreviaturaValueAndActivoIsTrue(Language lang, String abreviatura);

  /**
   * Obtiene la entidad {@link RolSocio} activa con la abreviatura indicada
   *
   * @param abreviatura la abreviatura de {@link RolSocio}.
   * @return el {@link RolSocio} activo con la abreviatura indicada
   */
  Optional<RolSocio> findByAbreviaturaValueAndActivoIsTrue(String abreviatura);
  
  /**
   * Obtiene la entidad {@link RolSocio} activa con el nombre indicado y el lang
   *
   * @param nombre el nombre de {@link RolSocio}.
   * @param lang el language de {@link RolSocio}.
   * @return el {@link RolSocio} activo con el nombre indicado
   */
  Optional<RolSocio> findByNombreLangAndNombreValueAndActivoIsTrue(Language lang, String nombre);
  
  /**
   * Obtiene la entidad {@link RolSocio} activa con el nombre indicado
   *
   * @param nombre el nombre de {@link RolSocio}.
   * @return el {@link RolSocio} activo con el nombre indicado
   */
  Optional<RolSocio> findByNombreValueAndActivoIsTrue(String nombre);

}
