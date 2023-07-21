package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.crue.hercules.sgi.csp.model.TipoOrigenFuenteFinanciacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TipoOrigenFuenteFinanciacionRepository
    extends JpaRepository<TipoOrigenFuenteFinanciacion, Long>, JpaSpecificationExecutor<TipoOrigenFuenteFinanciacion> {

  /**
   * Busca un {@link TipoOrigenFuenteFinanciacion} activo por su nombre.
   * 
   * @param nombre Nombre del {@link TipoOrigenFuenteFinanciacion}.
   * @return un {@link TipoOrigenFuenteFinanciacion} si tiene el nombre buscado.
   */
  Optional<TipoOrigenFuenteFinanciacion> findByNombreAndActivoIsTrue(String nombre);

}
