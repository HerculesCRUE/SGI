package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.crue.hercules.sgi.csp.model.ConvocatoriaFase;
import org.crue.hercules.sgi.csp.model.ConvocatoriaFaseAviso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ConvocatoriaFaseAvisoRepository
    extends JpaRepository<ConvocatoriaFaseAviso, Long>, JpaSpecificationExecutor<ConvocatoriaFaseAviso> {

  /**
   * Obtiene un {@link ConvocatoriaFaseAviso} a partir del identificador de
   * {@link ConvocatoriaFase}
   * 
   * @param id Identificador de {@link ConvocatoriaFase}
   * @return ConvocatoriaFaseAviso
   */
  Optional<ConvocatoriaFaseAviso> findByConvocatoriaFaseId(Long id);
}
