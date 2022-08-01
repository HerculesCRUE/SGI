package org.crue.hercules.sgi.eer.repository.custom;

import java.util.List;

import org.crue.hercules.sgi.eer.model.Empresa;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

/**
 * Custom repository para {@link Empresa}.
 */
@Component
public interface CustomEmpresaRepository {

  /**
   * Obtiene los ids de {@link Empresa} que cumplen con la specification recibida.
   * 
   * @param specification condiciones que deben cumplir.
   * @return lista de ids de {@link Empresa}.
   */
  List<Long> findIds(Specification<Empresa> specification);

}
