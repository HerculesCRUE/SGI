package org.crue.hercules.sgi.prc.repository;

import java.util.List;

import org.crue.hercules.sgi.prc.model.IndiceImpacto;
import org.crue.hercules.sgi.prc.repository.custom.CustomIndiceImpactoRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link IndiceImpacto}.
 */

@Repository
public interface IndiceImpactoRepository
    extends JpaRepository<IndiceImpacto, Long>, JpaSpecificationExecutor<IndiceImpacto>, CustomIndiceImpactoRepository {

  List<IndiceImpacto> findAllByProduccionCientificaId(Long produccionCientificaId);
}
