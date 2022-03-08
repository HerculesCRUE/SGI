package org.crue.hercules.sgi.prc.repository;

import java.util.List;

import org.crue.hercules.sgi.prc.model.PuntuacionItemInvestigador;
import org.crue.hercules.sgi.prc.repository.custom.CustomPuntuacionItemInvestigadorRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link PuntuacionItemInvestigador}.
 */

@Repository
public interface PuntuacionItemInvestigadorRepository
    extends JpaRepository<PuntuacionItemInvestigador, Long>, JpaSpecificationExecutor<PuntuacionItemInvestigador>,
    CustomPuntuacionItemInvestigadorRepository {

  List<PuntuacionItemInvestigador> findAllByProduccionCientificaIdAndPersonaRef(Long produccionCientificaId,
      String personaRef);

}
