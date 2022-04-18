package org.crue.hercules.sgi.prc.repository;

import java.util.List;
import java.util.Optional;

import org.crue.hercules.sgi.prc.model.Modulador;
import org.crue.hercules.sgi.prc.model.Modulador.TipoModulador;
import org.crue.hercules.sgi.prc.repository.custom.CustomModuladorRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link Modulador}.
 */

@Repository
public interface ModuladorRepository
    extends JpaRepository<Modulador, Long>, JpaSpecificationExecutor<Modulador>, CustomModuladorRepository {

  List<Modulador> findByConvocatoriaBaremacionId(Long convocatoriaBaremacionId);

  Optional<Modulador> findByConvocatoriaBaremacionIdAndAreaRefAndTipo(Long convocatoriaBaremacionId,
      String areaRef, TipoModulador tipo);
}
