package org.crue.hercules.sgi.prc.repository;

import java.util.List;
import java.util.Optional;

import org.crue.hercules.sgi.prc.model.PuntuacionGrupo;
import org.crue.hercules.sgi.prc.repository.custom.CustomPuntuacionGrupoRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link PuntuacionGrupo}.
 */

@Repository
public interface PuntuacionGrupoRepository
    extends JpaRepository<PuntuacionGrupo, Long>, JpaSpecificationExecutor<PuntuacionGrupo>,
    CustomPuntuacionGrupoRepository {

  List<PuntuacionGrupo> findByConvocatoriaBaremacionId(Long convocatoriaBaremacionId);

  Optional<PuntuacionGrupo> findByConvocatoriaBaremacionIdAndGrupoRef(Long convocatoriaBaremacionId, Long grupoRef);

}
