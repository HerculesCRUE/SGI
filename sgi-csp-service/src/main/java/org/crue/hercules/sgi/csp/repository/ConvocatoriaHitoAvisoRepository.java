package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.crue.hercules.sgi.csp.model.ConvocatoriaHito;
import org.crue.hercules.sgi.csp.model.ConvocatoriaHitoAviso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ConvocatoriaHitoAvisoRepository
    extends JpaRepository<ConvocatoriaHitoAviso, Long>, JpaSpecificationExecutor<ConvocatoriaHitoAviso> {

  /**
   * Obtiene un {@link ConvocatoriaHitoAviso} a partir del identificador de
   * {@link ConvocatoriaHito}
   * 
   * @param id Identificador de {@link ConvocatoriaHito}
   * @return ConvocatoriaHitoAviso
   */
  Optional<ConvocatoriaHitoAviso> findByConvocatoriaHitoId(Long id);
}
