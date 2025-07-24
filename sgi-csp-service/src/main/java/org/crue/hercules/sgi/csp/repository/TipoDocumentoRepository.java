package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.crue.hercules.sgi.csp.model.TipoDocumento;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link TipoDocumento}.
 */
@Repository
public interface TipoDocumentoRepository
    extends JpaRepository<TipoDocumento, Long>, JpaSpecificationExecutor<TipoDocumento> {

  /**
   * Obtiene la entidad {@link TipoDocumento} activa con el nombre e idioma
   * indicados
   *
   * @param lang   el language sobre el que buscar
   * @param nombre el nombre del {@link TipoDocumento}.
   * @return el {@link TipoDocumento} activo con el nombre indicado
   */

  Optional<TipoDocumento> findByNombreLangAndNombreValueAndActivoIsTrue(Language lang, String nombre);

}