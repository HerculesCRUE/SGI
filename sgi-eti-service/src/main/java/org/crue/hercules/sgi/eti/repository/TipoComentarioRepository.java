package org.crue.hercules.sgi.eti.repository;

import org.crue.hercules.sgi.eti.model.TipoComentario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link TipoComentario}.
 */

@Repository
public interface TipoComentarioRepository
    extends JpaRepository<TipoComentario, Long>, JpaSpecificationExecutor<TipoComentario> {

}