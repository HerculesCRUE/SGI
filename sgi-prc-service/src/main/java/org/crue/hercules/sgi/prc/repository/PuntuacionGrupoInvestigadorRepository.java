package org.crue.hercules.sgi.prc.repository;

import java.util.List;
import java.util.Optional;

import org.crue.hercules.sgi.prc.model.PuntuacionGrupoInvestigador;
import org.crue.hercules.sgi.prc.repository.custom.CustomPuntuacionGrupoInvestigadorRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link PuntuacionGrupoInvestigador}.
 */

@Repository
public interface PuntuacionGrupoInvestigadorRepository
    extends JpaRepository<PuntuacionGrupoInvestigador, Long>, JpaSpecificationExecutor<PuntuacionGrupoInvestigador>,
    CustomPuntuacionGrupoInvestigadorRepository {

  Optional<PuntuacionGrupoInvestigador> findAllByPuntuacionGrupoIdAndPuntuacionItemInvestigadorId(
      Long puntuacionGrupoId, Long puntuacionItemInvestigadorId);

  List<PuntuacionGrupoInvestigador> findAllByPuntuacionGrupoId(Long puntuacionGrupoId);

  List<PuntuacionGrupoInvestigador> findAllByPuntuacionGrupoConvocatoriaBaremacionId(Long convocatoriaBaremacionId);
}
