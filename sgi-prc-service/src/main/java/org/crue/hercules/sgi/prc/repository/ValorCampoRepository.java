package org.crue.hercules.sgi.prc.repository;

import java.util.List;

import org.crue.hercules.sgi.prc.model.ValorCampo;
import org.crue.hercules.sgi.prc.repository.custom.CustomValorCampoRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link ValorCampo}.
 */

@Repository
public interface ValorCampoRepository
    extends JpaRepository<ValorCampo, Long>, JpaSpecificationExecutor<ValorCampo>, CustomValorCampoRepository {

  List<ValorCampo> findAllByCampoProduccionCientificaId(Long campoProduccionCientificaId);
}
