package org.crue.hercules.sgi.prc.repository;

import java.util.List;

import org.crue.hercules.sgi.prc.model.Acreditacion;
import org.crue.hercules.sgi.prc.repository.custom.CustomAcreditacionRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link Acreditacion}.
 */

@Repository
public interface AcreditacionRepository
    extends JpaRepository<Acreditacion, Long>, JpaSpecificationExecutor<Acreditacion>, CustomAcreditacionRepository {

  List<Acreditacion> findAllByProduccionCientificaId(Long produccionCientificaId);
}
