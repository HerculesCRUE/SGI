package org.crue.hercules.sgi.csp.repository.custom;

import java.util.List;

import org.crue.hercules.sgi.csp.dto.AutorizacionWithFirstEstado;
import org.crue.hercules.sgi.csp.model.Autorizacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

/**
 * Custom repository para {@link Autorizacion}.
 */

@Component
public interface CustomAutorizacionRepository {

  /**
   * Obtiene los ids de autorizaciones que cumplen con la specification
   * recibida.
   * 
   * @param specification condiciones que deben cumplir.
   * @return lista de ids de {@link Autorizacion}.
   */
  List<Long> findIds(Specification<Autorizacion> specification);

  /**
   * Obtiene las de autorizaciones que cumplen con la specification
   * recibida, con su fecha de primer estado.
   * 
   * @param specification condiciones que deben cumplir.
   * @param pageable      paginación.
   * @return lista de ids de {@link Autorizacion}.
   */
  Page<AutorizacionWithFirstEstado> findAllAutorizacionWithFirstEstado(Specification<Autorizacion> specification,
      Pageable pageable);
}
