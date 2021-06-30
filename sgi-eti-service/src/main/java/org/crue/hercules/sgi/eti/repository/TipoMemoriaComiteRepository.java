package org.crue.hercules.sgi.eti.repository;

import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.TipoMemoriaComite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link TipoMemoriaComite}.
 */

@Repository
public interface TipoMemoriaComiteRepository
    extends JpaRepository<TipoMemoriaComite, Long>, JpaSpecificationExecutor<TipoMemoriaComite> {

  /**
   * Recupera una lista paginada de los tipos de memoria comité asociados al
   * comité recibido por parámetro (debe estar activo).
   * 
   * @param idComite Identificador de {@link Comite}.
   * @param paging   Datos de la paginación.
   * @return lista paginada de los tipos de memoria.
   */
  Page<TipoMemoriaComite> findByComiteIdAndComiteActivoTrue(Long idComite, Pageable paging);

}