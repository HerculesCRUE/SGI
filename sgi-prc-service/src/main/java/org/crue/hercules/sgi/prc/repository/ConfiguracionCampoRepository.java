package org.crue.hercules.sgi.prc.repository;

import java.util.List;
import java.util.Optional;

import org.crue.hercules.sgi.prc.enums.EpigrafeCVN;
import org.crue.hercules.sgi.prc.enums.CodigoCVN;
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

  List<ConfiguracionCampo> findByEpigrafeCVNOrderByCodigoCVN(EpigrafeCVN epigrafeCVN);

  Optional<ConfiguracionCampo> findByEpigrafeCVNAndFechaReferenciaInicioIsTrue(EpigrafeCVN epigrafeCVN);

  Optional<ConfiguracionCampo> findByEpigrafeCVNAndFechaReferenciaFinIsTrue(EpigrafeCVN epigrafeCVN);

}
