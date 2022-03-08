package org.crue.hercules.sgi.prc.repository;

import java.util.List;

import org.crue.hercules.sgi.prc.model.Proyecto;
import org.crue.hercules.sgi.prc.repository.custom.CustomProyectoRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link Proyecto}.
 */

@Repository
public interface ProyectoRepository
    extends JpaRepository<Proyecto, Long>, JpaSpecificationExecutor<Proyecto>, CustomProyectoRepository {

  List<Proyecto> findAllByProduccionCientificaId(Long produccionCientificaId);

}
