package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.crue.hercules.sgi.csp.model.TipoDocumento;
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
   * Busca un {@link TipoDocumento} activo por su nombre.
   * 
   * @param nombre Nombre del {@link TipoDocumento}.
   * @return un {@link TipoDocumento} si tiene el nombre buscado.
   */
  Optional<TipoDocumento> findByNombreAndActivoIsTrue(String nombre);

}