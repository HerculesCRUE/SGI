package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.crue.hercules.sgi.csp.model.TipoEnlace;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TipoEnlaceRepository extends JpaRepository<TipoEnlace, Long>, JpaSpecificationExecutor<TipoEnlace> {

  /**
   * Obtiene la entidad {@link TipoEnlace} activa con el nombre indicado y el lang
   *
   * @param nombre el nombre de {@link TipoEnlace}.
   * @param lang   el land de {@link TipoEnlace}.
   * @return el {@link TipoEnlace} con el nombre indicado
   */
  Optional<TipoEnlace> findByNombreLangAndNombreValueAndActivoIsTrue(Language lang, String nombre);
}
