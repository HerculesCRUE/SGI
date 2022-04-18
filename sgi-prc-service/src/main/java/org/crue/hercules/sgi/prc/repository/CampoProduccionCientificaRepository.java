package org.crue.hercules.sgi.prc.repository;

import java.util.List;
import java.util.Optional;

import org.crue.hercules.sgi.prc.enums.CodigoCVN;
import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica;
import org.crue.hercules.sgi.prc.repository.custom.CustomCampoProduccionCientificaRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link CampoProduccionCientifica}.
 */

@Repository
public interface CampoProduccionCientificaRepository
    extends JpaRepository<CampoProduccionCientifica, Long>, JpaSpecificationExecutor<CampoProduccionCientifica>,
    CustomCampoProduccionCientificaRepository {

  Optional<CampoProduccionCientifica> findByCodigoCVNAndProduccionCientificaId(CodigoCVN codigoCVN,
      Long produccionCientificaId);

  List<CampoProduccionCientifica> findAllByProduccionCientificaId(Long produccionCientificaId);
}
