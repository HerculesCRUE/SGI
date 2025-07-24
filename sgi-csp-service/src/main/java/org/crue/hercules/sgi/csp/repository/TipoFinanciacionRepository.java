package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.crue.hercules.sgi.csp.model.TipoFinanciacion;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link TipoFinanciacion}.
 */
@Repository
public interface TipoFinanciacionRepository
    extends JpaRepository<TipoFinanciacion, Long>, JpaSpecificationExecutor<TipoFinanciacion> {

  /**
   * Busca un {@link TipoFinanciacion} por su nombre.
   * 
   * @param nombre Nombre del {@link TipoFinanciacion}.
   * @param lang   Language del {@link TipoFinanciacion}.
   * @return un {@link TipoFinanciacion} si tiene el nombre buscado.
   */
  Optional<TipoFinanciacion> findByNombreLangAndNombreValueAndActivoIsTrue(Language lang, String nombre);

}
