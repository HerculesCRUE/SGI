package org.crue.hercules.sgi.eti.repository;

import org.crue.hercules.sgi.eti.model.TipoMemoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link TipoMemoria}.
 */

@Repository
public interface TipoMemoriaRepository extends JpaRepository<TipoMemoria, Long>, JpaSpecificationExecutor<TipoMemoria> {

}