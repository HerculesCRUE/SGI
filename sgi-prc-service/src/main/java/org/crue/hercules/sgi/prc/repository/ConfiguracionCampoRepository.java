package org.crue.hercules.sgi.prc.repository;

import java.util.Optional;

import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica.CodigoCVN;
import org.crue.hercules.sgi.prc.model.ConfiguracionCampo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link ConfiguracionCampo}.
 */

@Repository
public interface ConfiguracionCampoRepository
    extends JpaRepository<ConfiguracionCampo, Long>, JpaSpecificationExecutor<ConfiguracionCampo> {

  Optional<ConfiguracionCampo> findByCodigoCVN(CodigoCVN codigoCVN);

}
