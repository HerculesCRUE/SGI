package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.crue.hercules.sgi.csp.model.AlegacionRequerimiento;
import org.crue.hercules.sgi.csp.model.RequerimientoJustificacion;
import org.crue.hercules.sgi.csp.repository.custom.CustomAlegacionRequerimientoRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AlegacionRequerimientoRepository extends JpaRepository<AlegacionRequerimiento, Long>,
    JpaSpecificationExecutor<AlegacionRequerimiento>,
    CustomAlegacionRequerimientoRepository {

  /**
   * Obtiene el {@link AlegacionRequerimiento} de un
   * {@link RequerimientoJustificacion}.
   * 
   * @param id identificador de la {@link RequerimientoJustificacion}
   * @return la entidad {@link AlegacionRequerimiento}
   */
  Optional<AlegacionRequerimiento> findByRequerimientoJustificacionId(Long id);
}
