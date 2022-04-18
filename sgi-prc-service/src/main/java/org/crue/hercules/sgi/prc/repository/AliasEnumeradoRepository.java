package org.crue.hercules.sgi.prc.repository;

import java.util.Optional;

import org.crue.hercules.sgi.prc.enums.CodigoCVN;
import org.crue.hercules.sgi.prc.model.AliasEnumerado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link AliasEnumerado}.
 */

@Repository
public interface AliasEnumeradoRepository
    extends JpaRepository<AliasEnumerado, Long>, JpaSpecificationExecutor<AliasEnumerado> {

  Optional<AliasEnumerado> findByCodigoCVN(CodigoCVN codigoCVN);
}
