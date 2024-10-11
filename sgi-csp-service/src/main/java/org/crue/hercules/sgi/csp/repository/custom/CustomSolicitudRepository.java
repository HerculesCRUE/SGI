package org.crue.hercules.sgi.csp.repository.custom;

import java.util.List;

import org.crue.hercules.sgi.csp.model.Solicitud;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

/**
 * Custom repository para {@link Solicitud}.
 */
@Component
public interface CustomSolicitudRepository {

  /**
   * Obtiene los ids de {@link Solicitud} que cumplen con la specification
   * recibida.
   * 
   * @param specification condiciones que deben cumplir.
   * @return lista de ids de {@link Solicitud}.
   */
  List<Long> findIds(Specification<Solicitud> specification);

}
